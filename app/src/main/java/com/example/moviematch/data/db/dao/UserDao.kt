package com.example.moviematch.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.moviematch.data.db.entity.User

/**
 * Data Access Object (DAO) for managing user-related database operations.
 * This interface defines methods for inserting, updating, retrieving, and deleting user data.
 */
@Dao
interface UserDao {

    /**
     * Inserts a new user into the database.
     *
     * @param user The [User] entity to be inserted.
     */
    @Insert
    suspend fun insertUser(user: User)

    /**
     * Updates an existing user's information in the database.
     *
     * @param user The [User] entity with updated information.
     */
    @Update
    suspend fun updateUser(user: User)

    /**
     * Retrieves a user by their username.
     *
     * @param username The username to search for.
     * @return The [User] if found, or null otherwise.
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    /**
     * Retrieves a user by their unique user ID.
     *
     * @param id The user ID to search for.
     * @return The [User] if found, or null otherwise.
     */
    @Query("SELECT * FROM users WHERE userId = :id LIMIT 1")
    suspend fun getUserById(id: String): User?

    /**
     * Deletes a user from the database using their user ID.
     *
     * @param id The user ID of the user to be deleted.
     */
    @Query("DELETE FROM users WHERE userId = :id")
    suspend fun deleteUserById(id: String)
}
