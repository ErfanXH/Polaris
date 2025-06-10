package com.netwatcher.polaris.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.netwatcher.polaris.domain.model.NetworkData
import com.netwatcher.polaris.presentation.home.components.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onProfileClick: () -> Unit = {}
) {

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
            .padding(horizontal = 8.dp)
    ) {
        IconButton(
            onClick = onProfileClick,
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile"
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RunTestButton(
                onClick = onRunTest,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "نتیجه آخرین تست",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            NetworkResults(networkData = networkData)
        }
    }
}
@Composable
private fun NetworkResults(networkData: NetworkData) {
    val sections = listOf(
        Triple("مکان کاربر", listOf(
            "عرض جغرافیایی" to networkData.latitude.toString(),
            "طول جغرافیایی" to networkData.longitude.toString(),
            "زمان ثبت" to networkData.timestamp
        )) { _: NetworkData -> /* No special handling */ },

        Triple("فناوری سلولی", when (networkData.networkType) {
            "LTE", "5G" -> listOf(
            "فناوری" to networkData.networkType,
            "TAC" to (networkData.tac ?: "N/A"),
            "شناسه سلول" to (networkData.cellId ?: "N/A"),
            "PLMN شناسه" to (networkData.plmnId ?: "N/A"),
            "ARFCN" to "${networkData.arfcn} (${networkData.frequency} MHz)",
            "باند فرکانسی" to (networkData.frequencyBand ?: "N/A")
            )
            "WCDMA", "HSPA", "HSPA+", "GSM", "GPRS", "EDGE" -> listOf(
                "فناوری" to networkData.networkType,
                "LAC" to (networkData.lac ?: "N/A"),
                "RAC" to (networkData.rac ?: "N/A"),
                "شناسه سلول" to (networkData.cellId ?: "N/A"),
                "PLMN شناسه" to (networkData.plmnId ?: "N/A"),
                "ARFCN" to "${networkData.arfcn} (${networkData.frequency} MHz)",
                "باند فرکانسی" to (networkData.frequencyBand ?: "N/A")
            )
            else -> emptyList()
        }) { _: NetworkData -> /* No special handling */ },

        Triple("کیفیت سیگنال", when (networkData.networkType) {
            "LTE" -> listOf(
                "RSRP" to "${networkData.rsrp} dBm",
                "RSRQ" to "${networkData.rsrq} dB",
//                "SINR" to (networkData.sinr?.toString() ?: "N/A")
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
//                "SS-SINR" to (networkData.ssSinr?.toString() ?: "N/A")
            )
            else -> emptyList()
        }) { _: NetworkData -> /* No special handling */ },

        Triple("تست های عملکردی", listOf(
            "HTTP گذردهی" to "${networkData.httpThroughput} Mbps",
            "Ping" to "${networkData.pingTime} ms",
            "DNS زمان پاسخ" to "${networkData.dnsResponse} ms",
            "Web زمان پاسخ" to "${networkData.webResponse} ms",
            "SMS زمان تحویل" to "${networkData.smsDeliveryTime} ms"
        )) { _: NetworkData -> /* No special handling */ }
    )

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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