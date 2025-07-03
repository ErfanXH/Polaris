package com.netwatcher.polaris.domain.repository

import android.location.Location
import com.netwatcher.polaris.domain.model.NetworkData
import com.netwatcher.polaris.domain.model.TestSelection
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody

interface NetworkRepository {
    suspend fun runNetworkTest(subscriptionId: Int? = null, testSelection: TestSelection): NetworkData
    suspend fun getCurrentLocation(): Location?
    suspend fun pingTest(): Double?
    suspend fun dnsTest(): Double?
    suspend fun measureUploadThroughput(): Double?
    suspend fun measureDownloadThroughput(): Double?
    suspend fun measureWebResponseTime(): Double?
    suspend fun addNetworkData(networkData: NetworkData)
    fun getAllNetworkData(): Flow<List<NetworkData>>
    suspend fun uploadNetworkData(data: Any): Result<Unit>
    suspend fun uploadNetworkDataBatch(data: RequestBody): Result<Unit>
    suspend fun getUserInfo(): Result<Unit>
}