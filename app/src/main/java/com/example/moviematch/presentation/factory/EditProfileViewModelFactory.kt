package com.example.moviematch.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moviematch.domain.repository.UserRepository
import com.example.moviematch.presentation.viewmodel.EditProfileViewModel

class EditProfileViewModelFactory(
    private val userId: String,
    private val repository: UserRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return EditProfileViewModel(userId, repository) as T
    }
}

