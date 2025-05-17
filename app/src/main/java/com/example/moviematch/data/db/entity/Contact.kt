package com.example.moviematch.data.db.entity

import androidx.room.Entity

@Entity(tableName = "contacts", primaryKeys = ["ownerId", "contactId"])
data class Contact(
    val ownerId: String,
    val contactId: String,
    val displayName: String = "",
    val addedAt: Long = System.currentTimeMillis()
)

