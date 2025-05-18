package com.example.moviematch.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moviematch.domain.repository.UserRepository
import com.example.moviematch.presentation.viewmodel.LoginViewModel

/**
 * Factory class for creating an instance of [LoginViewModel].
 *
 * This factory provides the required [UserRepository] dependency
 * to the [LoginViewModel] constructor.
 *
 * @property userRepository The repository used for user-related data operations such as login.
 */
class LoginViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the given [ViewModel] class.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return A new instance of [LoginViewModel].
     * @throws IllegalArgumentException If the ViewModel class is not recognized.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return LoginViewModel(userRepository) as T
    }
}
