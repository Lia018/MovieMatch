package com.example.moviematch.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_preferences")
data class MoviePreference(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val genre: String,
    val movie: String
)
