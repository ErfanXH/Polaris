package com.netwatcher.polaris.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

object WebTestUtility {
    private val TAG = "WebTestUtility"
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    suspend fun measureWebResponseTime(url: String = "https://www.google.com"): Long? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Starting web response measurement for $url")

                val request = Request.Builder()
                    .url(url)
                    .header("Cache-Control", "no-cache")
                    .build()

                val startTime = System.currentTimeMillis()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException("Unexpected response code: ${response.code}")
                    }
                    response.body?.string()
                }
                val time = System.currentTimeMillis() - startTime

                Log.d(TAG, "Successfully measured web response time: ${time}ms")
                time
            } catch (e: Exception) {
                Log.e(TAG, "Error measuring web response time: ${e.message}", e)
                null
            }
        }
    }
}