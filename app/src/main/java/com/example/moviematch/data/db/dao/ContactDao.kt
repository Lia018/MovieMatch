package com.example.moviematch.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.moviematch.data.db.entity.Contact

/**
 * Data Access Object (DAO) for accessing and managing user contacts in the database.
 * Provides methods to insert, retrieve, update, and delete contact records.
 */
@Dao
interface ContactDao {

    /**
     * Inserts a new contact into the database.
     *
     * @param contact The contact to be added.
     */
    @Insert
    suspend fun addContact(contact: Contact)

    /**
     * Retrieves all contacts for the given user, sorted by the date they were added (most recent first).
     *
     * @param userId The ID of the user whose contacts are being fetched.
     * @return A list of contacts belonging to the specified user.
     */
    @Query("SELECT * FROM contacts WHERE ownerId = :userId ORDER BY addedAt DESC")
    suspend fun getContactsForUser(userId: String): List<Contact>

    /**
     * Deletes a specific contact based on the owner ID and contact ID.
     *
     * @param ownerId The ID of the user who owns the contact.
     * @param contactId The ID of the contact to be deleted.
     */
    @Query("DELETE FROM contacts WHERE ownerId = :ownerId AND contactId = :contactId")
    suspend fun deleteContact(ownerId: String, contactId: String)

    /**
     * Updates an existing contact's information in the database.
     *
     * @param contact The contact with updated information.
     */
    @Update
    suspend fun update(contact: Contact)

    /**
     * Retrieves a single contact by owner ID and contact ID.
     *
     * @param ownerId The ID of the user who owns the contact.
     * @param contactId The ID of the contact to be retrieved.
     * @return The contact if found, or null if no matching contact exists.
     */
    @Query("SELECT * FROM contacts WHERE ownerId = :ownerId AND contactId = :contactId LIMIT 1")
    suspend fun getContact(ownerId: String, contactId: String): Contact?
}
