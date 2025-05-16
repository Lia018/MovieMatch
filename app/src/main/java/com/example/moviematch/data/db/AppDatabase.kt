package com.example.moviematch.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moviematch.data.db.dao.ContactDao
import com.example.moviematch.data.db.dao.MoviePreferenceDao
import com.example.moviematch.data.db.dao.UserDao
import com.example.moviematch.data.db.entity.Contact
import com.example.moviematch.data.db.entity.MoviePreference
import com.example.moviematch.data.db.entity.User

@Database(entities = [User::class, MoviePreference::class, Contact::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun moviePreferenceDao(): MoviePreferenceDao
    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

