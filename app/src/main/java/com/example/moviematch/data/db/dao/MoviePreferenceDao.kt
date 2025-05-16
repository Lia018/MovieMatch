package com.example.moviematch.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.moviematch.data.db.entity.MoviePreference

@Dao
interface MoviePreferenceDao {

    @Insert
    suspend fun insertPreference(pref: MoviePreference)

    @Query("SELECT movie FROM movie_preferences WHERE userId = :userId AND genre = :genre")
    suspend fun getMoviesForUserGenre(userId: String, genre: String): List<String>

    @Query("DELETE FROM movie_preferences WHERE userId = :userId AND genre = :genre")
    suspend fun deleteGenrePreferences(userId: String, genre: String)

    @Transaction
    suspend fun updatePreferences(userId: String, genre: String, movies: List<String>) {
        deleteGenrePreferences(userId, genre)
        movies.forEach {
            insertPreference(MoviePreference(userId = userId, genre = genre, movie = it))
        }
    }

    @Query("SELECT * FROM movie_preferences WHERE userId = :userId")
    suspend fun getMoviesForUser(userId: String): List<MoviePreference>




}
