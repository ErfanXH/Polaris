package eh.learning.homepage.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.Random
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

object ThroughputUtility {
    private const val UPLOAD_URL = "https://httpbin.org/post" // Public test server
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

     // Measures upload throughput in Mbps
    suspend fun measureUploadThroughput(): Double? = withContext(Dispatchers.IO) {
        try {
            val testData = generateTestData()
            val startTime = System.nanoTime()
            var totalBytesSent = 0L

            while ((System.nanoTime() - startTime) / 1_000_000 < TEST_DURATION_MS) {
                val chunk = testData.copyOfRange(0, minOf(CHUNK_SIZE, testData.size))
                val requestBody = chunk.toRequestBody(OCTET_STREAM)

                val request = Request.Builder()
                    .url(UPLOAD_URL)
                    .post(requestBody)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        return@withContext null
                    }
                    totalBytesSent += chunk.size
                }
            }

            // Calculate throughput in Mbps
            val durationSeconds = (System.nanoTime() - startTime).toDouble() / 1_000_000_000
            val bitsSent = totalBytesSent * 8
            (bitsSent / durationSeconds / 1_000_000)
        } catch (e: IOException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun generateTestData(): ByteArray {
        // Generate 5MB of random test data
        val size = 5 * 1024 * 1024
        return ByteArray(size).apply {
            Random().nextBytes(this)
        }
    }
}