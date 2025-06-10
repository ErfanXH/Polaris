package eh.learning.homepage.utils

import java.net.InetAddress
import java.net.UnknownHostException

object DnsUtility {
    fun measureDnsResolution(hostname: String = "google.com"): Long? {
        return try {
            val startTime = System.currentTimeMillis()
            InetAddress.getAllByName(hostname)
            System.currentTimeMillis() - startTime
        } catch (e: UnknownHostException) {
            null
        } catch (e: SecurityException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    fun measureDnsResolutionWithRetry(
        hostname: String = "google.com",
        retries: Int = 3
    ): Long? {
        var lastError: Exception? = null
        repeat(retries) {
            try {
                return measureDnsResolution(hostname)
            } catch (e: Exception) {
                lastError = e
            }
        }
        lastError?.printStackTrace()
        return null
    }
}