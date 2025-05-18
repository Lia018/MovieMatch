package com.example.moviematch.data.db.entity

import androidx.room.Entity

/**
 * Represents a contact relationship between two users in the app.
 * Each contact is uniquely identified by a combination of [ownerId] and [contactId].
 *
 * The contact's display name can be optionally customized, and the timestamp of when the contact was added is stored.
 *
 * This entity is stored in the "contacts" table.
 */
@Entity(tableName = "contacts", primaryKeys = ["ownerId", "contactId"])
data class Contact(

    /**
     * The ID of the user who owns this contact.
     * This is the user who added the contact.
     */
    val ownerId: String,

    /**
     * The ID of the user who was added as a contact.
     */
    val contactId: String,

    /**
     * Optional display name shown for the contact.
     * Defaults to an empty string if not provided.
     */
    val displayName: String = "",

    /**
     * Timestamp indicating when the contact was added (in milliseconds).
     * Defaults to the current system time at the moment of creation.
     */
    val addedAt: Long = System.currentTimeMillis()
)
