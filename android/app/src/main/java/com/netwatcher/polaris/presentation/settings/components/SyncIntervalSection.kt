package com.netwatcher.polaris.presentation.settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.netwatcher.polaris.presentation.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncIntervalSection(viewModel: SettingsViewModel) {
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