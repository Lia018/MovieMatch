package com.example.moviematch.data.repository

import com.example.moviematch.data.db.entity.User
import com.example.moviematch.data.db.dao.UserDao
import com.example.moviematch.domain.repository.UserRepository

/**
 * Implementation of [UserRepository] that handles user-related data operations.
 * This class delegates all data access to the [UserDao] interface.
 *
 * @property userDao DAO for accessing user data in the Room database.
 */
class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    /**
     * Retrieves a user by their unique ID.
     *
     * @param id The unique user ID.
     * @return The [User] entity if found, or null otherwise.
     */
    override suspend fun getUserById(id: String): User? {
        return userDao.getUserById(id)
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The username to search for.
     * @return The [User] entity if found, or null otherwise.
     */
    override suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    /**
     * Inserts a new user into the database.
     *
     * @param user The [User] entity to insert.
     */
    override suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    /**
     * Updates an existing user's information in the database.
     *
     * @param user The updated [User] entity.
     */
    override suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    /**
     * Deletes a user from the database by their unique ID.
     *
     * @param id The unique ID of the user to delete.
     */
    override suspend fun deleteUserById(id: String) {
        userDao.deleteUserById(id)
    }
}
