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

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun SettingsScreen(
    onSimSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val subscriptionManager = remember {
        context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
    }

    val simList = remember {
        subscriptionManager.activeSubscriptionInfoList?.map {
            SimInfo(
                displayName = it.displayName?.toString() ?: "Unknown",
                carrierName = it.carrierName?.toString() ?: "Unknown",
                simSlotIndex = it.simSlotIndex,
                subscriptionId = it.subscriptionId
            )
        } ?: emptyList()
    }

    var selectedSimId by remember { mutableStateOf<Int?>(null) }

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
            // SIM Selection Section
            Text("Select SIM Card", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            simList.forEach { sim ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedSimId = sim.subscriptionId
                            onSimSelected(sim.subscriptionId)
                        }
                        .padding(vertical = 8.dp)
                ) {
                    RadioButton(
                        selected = sim.subscriptionId == selectedSimId,
                        onClick = {
                            selectedSimId = sim.subscriptionId
                            onSimSelected(sim.subscriptionId)
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SIM ${sim.simSlotIndex + 1} (${sim.carrierName})")
                }
            }

            Divider(modifier = Modifier.padding(vertical = 24.dp))

            // Sync Interval Section
            SyncIntervalSettings(context = context)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncIntervalSettings(context: Context) {
    val preferences = remember { DataSyncScheduler.getPreferences(context) }
    val currentInterval = preferences.getLong(DataSyncScheduler.KEY_SYNC_INTERVAL, 30L)
    var selectedInterval by remember { mutableStateOf(currentInterval) }
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
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = intervals.first { it.second == selectedInterval }.first,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                intervals.forEach { (label, value) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            selectedInterval = value
                            expanded = false
                            DataSyncScheduler.updateSyncInterval(context, value)
                        }
                    )
                }
            }
        }
    }
}
