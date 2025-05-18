package com.example.moviematch.presentation.factory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moviematch.domain.repository.ContactRepository
import com.example.moviematch.domain.repository.MoviePreferenceRepository
import com.example.moviematch.domain.repository.UserRepository
import com.example.moviematch.presentation.viewmodel.MatchViewModel

/**
 * Factory class for creating an instance of [MatchViewModel].
 *
 * This factory provides the required repositories and user ID needed to construct the [MatchViewModel].
 *
 * @property application The application context used within the ViewModel.
 * @property userId The ID of the current user for whom the ViewModel is being created.
 * @property movieRepo Repository for accessing and managing movie preferences.
 * @property contactRepo Repository for managing the user's contacts.
 * @property userRepo Repository for user-related data and actions.
 */
class MatchViewModelFactory(
    private val application: Application,
    private val userId: String,
    private val movieRepo: MoviePreferenceRepository,
    private val contactRepo: ContactRepository,
    private val userRepo: UserRepository
) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the given [ViewModel] class.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return A new instance of [MatchViewModel].
     * @throws IllegalArgumentException If the ViewModel class is not assignable from [MatchViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MatchViewModel(application, userId, movieRepo, contactRepo, userRepo) as T
    }
}
