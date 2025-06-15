package com.netwatcher.polaris.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.FlowColumnScopeInstance.weight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.netwatcher.polaris.domain.model.NetworkData
import com.netwatcher.polaris.presentation.home.components.KeyValueRow
import com.netwatcher.polaris.presentation.home.components.NetworkInfoCard
import com.netwatcher.polaris.presentation.home.components.RunTestButton

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onProfileClick: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        viewModel.loadInitialState()
    }

    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is HomeUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is HomeUiState.Empty -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No test data available")
                    RunTestButton(onClick = { viewModel.runNetworkTest() })
                }
            }
            is HomeUiState.Success -> {
                HomeContent(
                    networkData = state.data,
                    onRunTest = { viewModel.runNetworkTest() },
                    onProfileClick = onProfileClick
                )
            }
            is HomeUiState.LocationSuccess -> {
                Text(text = "Lat: ${state.location.latitude}, Lon: ${state.location.longitude}")
            }
        }
    }
}

@Composable
private fun HomeContent(
    networkData: NetworkData,
    onRunTest: () -> Unit,
    onProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RunTestButton(
            onClick = onRunTest,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Last Test Results",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        NetworkResults(networkData = networkData)
    }
}
@Composable
private fun NetworkResults(networkData: NetworkData) {
    val sections = listOf(
        Triple("User Info", listOf(
            "Latitude" to networkData.latitude.toString(),
            "Longitude" to networkData.longitude.toString(),
            "Date Time" to networkData.timestamp
        )) { _: NetworkData -> /* No special handling */ },

        Triple("Cell Info", when (networkData.networkType) {
            "LTE", "5G" -> listOf(
            "Technology" to networkData.networkType,
            "TAC" to (networkData.tac ?: "N/A"),
            "Cell ID" to (networkData.cellId ?: "N/A"),
            "PLMN ID" to (networkData.plmnId ?: "N/A"),
            "ARFCN" to "${networkData.arfcn} (${networkData.frequency} MHz)",
            "Frequency Band" to (networkData.frequencyBand ?: "N/A")
            )
            "WCDMA", "HSPA", "HSPA+", "GSM", "GPRS", "EDGE" -> listOf(
                "Technology" to networkData.networkType,
                "LAC" to (networkData.lac ?: "N/A"),
                "RAC" to (networkData.rac ?: "N/A"),
                "Cell ID" to (networkData.cellId ?: "N/A"),
                "PLMN ID" to (networkData.plmnId ?: "N/A"),
                "ARFCN" to "${networkData.arfcn} (${networkData.frequency} MHz)",
                "Frequency Band" to (networkData.frequencyBand ?: "N/A")
            )
            else -> emptyList()
        }) { _: NetworkData -> /* No special handling */ },

        Triple("Signal Quality", when (networkData.networkType) {
            "LTE" -> listOf(
                "RSRP" to "${networkData.rsrp} dBm",
                "RSRQ" to "${networkData.rsrq} dB",
            )
            "WCDMA", "HSPA", "HSPA+" -> listOf(
                "RSCP" to "${networkData.rscp} dBm",
                "Ec/Io" to (networkData.ecIo?.toString() ?: "N/A")
            )
            "GSM", "GPRS", "EDGE" -> listOf(
                "RxLev" to "${networkData.rxLev} dBm"
            )
            "5G" -> listOf(
                "SS-RSRP" to (networkData.ssRsrp?.toString() ?: "N/A"),
            )
            else -> emptyList()
        }) { _: NetworkData -> /* No special handling */ },

        Triple("Functional Tests", listOf(
            "HTTP Upload Throughput" to "${String.format("%.2f", networkData.httpUploadThroughput)} Mbps",
            "HTTP Download Throughput" to "${String.format("%.2f", networkData.httpDownloadThroughput)} Mbps",
            "Ping" to "${networkData.pingTime} ms",
            "DNS Response Time" to "${networkData.dnsResponse} ms",
            "Web Response Time" to "${networkData.webResponse} ms",
            "SMS Delivery Time" to "${networkData.smsDeliveryTime} ms"
        )) { _: NetworkData -> /* No special handling */ }
    )

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        sections.forEach { (title, items, _) ->
            item(key = title) {  // Added key parameter
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