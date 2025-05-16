package com.example.moviematch.presentation.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moviematch.presentation.viewmodel.PreferencesViewModel
import com.example.moviematch.domain.repository.MoviePreferenceRepository

class PreferencesViewModelFactory(
    private val context: Context,
    private val userId: String,
    private val repository: MoviePreferenceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PreferencesViewModel(context, userId, repository) as T
    }
}
