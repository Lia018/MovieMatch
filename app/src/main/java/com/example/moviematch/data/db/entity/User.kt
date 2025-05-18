package com.example.moviematch.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a user in the application.
 *
 * This entity stores basic user authentication data such as a unique user ID,
 * username, and password. It is persisted in the "users" table of the Room database.
 */
@Entity(tableName = "users")
data class User(

    /**
     * Unique identifier for the user.
     * This ID is used across the application to associate user-specific data.
     */
    @PrimaryKey val userId: String,

    /**
     * The user's chosen username, used for login and display.
     */
    val username: String,

    /**
     * The user's password.
     * Note: In production, this should be hashed and not stored in plain text.
     */
    val password: String
)
