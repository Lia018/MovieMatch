package com.example.moviematch.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moviematch.domain.repository.UserRepository
import com.example.moviematch.presentation.viewmodel.EditProfileViewModel

/**
 * Factory class for creating an instance of [EditProfileViewModel] with required constructor parameters.
 *
 * @property userId The ID of the user whose profile is being edited.
 * @property repository The repository responsible for accessing and updating user data.
 */
class EditProfileViewModelFactory(
    private val userId: String,
    private val repository: UserRepository,
) : ViewModelProvider.Factory {

    /**
     * Creates an instance of the specified [ViewModel] class.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return A new instance of [EditProfileViewModel].
     * @throws IllegalArgumentException If the ViewModel class is not recognized.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EditProfileViewModel(userId, repository) as T
    }
}
