package com.netwatcher.polaris.utils.tests

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

object WebTestUtility {
    private val DEFAULT_TEST_URL = "https://www.google.com"

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    suspend fun measureWebResponseTime(url: String?): Double? {
        val testUrl = prepareUrl(url) ?: return null

        return withContext(Dispatchers.IO) {
            try {
                Log.d("WebTestUtility", "Starting web response measurement for $testUrl")

                val request = Request.Builder()
                    .url(testUrl)
                    .header("Cache-Control", "no-cache")
                    .header("Pragma", "no-cache")
                    .build()

                val startTime = System.currentTimeMillis()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException("Unexpected response code: ${response.code}")
                    }
                    response.body?.string()
                }
                val time = System.currentTimeMillis() - startTime

                Log.d("WebTestUtility", "Successfully measured web response time: ${time}ms")
                time.toDouble()
            } catch (e: Exception) {
                Log.e("WebTestUtility", "Error measuring web response time for $testUrl: ${e.message}", e)
                null
            }
        }
    }

    private fun prepareUrl(url: String?): String? {
        if (url.isNullOrBlank()) {
            Log.w("WebTestUtility", "No URL provided, using default: $DEFAULT_TEST_URL")
            return DEFAULT_TEST_URL
        }

        return when {
            url.startsWith("http://") || url.startsWith("https://") -> url
            url.startsWith("www.") -> "https://$url"
            else -> {
                Log.w("WebTestUtility", "URL missing protocol, prepending https://")
                "https://$url"
            }
        }.also { preparedUrl ->
            if (!isValidUrl(preparedUrl)) {
                Log.e("WebTestUtility", "Invalid URL format: $preparedUrl")
                return null
            }
        }
    }

    private fun isValidUrl(url: String): Boolean {
        return try {
            Request.Builder().url(url).build()
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}