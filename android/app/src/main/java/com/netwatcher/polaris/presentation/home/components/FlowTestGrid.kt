package com.netwatcher.polaris.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FlowTestGrid(
    items: List<Triple<String, Boolean, (Boolean) -> Unit>>
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (row in items.chunked(3)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { (label, selected, onToggle) ->
                    TestToggleCard(
                        label = label,
                        selected = selected,
                        onCheckedChange = { onToggle(it) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Fill remaining space if row has less than 3 items
                repeat(3 - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
