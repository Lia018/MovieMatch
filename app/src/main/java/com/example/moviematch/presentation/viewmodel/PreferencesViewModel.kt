package com.example.moviematch.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviematch.R
import com.example.moviematch.domain.repository.MoviePreferenceRepository
import com.example.moviematch.util.importMoviesFromCsv
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/*class PreferencesViewModel(
    private val context: Context,
    private val userId: String,
    private val repository: MoviePreferenceRepository
) : ViewModel() {

    private val _genreMovieMap = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val genreMovieMap: StateFlow<Map<String, List<String>>> = _genreMovieMap

    val selectedGenre = MutableStateFlow("")

    private val _selectedByGenre = MutableStateFlow<Map<String, Set<String>>>(emptyMap())
    val selectedByGenre: StateFlow<Map<String, Set<String>>> = _selectedByGenre


    init {
        viewModelScope.launch {
            val imported = importMoviesFromCsv(context)
            _genreMovieMap.value = imported
            selectedGenre.value = imported.keys.sorted().firstOrNull().orEmpty()
        }

        selectedGenre
            .onEach { genre ->
                if (genre.isNotBlank() && !_selectedByGenre.value.containsKey(genre)) {
                    viewModelScope.launch {
                        val selected = repository.getMoviesForUserGenre(userId, genre).toSet()
                        _selectedByGenre.value = _selectedByGenre.value + (genre to selected)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun toggleMovieSelection(movie: String) {
        val genre = selectedGenre.value
        val currentMap = _selectedByGenre.value.toMutableMap()
        val selected = currentMap[genre]?.toMutableSet() ?: mutableSetOf()

        if (selected.contains(movie)) selected.remove(movie) else selected.add(movie)

        currentMap[genre] = selected
        _selectedByGenre.value = currentMap
    }

    fun saveAll() {
        viewModelScope.launch {
            _selectedByGenre.value.forEach { (genre, movies) ->
                repository.updatePreferences(userId, genre, movies.toList())
            }
            Toast.makeText(context, R.string.preferences_updated, Toast.LENGTH_SHORT).show()
        }
    }

}*/

class PreferencesViewModel(
    application: Application,
    private val userId: String,
    private val repository: MoviePreferenceRepository
) : AndroidViewModel(application) {

    private val _genreMovieMap = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val genreMovieMap: StateFlow<Map<String, List<String>>> = _genreMovieMap

    val selectedGenre = MutableStateFlow("")
    private val _selectedByGenre = MutableStateFlow<Map<String, Set<String>>>(emptyMap())
    val selectedByGenre: StateFlow<Map<String, Set<String>>> = _selectedByGenre

    init {
        viewModelScope.launch {
            val imported = importMoviesFromCsv(getApplication())
            _genreMovieMap.value = imported
            selectedGenre.value = imported.keys.sorted().firstOrNull().orEmpty()
        }

        selectedGenre
            .onEach { genre ->
                if (genre.isNotBlank() && !_selectedByGenre.value.containsKey(genre)) {
                    viewModelScope.launch {
                        val selected = repository.getMoviesForUserGenre(userId, genre).toSet()
                        _selectedByGenre.value = _selectedByGenre.value + (genre to selected)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun toggleMovieSelection(movie: String) {
        val genre = selectedGenre.value
        val currentMap = _selectedByGenre.value.toMutableMap()
        val selected = currentMap[genre]?.toMutableSet() ?: mutableSetOf()

        if (selected.contains(movie)) selected.remove(movie) else selected.add(movie)
        currentMap[genre] = selected
        _selectedByGenre.value = currentMap
    }

    fun saveAll() {
        viewModelScope.launch {
            _selectedByGenre.value.forEach { (genre, movies) ->
                repository.updatePreferences(userId, genre, movies.toList())
            }
            Toast.makeText(getApplication(), R.string.preferences_updated, Toast.LENGTH_SHORT).show()
        }
    }
}

