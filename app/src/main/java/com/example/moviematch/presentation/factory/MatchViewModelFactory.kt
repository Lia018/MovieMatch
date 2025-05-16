package com.example.moviematch.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moviematch.domain.repository.ContactRepository
import com.example.moviematch.domain.repository.MoviePreferenceRepository
import com.example.moviematch.domain.repository.UserRepository
import com.example.moviematch.presentation.viewmodel.MatchViewModel

class MatchViewModelFactory(
    private val userId: String,
    private val movieRepo: MoviePreferenceRepository,
    private val contactRepo: ContactRepository,
    private val userRepo: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MatchViewModel(userId, movieRepo, contactRepo, userRepo) as T
    }
}

