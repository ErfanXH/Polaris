package com.netwatcher.polaris.domain.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [NetworkData::class],
    version = 9,
    exportSchema = false
)
abstract class NetworkDataDatabase : RoomDatabase() {
    abstract fun networkDataDao(): NetworkDataDao
}