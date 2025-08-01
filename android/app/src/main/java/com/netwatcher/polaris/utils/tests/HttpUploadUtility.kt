package com.netwatcher.polaris.utils.tests

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

object HttpUploadUtility {
    private const val UPLOAD_URL = "http://194.62.43.37/api/mobile/HTTPTest/upload/"
    private const val TEST_DURATION_MS = 5000
    private const val CHUNK_SIZE = 1024 * 512   // 512KB
    private const val TEST_DATA_SIZE = 3072 * 1024  // 3MB

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private val OCTET_STREAM: MediaType = "application/octet-stream".toMediaType()

    suspend fun measureUploadThroughput(token: String?): Double = withContext(Dispatchers.IO) {
        val testData = generateTestData()
        val startTime = System.nanoTime()
        var totalBytesSent = 0L
        var offset = 0

        try {
            val requestBuilder = Request.Builder()
                .addHeader("Authorization", token.toString())
                .url(UPLOAD_URL)

            while ((System.nanoTime() - startTime) / 1_000_000 < TEST_DURATION_MS) {
                val remainingBytes = testData.size - offset
                if (remainingBytes <= 0) {
                    offset = 0
                    continue
                }

                val chunkSize = minOf(CHUNK_SIZE, remainingBytes)
                val chunk = testData.copyOfRange(offset, offset + chunkSize)
                offset += chunkSize

                val request = requestBuilder
                    .post(chunk.toRequestBody(OCTET_STREAM))
                    .build()

                try {
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            totalBytesSent += chunk.size
                        }
                    }
                } catch (e: IOException) {
                    continue
                }
            }

            calculateThroughput(startTime, totalBytesSent)
        } catch (e: Exception) {
            -1.0
        }
    }

    private fun calculateThroughput(startTime: Long, bytesSent: Long): Double {
        val durationSeconds = (System.nanoTime() - startTime) / 1_000_000_000.0
        return (bytesSent * 8) / (durationSeconds * 1_000_000)  // Mbps
    }

    private fun generateTestData(): ByteArray {
        return ByteArray(TEST_DATA_SIZE).apply {
            val pattern = "POLARIS_PATTERN_".toByteArray()
            for (i in indices) {
                this[i] = pattern[i % pattern.size]
            }
        }
    }
}