package eh.learning.homepage.utils

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.regex.Pattern

object PingUtility {
    fun ping(host: String, count: Int = 3): Double? {
        return try {
            val process = ProcessBuilder("ping", "-c", count.toString(), host).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val output = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }

            process.waitFor()

            parsePingOutput(output.toString())
        } catch (e: Exception) {
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