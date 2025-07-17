package com.netwatcher.polaris.presentation.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CellTower
import androidx.compose.material.icons.outlined.PersonPinCircle
import com.netwatcher.polaris.domain.model.NetworkData

fun LazyListScope.networkResults(networkData: NetworkData) {
    item {
        KeyMetricCard(networkData)
    }

    val sections = listOf(
        Triple(
            "User Info", Icons.Outlined.PersonPinCircle, listOf(
                "Latitude" to networkData.latitude.toString(),
                "Longitude" to networkData.longitude.toString(),
                "Time" to networkData.timestamp
            )
        ),
        Triple("Cell Info", Icons.Outlined.CellTower, when (networkData.networkType) {
            "LTE", "5G" -> listOf(
                "Technology" to (networkData.networkType),
                "TAC" to (networkData.tac ?: "N/A"),
                "Cell ID" to (networkData.cellId ?: "N/A"),
                "PLMN ID" to (networkData.plmnId ?: "N/A"),
                "Frequency" to (networkData.frequency?.let { String.format("%.2f MHz", it) } ?: "N/A"),
                "ARFCN" to (networkData.arfcn?.toString() ?: "N/A"),
                "Frequency Band" to (networkData.frequencyBand ?: "N/A")
            )
            else -> listOf(
                "Technology" to (networkData.networkType),
                "LAC" to (networkData.lac ?: "N/A"),
                "Cell ID" to (networkData.cellId ?: "N/A"),
                "PLMN ID" to (networkData.plmnId ?: "N/A"),
                "Frequency" to (networkData.frequency?.let { String.format("%.2f MHz", it) } ?: "N/A"),
                "ARFCN" to (networkData.arfcn?.toString() ?: "N/A"),
                "Frequency Band" to (networkData.frequencyBand ?: "N/A")
            )
        }),
        Triple("Signal Quality", Icons.Outlined.BarChart, when (networkData.networkType) {
            "LTE" -> listOf(
                "RSRP" to (networkData.rsrp?.let { String.format("%d dBm", it) } ?: "N/A"),
                "RSRQ" to (networkData.rsrq?.let { String.format("%d dB", it) } ?: "N/A"),
            )
            "WCDMA", "HSPA", "HSPA+", "HSDPA", "HSUPA", "UMTS", "3G" -> listOf(
                "RSCP" to (networkData.rscp?.let { String.format("%d dBm", it) } ?: "N/A"),
                "Ec/N0" to (networkData.ecIo?.let { String.format("%d dBm", it) } ?: "N/A")
            )
            "GSM", "GPRS", "EDGE", "CDMA" -> listOf(
                "RxLev" to (networkData.rxLev?.let { String.format("%d dBm", it) } ?: "N/A")
            )
            else -> emptyList()
        })
    )

    sections.forEach { (title, icon, items) ->
        if (items.isNotEmpty()) {
            item(key = title) {
                NetworkInfoCard(title = title, icon = icon) {
                    Column {
                        items.forEach { (key, value) ->
                            KeyValueRow(key = key, value = value ?: "N/A")
                        }
                    }
                }
            }
        }
    }
}