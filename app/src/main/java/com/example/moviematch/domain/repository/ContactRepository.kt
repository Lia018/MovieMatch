package com.example.moviematch.domain.repository

import com.example.moviematch.data.db.entity.Contact

/**
 * Interface for contact-related data operations.
 * Acts as an abstraction layer between the data layer and the use cases.
 */
interface ContactRepository {

    /**
     * Adds a new contact to the database.
     *
     * @param contact The [Contact] object to be added.
     */
    suspend fun addContact(contact: Contact)

    /**
     * Retrieves all contacts for a given user.
     *
     * @param userId The ID of the user whose contacts should be retrieved.
     * @return A list of [Contact]s associated with the user.
     */
    suspend fun getContactsForUser(userId: String): List<Contact>

    /**
     * Deletes a specific contact associated with a user.
     *
     * @param ownerId The ID of the user who owns the contact.
     * @param contactId The ID of the contact to be deleted.
     */
    suspend fun deleteContact(ownerId: String, contactId: String)

    /**
     * Retrieves a specific contact by owner and contact ID.
     *
     * @param ownerId The ID of the user who owns the contact.
     * @param contactId The ID of the contact to retrieve.
     * @return The [Contact] object if found, or null if not found.
     */
    suspend fun getContact(ownerId: String, contactId: String): Contact?

    /**
     * Updates an existing contact in the database.
     *
     * @param contact The [Contact] object with updated information.
     */
    suspend fun updateContact(contact: Contact)
}
