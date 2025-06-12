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
    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun addNetworkData(networkDataEntity: NetworkData)

    @Query("Select * from `networkData-table`")
    abstract fun getAllNetworkData(): Flow<List<NetworkData>>

    @Update
    abstract suspend fun updateNetworkData(networkDataEntity: NetworkData)

    @Delete
    abstract suspend fun deleteNetworkData(networkDataEntity: NetworkData)

    @Query("Select * from `networkData-table` where id=:id")
    abstract fun getNetworkDataById(id: Long): Flow<NetworkData>
}