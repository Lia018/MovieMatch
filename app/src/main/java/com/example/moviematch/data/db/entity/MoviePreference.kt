package com.example.moviematch.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a movie preference entry for a specific user.
 *
 * Each record links a user to a particular movie within a given genre.
 * This allows the app to store and retrieve user-specific movie preferences.
 *
 * This entity is stored in the "movie_preferences" table.
 */
@Entity(tableName = "movie_preferences")
data class MoviePreference(

    /**
     * Auto-generated unique identifier for the preference entry.
     */
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    /**
     * The ID of the user who has this movie preference.
     */
    val userId: String,

    /**
     * The genre category of the movie.
     */
    val genre: String,

    /**
     * The name/title of the movie the user prefers within the given genre.
     */
    val movie: String
)
