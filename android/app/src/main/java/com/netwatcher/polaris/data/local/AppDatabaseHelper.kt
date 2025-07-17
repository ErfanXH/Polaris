package com.netwatcher.polaris.data.local

import android.content.Context
import androidx.room.Room
import com.netwatcher.polaris.domain.model.NetworkDataDatabase

object AppDatabaseHelper {
    private var database: NetworkDataDatabase? = null

    fun getDatabase(context: Context): NetworkDataDatabase {
        return database ?: createDatabase(context).also { database = it }
    }

    private fun createDatabase(context: Context): NetworkDataDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            NetworkDataDatabase::class.java,
            "network_data.db"
        ).fallbackToDestructiveMigration() // Add proper migrations in production
            .build()
    }
}