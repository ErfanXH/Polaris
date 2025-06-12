package com.netwatcher.polaris.domain.repository

import android.location.Location
import com.netwatcher.polaris.domain.model.NetworkData
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    suspend fun runNetworkTest(): NetworkData
//    suspend fun getLastTestResult(): NetworkData?
    suspend fun getCurrentLocation(): Location?
    suspend fun pingTest(host: String = "8.8.8.8"): Double?
    suspend fun dnsTest(hostname: String = "google.com"): Long?
    suspend fun measureUploadThroughput(): Double?
    suspend fun measureWebResponseTime(): Long?

    suspend fun addNetworkData(networkData: NetworkData)
    fun getAllNetworkData(): Flow<List<NetworkData>>
    suspend fun getNetworkDataById(id: Long): Flow<NetworkData>
    suspend fun deleteNetworkData(networkData: NetworkData)
}