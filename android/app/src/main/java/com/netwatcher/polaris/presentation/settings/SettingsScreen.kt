@file:OptIn(ExperimentalMaterial3Api::class)

package com.netwatcher.polaris.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.netwatcher.polaris.presentation.settings.components.SimSelectionSection
import com.netwatcher.polaris.presentation.settings.components.SyncIntervalSection
import com.netwatcher.polaris.presentation.settings.components.TestConfigurationSection
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onSimSelected: (Int, Int) -> Unit,
    onBack: () -> Unit
) {
    val simList by viewModel.simList.collectAsState()
    val selectedSimSlotId by viewModel.selectedSimSlotId.collectAsState()
    val selectedSimSubsId by viewModel.selectedSimSubsId.collectAsState()

    if (simList.isNotEmpty() && selectedSimSlotId == null) {
        LaunchedEffect(simList) {
            val defaultSimSlotId = simList.first().simSlotIndex
            val defaultSimSubsId = simList.first().subscriptionId
            viewModel.selectSim(defaultSimSlotId, defaultSimSubsId)
            onSimSelected(defaultSimSlotId, defaultSimSubsId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", style = MaterialTheme.typography.titleMedium,) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back",)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(1) {
                if (simList.size > 1) {
                    SimSelectionSection(
                        simList = simList,
                        selectedSimSubsId = selectedSimSubsId,
                        selectedSimSlotId = selectedSimSlotId,
                        onSimSelected = { slotId, subsId ->
                            viewModel.selectSim(slotId, subsId)
                            onSimSelected(slotId, subsId)
                        }
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }

                SyncIntervalSection(viewModel = viewModel)
                Divider(modifier = Modifier.padding(vertical = 16.dp))

                TestConfigurationSection(viewModel = viewModel)
            }
        }
    }
}
