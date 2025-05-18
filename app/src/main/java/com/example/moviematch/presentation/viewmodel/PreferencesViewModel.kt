package com.example.moviematch.presentation.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviematch.R
import com.example.moviematch.domain.repository.MoviePreferenceRepository
import com.example.moviematch.util.importMoviesFromCsv
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing and persisting movie preferences per genre for a specific user.
 * It loads available genres and movies from a CSV, tracks user selections, and updates preferences to the repository.
 *
 * This implementation extends [AndroidViewModel] to allow access to application context.
 *
 * @param application The application instance used to access assets and context.
 * @param userId The unique ID of the current user.
 * @param repository The data source handling preference persistence.
 */
class PreferencesViewModel(
    application: Application,
    private val userId: String,
    private val repository: MoviePreferenceRepository
) : AndroidViewModel(application) {

    /**
     * Map of genres to their corresponding list of movie titles.
     * This data is loaded from a CSV on initialization.
     */
    private val _genreMovieMap = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val genreMovieMap: StateFlow<Map<String, List<String>>> = _genreMovieMap

    /**
     * The genre currently selected by the user in the UI.
     */
    val selectedGenre = MutableStateFlow("")

    /**
     * Stores selected movies per genre for the user.
     * Used for tracking and submitting user preference changes.
     */
    private val _selectedByGenre = MutableStateFlow<Map<String, Set<String>>>(emptyMap())
    val selectedByGenre: StateFlow<Map<String, Set<String>>> = _selectedByGenre

    init {
        // Load all genres and associated movies from a CSV file on initialization
        viewModelScope.launch {
            val imported = importMoviesFromCsv(getApplication())
            _genreMovieMap.value = imported
            selectedGenre.value = imported.keys.minOrNull().orEmpty()
        }

        // When the selected genre changes, ensure its preferences are loaded from the repository
        selectedGenre
            .onEach { genre ->
                if (genre.isNotBlank() && !_selectedByGenre.value.containsKey(genre)) {
                    viewModelScope.launch {
                        val selected = repository.getMoviesForUserGenre(userId, genre).toSet()
                        _selectedByGenre.value += (genre to selected)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Toggles the selection state of a given movie for the currently selected genre.
     * Adds the movie to the selection if not present, or removes it if already selected.
     *
     * @param movie The title of the movie to be toggled.
     */
    fun toggleMovieSelection(movie: String) {
        val genre = selectedGenre.value
        val currentMap = _selectedByGenre.value.toMutableMap()
        val selected = currentMap[genre]?.toMutableSet() ?: mutableSetOf()

        if (selected.contains(movie)) selected.remove(movie) else selected.add(movie)
        currentMap[genre] = selected
        _selectedByGenre.value = currentMap
    }

    /**
     * Saves all current user preferences (selected movies by genre) to the repository.
     * Displays a toast on successful save.
     */
    fun saveAll() {
        viewModelScope.launch {
            _selectedByGenre.value.forEach { (genre, movies) ->
                repository.updatePreferences(userId, genre, movies.toList())
            }
            Toast.makeText(getApplication(), R.string.preferences_updated, Toast.LENGTH_SHORT).show()
        }
    }
}
