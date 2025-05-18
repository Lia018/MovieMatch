package com.example.moviematch.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moviematch.presentation.viewmodel.ContactsViewModel
import com.example.moviematch.domain.repository.ContactRepository
import com.example.moviematch.domain.repository.UserRepository

/**
 * Factory class used to create an instance of [ContactsViewModel] with constructor parameters.
 *
 * @param repository The repository responsible for managing contact data.
 * @param userRepository The repository used to access user-related data.
 * @param userId The ID of the currently logged-in user.
 */
class ContactsViewModelFactory(
    private val repository: ContactRepository,
    private val userRepository: UserRepository,
    private val userId: String
) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the specified [ViewModel] class.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return A new instance of the requested ViewModel.
     * @throws IllegalArgumentException If the ViewModel class is unknown.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ContactsViewModel(repository, userRepository, userId) as T
    }
}
