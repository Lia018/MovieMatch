package com.example.moviematch.data.repository

import com.example.moviematch.data.db.entity.MoviePreference
import com.example.moviematch.data.db.dao.MoviePreferenceDao
import com.example.moviematch.domain.repository.MoviePreferenceRepository

/**
 * Implementation of [MoviePreferenceRepository] that handles movie preference operations.
 * Delegates data access to the [MoviePreferenceDao] interface.
 *
 * @param dao DAO for accessing movie preference data in the database.
 */
class MoviePreferenceRepositoryImpl(
    private val dao: MoviePreferenceDao
) : MoviePreferenceRepository {

    /**
     * Retrieves all movie preferences for a given user.
     *
     * @param userId The ID of the user whose preferences are being retrieved.
     * @return A list of [MoviePreference] entities associated with the user.
     */
    override suspend fun getMoviesForUser(userId: String): List<MoviePreference> {
        return dao.getMoviesForUser(userId)
    }

    /**
     * Retrieves the list of movies that a user has selected for a specific genre.
     *
     * @param userId The ID of the user.
     * @param genre The genre to filter movies by.
     * @return A list of movie titles selected by the user for the specified genre.
     */
    override suspend fun getMoviesForUserGenre(userId: String, genre: String): List<String> {
        return dao.getMoviesForUserGenre(userId, genre)
    }

    /**
     * Updates the user's preferences for a given genre by replacing all existing
     * movies with the new list.
     *
     * This method first deletes all existing preferences for the given genre,
     * and then inserts the new ones.
     *
     * @param userId The ID of the user.
     * @param genre The genre for which preferences are being updated.
     * @param movies The new list of movies to save for the genre.
     */
    override suspend fun updatePreferences(userId: String, genre: String, movies: List<String>) {
        dao.updatePreferences(userId, genre, movies)
    }
}
