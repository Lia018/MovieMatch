package com.example.moviematch.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.moviematch.data.db.entity.Contact

@Dao
interface ContactDao {
    @Insert
    suspend fun addContact(contact: Contact)

    @Query("SELECT * FROM contacts WHERE ownerId = :userId ORDER BY addedAt DESC")
    suspend fun getContactsForUser(userId: String): List<Contact>

    @Query("DELETE FROM contacts WHERE ownerId = :ownerId AND contactId = :contactId")
    suspend fun deleteContact(ownerId: String, contactId: String)

    @Update
    suspend fun update(contact: Contact)

    @Query("SELECT * FROM contacts WHERE ownerId = :ownerId AND contactId = :contactId LIMIT 1")
    suspend fun getContact(ownerId: String, contactId: String): Contact?

}
