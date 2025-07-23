package com.netwatcher.polaris.utils.tests

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.regex.Pattern

object PingUtility {
    private const val DEFAULT_PING_HOST = "8.8.8.8"
    fun ping(host: String?, count: Int = 3): Double? {
        return try {
            Log.d("PING", "Starting PING Response Measurement for $host")
            val pingHost = when {
                host.isNullOrBlank() -> {
                    Log.w("PING", "No host provided, using default: $DEFAULT_PING_HOST")
                    DEFAULT_PING_HOST
                }
                else -> host
            }

            val process = ProcessBuilder("ping", "-c", count.toString(), pingHost).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }

            process.waitFor()

            parsePingOutput(output.toString())
        } catch (e: Exception) {
            Log.e("PING", "$e")
            null
        }
    }

    private fun parsePingOutput(output: String): Double? {
        val pattern = Pattern.compile("min/avg/max/mdev = [\\d.]+/([\\d.]+)/[\\d.]+/[\\d.]+")
        val matcher = pattern.matcher(output)

        return if (matcher.find()) {
            matcher.group(1)?.toDouble()
        } else {
            null
        }
    }
}