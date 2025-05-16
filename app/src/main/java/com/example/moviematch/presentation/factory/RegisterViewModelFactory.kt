package com.example.moviematch.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moviematch.presentation.viewmodel.RegisterViewModel
import com.example.moviematch.domain.repository.UserRepository

class RegisterViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return RegisterViewModel(repository) as T
    }
}
