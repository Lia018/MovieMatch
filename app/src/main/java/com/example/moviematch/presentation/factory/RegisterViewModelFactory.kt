package com.example.moviematch.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moviematch.presentation.viewmodel.RegisterViewModel
import com.example.moviematch.domain.repository.UserRepository

/**
 * Factory class for creating an instance of [RegisterViewModel].
 *
 * This factory provides the required [UserRepository] dependency to the ViewModel.
 *
 * @property repository Repository for user-related data operations (e.g., registration).
 */
class RegisterViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.Factory {

    /**
     * Creates and returns an instance of [RegisterViewModel].
     *
     * @param modelClass The ViewModel class to instantiate.
     * @return A new instance of [RegisterViewModel], cast to the expected ViewModel type.
     * @throws IllegalArgumentException If the ViewModel class is not assignable.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return RegisterViewModel(repository) as T
    }
}
