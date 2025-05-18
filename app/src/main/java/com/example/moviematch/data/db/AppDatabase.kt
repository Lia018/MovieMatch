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

/**
 * Main Room database class for the MovieMatch app.
 *
 * This database includes entities related to users, their movie preferences, and contacts.
 * It provides abstract methods to access DAOs for each entity type.
 */
@Database(entities = [User::class, MoviePreference::class, Contact::class], version = 4)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provides access to user-related database operations.
     */
    abstract fun userDao(): UserDao

    /**
     * Provides access to movie preference-related database operations.
     */
    abstract fun moviePreferenceDao(): MoviePreferenceDao

    /**
     * Provides access to contact-related database operations.
     */
    abstract fun contactDao(): ContactDao

    companion object {
        // Ensures that only one instance of the database is created (singleton pattern)
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Returns the singleton instance of [AppDatabase].
         *
         * If the instance doesn't already exist, it builds the database using the application context.
         * `fallbackToDestructiveMigration` is used to reset the database on schema changes.
         *
         * @param context The application context used to build the database.
         * @return A singleton instance of [AppDatabase].
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    // Destroys and rebuilds the database if migration isn't handled explicitly
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
