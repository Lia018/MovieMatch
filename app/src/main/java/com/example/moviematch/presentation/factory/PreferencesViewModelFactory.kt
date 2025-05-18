package com.example.moviematch.presentation.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moviematch.presentation.viewmodel.PreferencesViewModel
import com.example.moviematch.domain.repository.MoviePreferenceRepository

/**
 * Factory class for creating an instance of [PreferencesViewModel].
 *
 * This factory supplies the [Application] context, the ID of the user, and a [MoviePreferenceRepository]
 * to the [PreferencesViewModel] constructor.
 *
 * @property application The application context used within the ViewModel.
 * @property userId The ID of the current user whose preferences will be managed.
 * @property repository Repository handling movie preference data access and operations.
 */
class PreferencesViewModelFactory(
    private val application: Application,
    private val userId: String,
    private val repository: MoviePreferenceRepository
) : ViewModelProvider.Factory {

    /**
     * Creates and returns an instance of [PreferencesViewModel].
     *
     * @param modelClass The ViewModel class to instantiate.
     * @return A new instance of [PreferencesViewModel] cast to the expected ViewModel type.
     * @throws IllegalArgumentException If the ViewModel class is not assignable.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PreferencesViewModel(application, userId, repository) as T
    }
}
