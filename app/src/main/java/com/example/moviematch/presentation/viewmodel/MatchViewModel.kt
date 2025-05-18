package com.example.moviematch.presentation.viewmodel

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviematch.R
import com.example.moviematch.data.db.entity.Contact
import com.example.moviematch.data.db.entity.MoviePreference
import com.example.moviematch.domain.repository.ContactRepository
import com.example.moviematch.domain.repository.MoviePreferenceRepository
import com.example.moviematch.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for handling logic related to movie match finding between users
 * based on their saved preferences, moods (genres), and contact relationships.
 */
class MatchViewModel(
    private val userId: String,
    private val movieRepo: MoviePreferenceRepository,
    private val contactRepo: ContactRepository,
    private val userRepo: UserRepository
) : ViewModel() {

    /** Input field state for entering a user ID to match with */
    val inputUserId = MutableStateFlow("")

    /** List of all contacts for the current user */
    val contacts = MutableStateFlow<List<Contact>>(emptyList())

    /** Result of a match: either a direct match (user ID and shared movies) or group match (null and shared movies) */
    val matchResult = MutableStateFlow<Pair<String?, List<String>>?>(null)

    /** List of genres available in the current user's preferences */
    val availableGenres = MutableStateFlow<List<String>>(emptyList())

    /** Genres currently selected as the user's mood or preference */
    val selectedGenres = MutableStateFlow<List<String>>(emptyList())

    /** Emits UI message resource IDs to be displayed as simple Toasts */
    private val _uiMessage = MutableSharedFlow<Int>()
    val uiMessage: SharedFlow<Int> = _uiMessage

    /** Emits messages with string formatting arguments to be resolved in the UI */
    private val _uiTextMessage = MutableSharedFlow<Pair<Int, List<String>>>()
    val uiTextMessage: SharedFlow<Pair<Int, List<String>>> = _uiTextMessage

    init {
        loadContacts()
    }

    /**
     * Loads the list of distinct genres from the current user's movie preferences.
     */
    fun loadGenres() {
        viewModelScope.launch {
            val prefs = movieRepo.getMoviesForUser(userId)
            val genres = prefs.map { it.genre }.distinct().sorted()
            availableGenres.value = genres
        }
    }

    /**
     * Loads all contacts belonging to the current user.
     */
    private fun loadContacts() {
        viewModelScope.launch {
            contacts.value = contactRepo.getContactsForUser(userId)
        }
    }

    /**
     * Finds a direct movie match between the current user and another user based on preferences and moods.
     * Shows UI messages in case of input errors or lack of compatibility.
     */
    fun findDirectMatch(context: Context) {
        viewModelScope.launch {
            val targetId = inputUserId.value.trim()
            if (targetId.isBlank() || targetId.length != 6) {
                emitUiMessage(R.string.invalid_user_id_length)
                return@launch
            }

            if (targetId == userId) {
                emitUiMessage(R.string.cannot_match_self)
                return@launch
            }

            val userExists = userRepo.getUserById(targetId) != null
            if (!userExists) {
                emitUiMessage(R.string.not_found)
                return@launch
            }

            val myPrefs = movieRepo.getMoviesForUser(userId)
            val otherPrefs = movieRepo.getMoviesForUser(targetId)

            if (myPrefs.isEmpty()) {
                emitUiMessage(R.string.no_preferences)
                return@launch
            }

            if (otherPrefs.isEmpty()) {
                emitUiMessage(R.string.other_user_no_preferences_generic)
                return@launch
            }

            val myMood = selectedGenres.value
            val otherMood = loadSelectedGenresForUser(context, targetId)

            val moodConflict = myMood.isNotEmpty() && otherMood.isNotEmpty() &&
                    myMood.intersect(otherMood.toSet()).isEmpty()
            if (moodConflict) {
                emitUiMessage(R.string.no_genre_match)
                return@launch
            }

            val finalGenres = when {
                myMood.isNotEmpty() && otherMood.isNotEmpty() -> myMood.intersect(otherMood.toSet()).toList()
                myMood.isNotEmpty() -> myMood
                otherMood.isNotEmpty() -> otherMood
                else -> emptyList()
            }

            val filteredMine = filterByGenres(myPrefs, finalGenres)
            val filteredOther = filterByGenres(otherPrefs, finalGenres)

            val common = findCommonMovies(filteredMine, filteredOther)

            if (common.isNotEmpty()) {
                matchResult.value = targetId to common
            } else {
                emitUiMessage(R.string.no_common_movies_generic)
            }
        }
    }

    /**
     * Attempts to find shared movie preferences across a selected group of contacts.
     * Filters by selected genres and reports missing data or conflicts.
     *
     * @param selected List of contact IDs to match against.
     */
    fun findGroupMatch(selected: List<String>) {
        viewModelScope.launch {
            val myPrefs = movieRepo.getMoviesForUser(userId)
            if (myPrefs.isEmpty()) {
                emitUiMessage(R.string.no_preferences)
                return@launch
            }

            val filteredMine = filterByGenres(myPrefs, selectedGenres.value)
            val myMovies = filteredMine.map { it.movie }.toSet()
            var common = myMovies

            val contactsWithoutPrefs = mutableListOf<String>()
            var hasAtLeastOneWithPrefs = false

            for (id in selected) {
                val prefs = movieRepo.getMoviesForUser(id)
                val filtered = filterByGenres(prefs, selectedGenres.value)

                if (prefs.isEmpty()) {
                    val name = contacts.value.firstOrNull { it.contactId == id }?.displayName ?: id
                    contactsWithoutPrefs.add(name)
                } else if (filtered.isEmpty()) {
                    val name = contacts.value.firstOrNull { it.contactId == id }?.displayName ?: id
                    emitUiTextMessage(R.string.other_user_no_preferences_named, name)
                } else {
                    hasAtLeastOneWithPrefs = true
                    common = common.intersect(filtered.map { it.movie }.toSet())
                }
            }

            if (contactsWithoutPrefs.isNotEmpty()) {
                val names = contactsWithoutPrefs.joinToString(", ")
                emitUiTextMessage(R.string.other_user_no_preferences_named, names)
                return@launch
            }

            if (!hasAtLeastOneWithPrefs) {
                emitUiMessage(R.string.no_contacts_have_preferences)
                return@launch
            }

            if (common.isNotEmpty()) {
                matchResult.value = null to common.sorted()
            } else {
                emitUiMessage(R.string.no_group_match)
            }
        }
    }

    /**
     * Adds a contact to the user's contact list if it doesn't already exist.
     *
     * @param contactId The ID of the user to be added as a contact.
     */
    fun addToContacts(contactId: String) {
        viewModelScope.launch {
            val exists = contactRepo.getContactsForUser(userId).any { it.contactId == contactId }

            if (exists) {
                emitUiMessage(R.string.contact_exists)
            } else {
                contactRepo.addContact(Contact(ownerId = userId, contactId = contactId))
                emitUiMessage(R.string.added_to_contacts)
                loadContacts()
            }
        }
    }

    /**
     * Resets the current match result to null.
     */
    fun resetMatchResult() {
        matchResult.value = null
    }

    /**
     * Filters a list of movie preferences by selected genres.
     *
     * @param prefs List of movie preferences to filter.
     * @param genres Genres to filter by.
     * @param respectGenres Whether to apply genre filtering or return all.
     * @return Filtered list of preferences.
     */
    private fun filterByGenres(
        prefs: List<MoviePreference>,
        genres: List<String>,
        respectGenres: Boolean = true
    ): List<MoviePreference> {
        return if (!respectGenres || genres.isEmpty()) prefs
        else prefs.filter { it.genre in genres }
    }

    /**
     * Returns a sorted list of common movies between two users.
     */
    private fun findCommonMovies(prefs1: List<MoviePreference>, prefs2: List<MoviePreference>): List<String> {
        val set1 = prefs1.map { it.movie }.toSet()
        val set2 = prefs2.map { it.movie }.toSet()
        return set1.intersect(set2).sorted()
    }

    /**
     * Emits a simple UI message via resource ID.
     */
    private fun emitUiMessage(@StringRes resId: Int) {
        viewModelScope.launch {
            _uiMessage.emit(resId)
        }
    }

    /**
     * Emits a UI message with string arguments.
     */
    private fun emitUiTextMessage(@StringRes resId: Int, vararg args: String) {
        viewModelScope.launch {
            _uiTextMessage.emit(resId to args.toList())
        }
    }

    /**
     * Saves selected genres (user mood) into SharedPreferences.
     */
    fun saveSelectedGenres(context: Context) {
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit().apply {
            putStringSet("selected_genres_$userId", selectedGenres.value.toSet())
            apply()
        }
    }

    /**
     * Loads the current user's previously selected genres from SharedPreferences.
     */
    fun loadSelectedGenres(context: Context) {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val saved = prefs.getStringSet("selected_genres_$userId", emptySet())
        selectedGenres.value = saved?.toList() ?: emptyList()
    }

    /**
     * Loads another user's selected genres from SharedPreferences.
     */
    private fun loadSelectedGenresForUser(context: Context, targetUserId: String): List<String> {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val saved = prefs.getStringSet("selected_genres_$targetUserId", emptySet())
        return saved?.toList() ?: emptyList()
    }
}
