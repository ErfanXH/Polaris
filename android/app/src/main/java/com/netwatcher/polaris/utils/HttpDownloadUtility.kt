package com.netwatcher.polaris.utils

import com.netwatcher.polaris.di.CookieManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

object HttpDownloadUtility {
    private const val DOWNLOAD_URL = "http://194.62.43.37/api/mobile/HTTPTest/download/"
    private const val TEST_DURATION_MS = 5000

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private suspend fun getAuthToken(): String {
        return CookieManager.getToken().firstOrNull().toString()
    }

    suspend fun measureDownloadThroughput(): Double = withContext(Dispatchers.IO) {
        println("[DEBUG] Starting download throughput test")

        val startTime = System.nanoTime()
        var totalBytesReceived = 0L
        var successfulRequests = 0
        var failedRequests = 0

        try {
            println("[DEBUG] Starting download loop...")

            while (true) {
                val elapsedMs = (System.nanoTime() - startTime) / 1_000_000
                if (elapsedMs >= TEST_DURATION_MS) {
                    println("[DEBUG] Test duration reached ($TEST_DURATION_MS ms)")
                    break
                }

                val request = Request.Builder()
                    .addHeader("Authorization", getAuthToken())
                    .url(DOWNLOAD_URL)
                    .get()
                    .build()

                try {
                    val response = client.newCall(request).execute()
                    response.use {
                        if (it.isSuccessful) {
                            val bytes = it.body?.bytes() ?: byteArrayOf()
                            totalBytesReceived += bytes.size
                            successfulRequests++
                            println("[DEBUG] Download successful (${bytes.size} bytes) - Total: $totalBytesReceived bytes")
                        } else {
                            failedRequests++
                            println("[WARN] Download failed - Code: ${it.code} - Message: ${it.message}")
                        }
                    }
                } catch (e: IOException) {
                    failedRequests++
                    println("[ERROR] Network error: ${e.message}")
                }
            }

            val durationSeconds = (System.nanoTime() - startTime) / 1_000_000_000.0
            val bitsReceived = totalBytesReceived * 8
            val throughputMbps = bitsReceived / durationSeconds / 1_000_000

            println(
                "[DEBUG] Test completed - " +
                        "Duration: ${"%.2f".format(durationSeconds)}s, " +
                        "Total received: $totalBytesReceived bytes, " +
                        "Successful requests: $successfulRequests, " +
                        "Failed requests: $failedRequests, " +
                        "Throughput: ${"%.2f".format(throughputMbps)} Mbps"
            )

            throughputMbps
        } catch (e: Exception) {
            println("[ERROR] Unexpected error: ${e.javaClass.simpleName} - ${e.message}")
            -1.0
        }
    }
}