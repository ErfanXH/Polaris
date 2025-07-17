package com.netwatcher.polaris.presentation.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NetworkCheck
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.netwatcher.polaris.domain.model.NetworkData

@Composable
fun KeyMetricCard(networkData: NetworkData) {
    val functionalTestData = listOf(
        listOf(
            "Download" to (networkData.httpDownloadThroughput?.takeIf { it != -1.0 }?.let { "%.2f".format(it) } ?: "N/A"),
            "Upload" to (networkData.httpUploadThroughput?.takeIf { it != -1.0 }?.let { "%.2f".format(it) } ?: "N/A"),
            "SMS" to (networkData.smsDeliveryTime?.takeIf { it != -1.0 }?.let { "%.1f".format(it) } ?: "N/A")
        ),
        listOf(
            "DNS" to (networkData.dnsResponse?.takeIf { it != -1.0 }?.let { "%.1f".format(it) } ?: "N/A"),
            "Web" to (networkData.webResponse?.takeIf { it != -1.0 }?.let { "%.2f".format(it) } ?: "N/A"),
            "Ping" to (networkData.pingTime?.takeIf { it != -1.0 }?.let { "%.1f".format(it) } ?: "N/A")
        )
    )
    val units = listOf(listOf("Mbps", "Mbps", "ms"), listOf("ms", "ms", "ms"))

    NetworkInfoCard(title = "Functional Tests", icon = Icons.Outlined.NetworkCheck) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            functionalTestData[0].forEachIndexed { index, (label, value) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = units[0][index],
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = MaterialTheme.colorScheme.inverseOnSurface)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            functionalTestData[1].forEachIndexed { index, (label, value) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = units[1][index],
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}