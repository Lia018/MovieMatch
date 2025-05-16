package com.example.moviematch.data.repository

import com.example.moviematch.data.db.entity.MoviePreference
import com.example.moviematch.data.db.dao.MoviePreferenceDao
import com.example.moviematch.domain.repository.MoviePreferenceRepository

class MoviePreferenceRepositoryImpl(
    private val dao: MoviePreferenceDao
) : MoviePreferenceRepository {

    override suspend fun getMoviesForUser(userId: String): List<MoviePreference> {
        return dao.getMoviesForUser(userId)
    }

    override suspend fun getMoviesForUserGenre(userId: String, genre: String): List<String> {
        return dao.getMoviesForUserGenre(userId, genre)
    }

    override suspend fun updatePreferences(userId: String, genre: String, movies: List<String>) {
        dao.updatePreferences(userId, genre, movies)
    }
}
