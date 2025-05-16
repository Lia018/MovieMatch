package com.example.moviematch.domain.repository

import com.example.moviematch.data.db.entity.User

interface UserRepository {
    suspend fun getUserById(id: String): User?
    suspend fun getUserByUsername(username: String): User?
    suspend fun insertUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun deleteUserById(id: String)
}
