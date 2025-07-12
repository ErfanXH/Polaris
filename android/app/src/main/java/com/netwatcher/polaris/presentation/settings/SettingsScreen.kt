@file:OptIn(ExperimentalMaterial3Api::class)

package com.netwatcher.polaris.presentation.settings

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.SubscriptionManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.netwatcher.polaris.domain.model.SimInfo
import com.netwatcher.polaris.utils.DataSyncScheduler
import com.netwatcher.polaris.utils.TestConfigManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onSimSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    val simList by viewModel.simList.collectAsState()
    val selectedSimId by viewModel.selectedSimId.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (simList.size > 1) {
                Text("Select SIM Card", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                simList.forEach { sim ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectSim(sim.subscriptionId)
                                onSimSelected(sim.subscriptionId)
                            }
                            .padding(vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = sim.subscriptionId == selectedSimId,
                            onClick = {
                                viewModel.selectSim(sim.subscriptionId)
                                onSimSelected(sim.subscriptionId)
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("SIM ${sim.simSlotIndex + 1}")
                            Text(sim.carrierName, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }

            SyncIntervalSettings(viewModel = viewModel)
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            TestConfigurationSettings(viewModel = viewModel)
        }
    }
}

@Composable
fun SyncIntervalSettings(viewModel: SettingsViewModel) {
    val selectedInterval by viewModel.selectedInterval.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    val intervals = listOf(
        "15 minutes" to 15L,
        "30 minutes" to 30L,
        "1 hour" to 60L,
        "6 hours" to 360L,
        "12 hours" to 720L,
        "24 hours" to 1440L
    )

    Column {
        Text("Background Sync Interval", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = intervals.first { it.second == selectedInterval }.first,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                intervals.forEach { (label, value) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            viewModel.updateSyncInterval(value)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TestConfigurationSettings(viewModel: SettingsViewModel) {
    val context = LocalContext.current
    val prefs = remember { TestConfigManager.getPreferences(context) }

    var smsTestNumber by remember {
        mutableStateOf(prefs.getString(TestConfigManager.KEY_SMS_TEST_NUMBER, "+989303009264") ?: "")
    }
    var pingTestAddress by remember {
        mutableStateOf(prefs.getString(TestConfigManager.KEY_PING_TEST_ADDRESS, "8.8.8.8") ?: "")
    }
    var dnsTestAddress by remember {
        mutableStateOf(prefs.getString(TestConfigManager.KEY_DNS_TEST_ADDRESS, "google.com") ?: "")
    }
    var webTestAddress by remember {
        mutableStateOf(prefs.getString(TestConfigManager.KEY_WEB_TEST_ADDRESS, "https://www.google.com") ?: "")
    }

    Column {
        Text("Test Configuration", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = smsTestNumber,
            onValueChange = {
                smsTestNumber = it
                viewModel.setSmsTestNumber(it)
            },
            label = { Text("SMS Test Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = pingTestAddress,
            onValueChange = {
                pingTestAddress = it
                viewModel.setPingAddress(it)
            },
            label = { Text("Ping Test Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = dnsTestAddress,
            onValueChange = {
                dnsTestAddress = it
                viewModel.setDnsAddress(it)
            },
            label = { Text("DNS Test Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = webTestAddress,
            onValueChange = {
                webTestAddress = it
                viewModel.setWebAddress(it)
            },
            label = { Text("Web Test Address") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
