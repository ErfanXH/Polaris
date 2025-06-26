package com.netwatcher.polaris.domain.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class NetworkDataDao {
    companion object {
        private var gmail: String? = ""
        fun setEmail(userEmail: String?) {
            gmail = userEmail
        }
        fun getEmail(): String? {
            return gmail
        }
    }
    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun addNetworkData(networkDataEntity: NetworkData)

    @Query("Select * from `networkData-table`")
    abstract fun getAllNetworkData(): Flow<List<NetworkData>>

    @Query("SELECT * FROM `networkData-table` WHERE isSynced = 0 AND email=:gmail")
    abstract suspend fun getUnsyncedData(gmail: String?): List<NetworkData>

    @Query("UPDATE `networkData-table` SET isSynced = 1 WHERE id IN (:ids)")
    abstract suspend fun markAsSynced(ids: List<Long>)
}