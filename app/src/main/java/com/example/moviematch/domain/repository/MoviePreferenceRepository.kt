package com.example.moviematch.domain.repository

import com.example.moviematch.data.db.entity.MoviePreference

/**
 * Interface for accessing and managing movie preferences in the data layer.
 * Acts as a contract for repository implementations that deal with movie preference operations.
 */
interface MoviePreferenceRepository {

    /**
     * Retrieves all movie preferences for a given user.
     *
     * @param userId The ID of the user whose movie preferences are to be fetched.
     * @return A list of [MoviePreference] representing the user's stored preferences.
     */
    suspend fun getMoviesForUser(userId: String): List<MoviePreference>

    /**
     * Retrieves movies from a specific genre for a given user.
     *
     * @param userId The ID of the user.
     * @param genre The genre to filter movies by.
     * @return A list of movie titles preferred by the user in the specified genre.
     */
    suspend fun getMoviesForUserGenre(userId: String, genre: String): List<String>

    /**
     * Updates the list of preferred movies for a given user and genre.
     * Existing preferences for that genre are replaced with the provided list.
     *
     * @param userId The ID of the user whose preferences are to be updated.
     * @param genre The genre for which the preferences are being updated.
     * @param movies A list of movie titles to store as preferences for the genre.
     */
    suspend fun updatePreferences(userId: String, genre: String, movies: List<String>)
}
