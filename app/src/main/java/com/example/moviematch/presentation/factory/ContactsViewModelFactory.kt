package com.example.moviematch.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moviematch.presentation.viewmodel.ContactsViewModel
import com.example.moviematch.domain.repository.ContactRepository

class ContactsViewModelFactory(
    private val repository: ContactRepository,
    private val userId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ContactsViewModel(repository, userId) as T
    }
}

