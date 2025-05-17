package com.example.moviematch.presentation.viewmodel

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
import android.content.Context
import kotlinx.coroutines.launch

class MatchViewModel(
    private val userId: String,
    private val movieRepo: MoviePreferenceRepository,
    private val contactRepo: ContactRepository,
    private val userRepo: UserRepository
) : ViewModel() {

    val inputUserId = MutableStateFlow("")
    val contacts = MutableStateFlow<List<Contact>>(emptyList())
    val matchResult = MutableStateFlow<Pair<String?, List<String>>?>(null)

    val availableGenres = MutableStateFlow<List<String>>(emptyList())
    val selectedGenres = MutableStateFlow<List<String>>(emptyList())


    private val _uiMessage = MutableSharedFlow<Int>()
    val uiMessage: SharedFlow<Int> = _uiMessage

    private val _uiTextMessage = MutableSharedFlow<Pair<Int, List<String>>>()
    val uiTextMessage: SharedFlow<Pair<Int, List<String>>> = _uiTextMessage

    init {
        loadContacts()
    }

    fun loadGenres() {
        viewModelScope.launch {
            val prefs = movieRepo.getMoviesForUser(userId)
            val genres = prefs.map { it.genre }.distinct().sorted()
            availableGenres.value = genres
        }
    }

    private fun loadContacts() {
        viewModelScope.launch {
            contacts.value = contactRepo.getContactsForUser(userId)
        }
    }

    fun findDirectMatch(context: Context) {
        viewModelScope.launch {
            val targetId = inputUserId.value
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
            val myHasMood = myMood.isNotEmpty()

            val otherMood = loadSelectedGenresForUser(context, targetId)
            val otherHasMood = otherMood.isNotEmpty()

            val iHaveMood = myMood.isNotEmpty()
            val heHasMood = otherMood.isNotEmpty()

            val moodsConflict = iHaveMood && heHasMood && myMood.intersect(otherMood).isEmpty()
            if (moodsConflict) {
                emitUiMessage(R.string.no_genre_match)
                return@launch
            }

            val finalGenres = when {
                myHasMood && otherHasMood -> myMood.intersect(otherMood).toList()
                myHasMood -> myMood
                otherHasMood -> otherMood
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
                    val contactName = contacts.value
                        .firstOrNull { it.contactId == id }
                        ?.displayName
                        ?.takeIf { it.isNotBlank() } ?: id
                    contactsWithoutPrefs.add(contactName)
                } else if (filtered.isEmpty()) {
                    val contactName = contacts.value
                        .firstOrNull { it.contactId == id }
                        ?.displayName
                        ?.takeIf { it.isNotBlank() } ?: id
                    val names = contactsWithoutPrefs.joinToString(", ")
                    emitUiTextMessage(R.string.other_user_no_preferences_named, names)
                } else {
                    hasAtLeastOneWithPrefs = true
                    val theirMovies = filtered.map { it.movie }.toSet()
                    common = common.intersect(theirMovies)
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
                matchResult.value = null to common.toList().sorted()
            } else {
                emitUiMessage(R.string.no_group_match)
            }
        }
    }

    fun addToContacts(contactId: String) {
        viewModelScope.launch {
            val existing = contactRepo.getContactsForUser(userId)
                .any { it.contactId == contactId }

            if (existing) {
                emitUiMessage(R.string.contact_exists)
            } else {
                contactRepo.addContact(Contact(ownerId = userId, contactId = contactId))
                emitUiMessage(R.string.added_to_contacts)
                loadContacts()
            }
        }
    }


    fun resetMatchResult() {
        matchResult.value = null
    }

    private fun findCommonMovies(prefs1: List<MoviePreference>, prefs2: List<MoviePreference>): List<String> {
        val set1 = prefs1.map { it.movie }.toSet()
        val set2 = prefs2.map { it.movie }.toSet()
        return set1.intersect(set2).sorted()
    }

    private fun emitUiMessage(@StringRes resId: Int) {
        viewModelScope.launch {
            _uiMessage.emit(resId)
        }
    }

    private fun emitUiTextMessage(@StringRes resId: Int, vararg args: String) {
        viewModelScope.launch {
            _uiTextMessage.emit(resId to args.toList())
        }
    }

    private fun filterByGenres(
        prefs: List<MoviePreference>,
        genres: List<String>,
        respectGenres: Boolean = true
    ): List<MoviePreference> {
        return if (!respectGenres || genres.isEmpty()) prefs
        else prefs.filter { it.genre in genres }
    }


    fun saveSelectedGenres(context: Context) {
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit().apply {
            putStringSet("selected_genres_$userId", selectedGenres.value.toSet())
            apply()
        }
    }

    fun loadSelectedGenres(context: Context) {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val saved = prefs.getStringSet("selected_genres_$userId", emptySet())
        selectedGenres.value = saved?.toList() ?: emptyList()
    }

    fun loadSelectedGenresForUser(context: Context, targetUserId: String): List<String> {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val saved = prefs.getStringSet("selected_genres_$targetUserId", emptySet())
        return saved?.toList() ?: emptyList()
    }

}

