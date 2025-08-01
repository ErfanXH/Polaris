package com.netwatcher.polaris.presentation.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.netwatcher.polaris.domain.model.TestSelection

@Composable
fun TestSelectionSection(
    modifier: Modifier = Modifier,
    initialSelection: TestSelection = TestSelection(),
    onSelectionChanged: (TestSelection) -> Unit
) {
    var selection by remember { mutableStateOf(initialSelection) }

    val testItems = listOf(
        "Upload" to selection.runUploadTest,
        "Download" to selection.runDownloadTest,
        "SMS" to selection.runSmsTest,
        "Ping" to selection.runPingTest,
        "DNS" to selection.runDnsTest,
        "Web" to selection.runWebTest
    )

    NetworkInfoCard(
        modifier = modifier,
        title = "Select Tests",
        icon = Icons.Outlined.Checklist
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                testItems.take(3).forEach { (label, isSelected) ->
                    TestToggleButton(
                        modifier = Modifier.weight(1f),
                        label = label,
                        selected = isSelected,
                        onCheckedChange = {
                            selection = updateSelection(selection, label)
                            onSelectionChanged(selection)
                        }
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                testItems.drop(3).forEach { (label, isSelected) ->
                    TestToggleButton(
                        modifier = Modifier.weight(1f),
                        label = label,
                        selected = isSelected,
                        onCheckedChange = {
                            selection = updateSelection(selection, label)
                            onSelectionChanged(selection)
                        }
                    )
                }
            }
        }
    }
}

private fun updateSelection(current: TestSelection, label: String): TestSelection {
    return when (label) {
        "Upload" -> current.copy(runUploadTest = !current.runUploadTest)
        "Download" -> current.copy(runDownloadTest = !current.runDownloadTest)
        "SMS" -> current.copy(runSmsTest = !current.runSmsTest)
        "Ping" -> current.copy(runPingTest = !current.runPingTest)
        "DNS" -> current.copy(runDnsTest = !current.runDnsTest)
        "Web" -> current.copy(runWebTest = !current.runWebTest)
        else -> current
    }
}