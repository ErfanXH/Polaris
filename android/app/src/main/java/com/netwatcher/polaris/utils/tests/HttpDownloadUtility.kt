package com.netwatcher.polaris.utils.tests

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

object HttpDownloadUtility {
    private const val DOWNLOAD_URL = "https://polaris.work.gd/api/mobile/HTTPTest/download/"
    private const val TEST_DURATION_MS = 5000

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    suspend fun measureDownloadThroughput(token: String?): Double = withContext(Dispatchers.IO) {
        val startTime = System.nanoTime()
        var totalBytesReceived = 0L
        var successfulRequests = 0
        var failedRequests = 0

        try {
            println("Starting download ...")
            while (true) {
                val elapsedMs = (System.nanoTime() - startTime) / 1_000_000
                if (elapsedMs >= TEST_DURATION_MS) {
                    println("Test duration reached ($TEST_DURATION_MS ms)")
                    break
                }

                val request = Request.Builder()
                    .addHeader("Authorization", token.toString())
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
                            println("Download successful (${bytes.size} bytes) - Total: $totalBytesReceived bytes")
                        } else {
                            failedRequests++
                            println("Download failed - Code: ${it.code} - Message: ${it.message}")
                        }
                    }
                } catch (e: IOException) {
                    failedRequests++
                    println("Network error: ${e.message}")
                }
            }

            val durationSeconds = (System.nanoTime() - startTime) / 1_000_000_000.0
            val bitsReceived = totalBytesReceived * 8
            val throughputMbps = bitsReceived / durationSeconds / 1_000_000

            println(
                "Download Test completed - " +
                        "Duration: ${"%.2f".format(durationSeconds)}s, " +
                        "Total received: $totalBytesReceived bytes, " +
                        "Successful requests: $successfulRequests, " +
                        "Failed requests: $failedRequests, " +
                        "Throughput: ${"%.2f".format(throughputMbps)} Mbps"
            )

            throughputMbps
        } catch (e: Exception) {
            println("Error: ${e.message}")
            -1.0
        }
    }
}