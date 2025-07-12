@file:OptIn(ExperimentalMaterial3Api::class)

package com.netwatcher.polaris.presentation.settings

import androidx.compose.foundation.layout.*
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
    onSimSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    val simList by viewModel.simList.collectAsState()
    val selectedSimId by viewModel.selectedSimId.collectAsState()

    if (simList.isNotEmpty() && selectedSimId == null) {
        LaunchedEffect(simList) {
            val defaultSimId = simList.first().subscriptionId
            viewModel.selectSim(defaultSimId)
            onSimSelected(defaultSimId)
        }
    }

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
                SimSelectionSection(
                    simList = simList,
                    selectedSimId = selectedSimId,
                    onSimSelected = {
                        viewModel.selectSim(it)
                        onSimSelected(it)
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
