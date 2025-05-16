package com.example.moviematch.domain.repository

import com.example.moviematch.data.db.entity.MoviePreference

interface MoviePreferenceRepository {
    suspend fun getMoviesForUser(userId: String): List<MoviePreference>
    suspend fun getMoviesForUserGenre(userId: String, genre: String): List<String>
    suspend fun updatePreferences(userId: String, genre: String, movies: List<String>)
}
