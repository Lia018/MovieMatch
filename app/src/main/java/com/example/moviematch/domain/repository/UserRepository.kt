package com.example.moviematch.domain.repository

import com.example.moviematch.data.db.entity.User

/**
 * Interface that defines methods for accessing and manipulating user-related data.
 * Implementations of this interface are responsible for communicating with the data layer
 * (typically a Room database).
 */
interface UserRepository {

    /**
     * Retrieves a [User] entity by its unique ID.
     *
     * @param id The user ID.
     * @return The corresponding [User] if found, or null if no user exists with the given ID.
     */
    suspend fun getUserById(id: String): User?

    /**
     * Retrieves a [User] entity by its username.
     *
     * @param username The username to search for.
     * @return The matching [User] if found, or null if no user has the specified username.
     */
    suspend fun getUserByUsername(username: String): User?

    /**
     * Inserts a new [User] into the database.
     *
     * @param user The user entity to insert.
     */
    suspend fun insertUser(user: User)

    /**
     * Updates the details of an existing [User].
     *
     * @param user The user entity with updated values.
     */
    suspend fun updateUser(user: User)

    /**
     * Deletes a [User] from the database using their ID.
     *
     * @param id The ID of the user to delete.
     */
    suspend fun deleteUserById(id: String)
}
