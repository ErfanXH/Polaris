package com.netwatcher.polaris.domain.repository

import com.netwatcher.polaris.domain.model.NetworkData
import android.location.Location

interface NetworkRepository {
    suspend fun runNetworkTest(): NetworkData
    suspend fun getLastTestResult(): NetworkData?
    suspend fun getCurrentLocation(): Location?
    suspend fun pingTest(host: String = "8.8.8.8"): Double?
    suspend fun dnsTest(hostname: String = "google.com"): Long?
    suspend fun measureUploadThroughput(): Double?
    suspend fun measureWebResponseTime(): Long?
}