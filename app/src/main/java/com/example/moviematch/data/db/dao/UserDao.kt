package com.example.moviematch.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.moviematch.data.db.entity.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE userId = :id LIMIT 1")
    suspend fun getUserById(id: String): User?

    @Query("DELETE FROM users WHERE userId = :id")
    suspend fun deleteUserById(id: String)

}
