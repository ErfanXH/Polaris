package com.netwatcher.polaris.utils

import com.netwatcher.polaris.di.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.Random
import java.util.concurrent.TimeUnit


object HttpUploadUtility {
    private const val UPLOAD_URL = "http://194.62.43.37/api/mobile/HTTPTest/upload/"
    private const val TEST_DURATION_MS = 5000
    private const val CHUNK_SIZE = 1024 * 32

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private val OCTET_STREAM: MediaType = "application/octet-stream".toMediaType()

    private suspend fun getAuthToken(): String {
        return TokenManager.getToken().firstOrNull().toString()
    }

    suspend fun measureUploadThroughput(): Double = withContext(Dispatchers.IO) {
        println("[DEBUG] Starting upload throughput test")

        val testData = generateTestData()
        println("[DEBUG] Generated test data: ${testData.size} bytes")

        val startTime = System.nanoTime()
        var totalBytesSent = 0L
        var offset = 0
        var successfulRequests = 0
        var failedRequests = 0

        try {
            println("[DEBUG] Starting upload loop...")

            while (true) {
                val elapsedMs = (System.nanoTime() - startTime) / 1_000_000
                if (elapsedMs >= TEST_DURATION_MS) {
                    println("[DEBUG] Test duration reached ($TEST_DURATION_MS ms)")
                    break
                }

                val remainingBytes = testData.size - offset
                if (remainingBytes <= 0) {
                    println("[DEBUG] Resetting data offset (reached end of test data)")
                    offset = 0
                }

                val chunkSize = minOf(CHUNK_SIZE, remainingBytes)
                val chunk = testData.copyOfRange(offset, offset + chunkSize)
                offset += chunkSize

                println("[DEBUG] Preparing chunk $chunkSize bytes (offset: $offset)")

                val requestBody = chunk.toRequestBody(OCTET_STREAM)
                val request = Request.Builder()
                    .addHeader("Authorization", getAuthToken())
                    .url(UPLOAD_URL)
                    .post(requestBody)
                    .build()

                try {
                    val response = client.newCall(request).execute()
                    response.use {
                        if (it.isSuccessful) {
                            totalBytesSent += chunk.size
                            successfulRequests++
                            println("[DEBUG] Chunk upload successful (${chunk.size} bytes) - Total: $totalBytesSent bytes")
                        } else {
                            failedRequests++
                            println("[WARN] Chunk upload failed - Code: ${it.code} - Message: ${it.message}")
                        }
                    }
                } catch (e: IOException) {
                    failedRequests++
                    println("[ERROR] Network error: ${e.message}")
                }
            }

            val durationSeconds = (System.nanoTime() - startTime) / 1_000_000_000.0
            val bitsSent = totalBytesSent * 8
            val throughputMbps = bitsSent / durationSeconds / 1_000_000

            println("[DEBUG] Test completed - " +
                    "Duration: ${"%.2f".format(durationSeconds)}s, " +
                    "Total sent: $totalBytesSent bytes, " +
                    "Successful requests: $successfulRequests, " +
                    "Failed requests: $failedRequests, " +
                    "Throughput: ${"%.2f".format(throughputMbps)} Mbps")

            throughputMbps
        } catch (e: Exception) {
            println("[ERROR] Unexpected error: ${e.javaClass.simpleName} - ${e.message}")
            0.0
        }
    }

    private fun generateTestData(): ByteArray {
        val size = 5 * 1024 * 1024 // 5MB
        println("[DEBUG] Generating $size bytes of test data")
        return ByteArray(size).apply {
            Random().nextBytes(this)
        }
    }
}