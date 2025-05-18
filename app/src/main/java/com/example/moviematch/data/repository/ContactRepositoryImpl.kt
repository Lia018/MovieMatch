package com.example.moviematch.data.repository

import com.example.moviematch.data.db.entity.Contact
import com.example.moviematch.data.db.dao.ContactDao
import com.example.moviematch.domain.repository.ContactRepository

class ContactRepositoryImpl(
    private val contactDao: ContactDao
) : ContactRepository {

    override suspend fun addContact(contact: Contact) {
        contactDao.addContact(contact)
    }

    override suspend fun getContactsForUser(userId: String): List<Contact> {
        return contactDao.getContactsForUser(userId)
    }

    override suspend fun deleteContact(ownerId: String, contactId: String) {
        contactDao.deleteContact(ownerId, contactId)
    }

    override suspend fun getContact(ownerId: String, contactId: String): Contact? {
        return contactDao.getContact(ownerId, contactId)
    }

    override suspend fun updateContact(contact: Contact) {
        contactDao.update(contact)
    }

}
