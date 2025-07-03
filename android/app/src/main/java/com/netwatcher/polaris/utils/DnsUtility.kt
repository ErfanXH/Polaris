package com.netwatcher.polaris.utils

import android.util.Log
import java.net.InetAddress
import java.net.UnknownHostException

object DnsUtility {
    private const val DEFAULT_DNS_HOST = "google.com"
    fun measureDnsResolution(hostname: String): Double? {
        return try {
            Log.d("DNS", "Starting DNS Response Measurement for $hostname")
            val startTime = System.currentTimeMillis()
            InetAddress.getAllByName(hostname)
            (System.currentTimeMillis() - startTime).toDouble()
        } catch (e: UnknownHostException) {
            null
        } catch (e: SecurityException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    fun measureDnsResolutionWithRetry(
        hostname: String,
        retries: Int = 3
    ): Double? {
        val dnsHost = when {
            hostname.isNullOrBlank() -> {
                Log.w("PING", "No host provided, using default: $DEFAULT_DNS_HOST")
                DEFAULT_DNS_HOST
            }
            else -> hostname
        }
        var lastError: Exception? = null
        repeat(retries) {
            try {
                return measureDnsResolution(dnsHost)
            } catch (e: Exception) {
                lastError = e
            }
        }
        lastError?.printStackTrace()
        return null
    }
}