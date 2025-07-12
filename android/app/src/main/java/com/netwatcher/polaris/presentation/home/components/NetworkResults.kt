package com.netwatcher.polaris.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.netwatcher.polaris.domain.model.NetworkData

@Composable
fun NetworkResults(networkData: NetworkData) {
    val sections = listOf(
        Triple(
            "User Info", listOf(
                "Latitude" to networkData.latitude.toString(),
                "Longitude" to networkData.longitude.toString(),
                "Time" to networkData.timestamp
            )
        ) { _: NetworkData -> },

        Triple("Cell Info", when (networkData.networkType) {
            "LTE", "IWLAN", "5G" -> listOf(
                "Technology" to (networkData.networkType),
                "TAC" to (networkData.tac ?: "N/A"),
                "Cell ID" to (networkData.cellId ?: "N/A"),
                "PLMN ID" to (networkData.plmnId ?: "N/A"),
                "Frequency" to (networkData.frequency?.let { String.format("%.2f MHz", it) }
                    ?: "N/A"),
                "ARFCN" to (networkData.arfcn?.toString() ?: "N/A"),
                "Frequency Band" to (networkData.frequencyBand ?: "N/A")
            )
            "WCDMA", "HSPA", "HSPA+", "HSDPA", "HSUPA", "UMTS" -> listOf(
                "Technology" to (networkData.networkType),
                "LAC" to (networkData.lac ?: "N/A"),
                "RAC" to (networkData.rac ?: "N/A"),
                "Cell ID" to (networkData.cellId ?: "N/A"),
                "PLMN ID" to (networkData.plmnId ?: "N/A"),
                "Frequency" to (networkData.frequency?.let { String.format("%.2f MHz", it) }
                    ?: "N/A"),
                "ARFCN" to (networkData.arfcn?.toString() ?: "N/A"),
                "Frequency Band" to (networkData.frequencyBand ?: "N/A")
            )
            "GSM", "GPRS", "EDGE", "CDMA" -> listOf(
                "Technology" to (networkData.networkType),
                "LAC" to (networkData.lac ?: "N/A"),
                "RAC" to (networkData.rac ?: "N/A"),
                "Cell ID" to (networkData.cellId ?: "N/A"),
                "PLMN ID" to (networkData.plmnId ?: "N/A"),
                "Frequency" to (networkData.frequency?.let { String.format("%.2f MHz", it) }
                    ?: "N/A"),
                "ARFCN" to (networkData.arfcn?.toString() ?: "N/A"),
                "Frequency Band" to (networkData.frequencyBand ?: "N/A")
            )

            else -> emptyList()
        }) { _: NetworkData -> },

        Triple("Signal Quality", when (networkData.networkType) {
            "LTE" -> listOf(
                "RSRP" to (networkData.rsrp?.let { String.format("%01d dBm", it) } ?: "N/A"),
                "RSRQ" to (networkData.rsrq?.let { String.format("%01d dB", it) } ?: "N/A"),
            )
            "WCDMA", "HSPA", "HSPA+", "HSDPA", "HSUPA", "UMTS" -> listOf(
                "RSCP" to (networkData.rscp?.let { String.format("%01d dBm", it) } ?: "N/A"),
                "Ec/N0" to (networkData.ecIo?.let { String.format("%01d dBm", it) } ?: "N/A")
            )
            "GSM", "GPRS", "EDGE", "CDMA" -> listOf(
                "RxLev" to (networkData.rxLev?.let { String.format("%01d dBm", it) } ?: "N/A")
            )

            "5G" -> listOf(
                "SS-RSRP" to (networkData.ssRsrp?.let { String.format("%01d dBm", it) } ?: "N/A"),
            )

            else -> emptyList()
        }) { _: NetworkData -> },

        Triple("Functional Tests", listOf(
            "HTTP Upload Throughput" to (
                    networkData.httpUploadThroughput?.takeIf { it != -1.0 }
                        ?.let { String.format("%.2f Mbps", it) } ?: "N/A"
                    ),
            "HTTP Download Throughput" to (
                    networkData.httpDownloadThroughput?.takeIf { it != -1.0 }
                        ?.let { String.format("%.2f Mbps", it) } ?: "N/A"
                    ),
            "Ping Time" to (
                    networkData.pingTime?.takeIf { it != -1.0 }
                        ?.let { String.format("%.2f ms", it) } ?: "N/A"
                    ),
            "DNS Response Time" to (
                    networkData.dnsResponse?.takeIf { it != -1.0 }
                        ?.let { String.format("%.2f ms", it) } ?: "N/A"
                    ),
            "Web Response Time" to (
                    networkData.webResponse?.takeIf { it != -1.0 }
                        ?.let { String.format("%.2f ms", it) } ?: "N/A"
                    ),
            "SMS Response Time" to (
                    networkData.smsDeliveryTime?.takeIf { it != -1.0 }
                        ?.let { String.format("%.2f ms", it) } ?: "N/A"
                    )
        )) { _: NetworkData -> }
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        sections.forEach { (title, items, _) ->
            item(key = title) {
                NetworkInfoCard(title = title) {
                    Column {
                        items.forEach { (key, value) ->
                            KeyValueRow(key = key, value = value)
                            if (key != items.last().first) {
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}