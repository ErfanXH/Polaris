package com.netwatcher.polaris.presentation.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.netwatcher.polaris.domain.model.TestSelection

@OptIn(ExperimentalLayoutApi::class)
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
        title = "Select Tests to Run",
        icon = Icons.Outlined.Checklist
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 3
        ) {
            testItems.forEach { (label, isSelected) ->
                TestToggleButton(
                    modifier = Modifier.width(100.dp),
                    label = label,
                    selected = isSelected,
                    onCheckedChange = {
                        selection = when (label) {
                            "Upload" -> selection.copy(runUploadTest = !selection.runUploadTest)
                            "Download" -> selection.copy(runDownloadTest = !selection.runDownloadTest)
                            "SMS" -> selection.copy(runSmsTest = !selection.runSmsTest)
                            "Ping" -> selection.copy(runPingTest = !selection.runPingTest)
                            "DNS" -> selection.copy(runDnsTest = !selection.runDnsTest)
                            "Web" -> selection.copy(runWebTest = !selection.runWebTest)
                            else -> selection
                        }
                        onSelectionChanged(selection)
                    }
                )

            }
        }
    }
}