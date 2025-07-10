package com.netwatcher.polaris.presentation.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.netwatcher.polaris.domain.model.NetworkData
import com.netwatcher.polaris.domain.model.TestSelection
import com.netwatcher.polaris.presentation.home.components.KeyValueRow
import com.netwatcher.polaris.presentation.home.components.NetworkInfoCard
import com.netwatcher.polaris.presentation.home.components.RunTestButton
import com.netwatcher.polaris.utils.TestConfigManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onProfileClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogout: () -> Unit = {},
    context: Context
) {
    LaunchedEffect(Unit) {
        viewModel.loadInitialState()
    }

    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.background),
    ) {
        TopAppBar(
            title = {
                Row {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home"
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Home",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    viewModel.loadInitialState()
                }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
                IconButton(onClick = {
                    coroutineScope.launch {
                        val success = viewModel.onLogoutClick()
                        if (success) {
                            onLogout()
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = Color.Black,
                navigationIconContentColor = Color.Black,
                actionIconContentColor = Color.Black
            )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is HomeUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(MaterialTheme.colorScheme.background),
                    )
                }

                is HomeUiState.Empty -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RunTestButton(onClick = {
                            viewModel.runNetworkTest(
                                TestConfigManager.getTestSelection(context)
                            )
                        })
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No Test Data Available",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                is HomeUiState.Success -> {
                    HomeContent(
                        networkData = state.data,
                        onRunTest = { selection -> viewModel.runNetworkTest(selection) },
                        onProfileClick = onProfileClick,
                        context = context
                    )
                }

                is HomeUiState.LocationSuccess -> {
                    Text(text = "Lat: ${state.location.latitude}, Lon: ${state.location.longitude}")
                }
            }
        }
    }
}

@Composable
private fun HomeContent(
    networkData: NetworkData,
    onRunTest: (TestSelection) -> Unit,
    onProfileClick: () -> Unit,
    context: Context
) {
    var testSelection by remember {
        mutableStateOf(TestConfigManager.getTestSelection(context))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        RunTestButton(
            onClick = { onRunTest(testSelection) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TestSelectionSection(
            initialSelection = testSelection,
            onSelectionChanged = { newSelection ->
                TestConfigManager.setTestSelection(context, newSelection)
                testSelection = newSelection
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Last Test Results",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        NetworkResults(networkData = networkData)
    }
}

@Composable
private fun NetworkResults(networkData: NetworkData) {
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

@Composable
fun TestSelectionSection(
    modifier: Modifier = Modifier,
    initialSelection: TestSelection = TestSelection(),
    onSelectionChanged: (TestSelection) -> Unit
) {
    var selection by remember { mutableStateOf(initialSelection) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Select Tests to Run",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TestOption(
                label = "Upload",
                checked = selection.runUploadTest,
                onCheckedChange = {
                    selection = selection.copy(runUploadTest = it)
                    onSelectionChanged(selection)
                }
            )
            TestOption(
                label = "Ping",
                checked = selection.runPingTest,
                onCheckedChange = {
                    selection = selection.copy(runPingTest = it)
                    onSelectionChanged(selection)
                }
            )
            TestOption(
                label = "Web",
                checked = selection.runWebTest,
                onCheckedChange = {
                    selection = selection.copy(runWebTest = it)
                    onSelectionChanged(selection)
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TestOption(
                label = "Download",
                checked = selection.runDownloadTest,
                onCheckedChange = {
                    selection = selection.copy(runDownloadTest = it)
                    onSelectionChanged(selection)
                }
            )
            TestOption(
                label = "DNS",
                checked = selection.runDnsTest,
                onCheckedChange = {
                    selection = selection.copy(runDnsTest = it)
                    onSelectionChanged(selection)
                }
            )
            TestOption(
                label = "SMS",
                checked = selection.runSmsTest,
                onCheckedChange = {
                    selection = selection.copy(runSmsTest = it)
                    onSelectionChanged(selection)
                }
            )
        }
    }
}

@Composable
private fun TestOption(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onCheckedChange(!checked) }
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
