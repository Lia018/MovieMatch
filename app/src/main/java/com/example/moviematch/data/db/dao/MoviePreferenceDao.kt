package com.example.moviematch.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.moviematch.data.db.entity.MoviePreference

/**
 * Data Access Object (DAO) for managing movie preferences in the local database.
 * Provides methods to insert, retrieve, update, and delete movie preferences by user and genre.
 */
@Dao
interface MoviePreferenceDao {

    /**
     * Inserts a single movie preference into the database.
     *
     * @param pref The [MoviePreference] object to insert.
     */
    @Insert
    suspend fun insertPreference(pref: MoviePreference)

    /**
     * Retrieves all movies for a given user and genre.
     *
     * @param userId The ID of the user.
     * @param genre The genre for which to retrieve preferred movies.
     * @return A list of movie titles associated with the specified user and genre.
     */
    @Query("SELECT movie FROM movie_preferences WHERE userId = :userId AND genre = :genre")
    suspend fun getMoviesForUserGenre(userId: String, genre: String): List<String>

    /**
     * Deletes all movie preferences for a given user and genre.
     *
     * @param userId The ID of the user.
     * @param genre The genre for which to delete preferences.
     */
    @Query("DELETE FROM movie_preferences WHERE userId = :userId AND genre = :genre")
    suspend fun deleteGenrePreferences(userId: String, genre: String)

    /**
     * Updates the movie preferences for a given user and genre.
     * First deletes existing preferences for the genre, then inserts the new ones.
     *
     * This method is transactional to ensure atomicity of the update operation.
     *
     * @param userId The ID of the user.
     * @param genre The genre being updated.
     * @param movies The new list of movies to associate with the genre.
     */
    @Transaction
    suspend fun updatePreferences(userId: String, genre: String, movies: List<String>) {
        deleteGenrePreferences(userId, genre)
        movies.forEach {
            insertPreference(MoviePreference(userId = userId, genre = genre, movie = it))
        }
    }

    /**
     * Retrieves all movie preferences for a given user.
     *
     * @param userId The ID of the user.
     * @return A list of [MoviePreference] records for the user.
     */
    @Query("SELECT * FROM movie_preferences WHERE userId = :userId")
    suspend fun getMoviesForUser(userId: String): List<MoviePreference>
}
