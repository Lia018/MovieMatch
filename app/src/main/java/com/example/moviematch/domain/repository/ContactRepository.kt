package com.example.moviematch.domain.repository

import com.example.moviematch.data.db.entity.Contact

interface ContactRepository {
    suspend fun addContact(contact: Contact)
    suspend fun getContactsForUser(userId: String): List<Contact>
    suspend fun deleteContact(ownerId: String, contactId: String)
    suspend fun getContact(ownerId: String, contactId: String): Contact?
    suspend fun updateContact(contact: Contact)
}
