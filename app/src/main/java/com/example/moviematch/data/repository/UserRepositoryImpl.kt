package com.example.moviematch.data.repository

import com.example.moviematch.data.db.entity.User
import com.example.moviematch.data.db.dao.UserDao
import com.example.moviematch.domain.repository.UserRepository

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun getUserById(id: String): User? {
        return userDao.getUserById(id)
    }

    override suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    override suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    override suspend fun deleteUserById(id: String) {
        userDao.deleteUserById(id)
    }
}
