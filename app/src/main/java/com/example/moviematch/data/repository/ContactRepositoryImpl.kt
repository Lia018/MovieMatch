package com.example.moviematch.data.repository

import com.example.moviematch.data.db.entity.Contact
import com.example.moviematch.data.db.dao.ContactDao
import com.example.moviematch.domain.repository.ContactRepository

/**
 * Implementation of [ContactRepository] that handles contact-related data operations.
 * Uses [ContactDao] to perform database interactions.
 *
 * @param contactDao DAO interface for accessing the contacts table.
 */
class ContactRepositoryImpl(
    private val contactDao: ContactDao
) : ContactRepository {

    /**
     * Adds a new contact to the database.
     *
     * @param contact The contact to be added.
     */
    override suspend fun addContact(contact: Contact) {
        contactDao.addContact(contact)
    }

    /**
     * Retrieves all contacts for a specific user.
     *
     * @param userId The ID of the user whose contacts should be fetched.
     * @return A list of [Contact]s associated with the user.
     */
    override suspend fun getContactsForUser(userId: String): List<Contact> {
        return contactDao.getContactsForUser(userId)
    }

    /**
     * Deletes a specific contact for a user.
     *
     * @param ownerId The ID of the user who owns the contact.
     * @param contactId The ID of the contact to be deleted.
     */
    override suspend fun deleteContact(ownerId: String, contactId: String) {
        contactDao.deleteContact(ownerId, contactId)
    }

    /**
     * Retrieves a specific contact for a user.
     *
     * @param ownerId The ID of the user who owns the contact.
     * @param contactId The ID of the contact to be fetched.
     * @return The [Contact] if it exists, or null otherwise.
     */
    override suspend fun getContact(ownerId: String, contactId: String): Contact? {
        return contactDao.getContact(ownerId, contactId)
    }

    /**
     * Updates an existing contact in the database.
     *
     * @param contact The contact with updated information.
     */
    override suspend fun updateContact(contact: Contact) {
        contactDao.update(contact)
    }
}
