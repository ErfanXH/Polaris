package com.netwatcher.polaris.presentation.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.netwatcher.polaris.domain.model.SimInfo

@Composable
fun SimSelectionSection(
    simList: List<SimInfo>,
    selectedSimSlotId: Int?,
    onSimSelected: (Int) -> Unit
) {
    Text("Select SIM Card", style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(4.dp))
    simList.forEach { sim ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onSimSelected(sim.simSlotIndex)
                }
                .padding(vertical = 8.dp)
        ) {
            RadioButton(
                selected = sim.simSlotIndex == selectedSimSlotId,
                onClick = { onSimSelected(sim.simSlotIndex) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("SIM ${sim.simSlotIndex + 1}")
                Text(sim.carrierName, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
