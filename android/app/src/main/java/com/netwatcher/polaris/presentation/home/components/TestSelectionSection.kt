package com.netwatcher.polaris.presentation.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.netwatcher.polaris.domain.model.TestSelection

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
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Select Executing Tests",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        val testItems = listOf(
            Triple("Upload", selection.runUploadTest) { value: Boolean ->
                selection = selection.copy(runUploadTest = value)
                onSelectionChanged(selection)
            },
            Triple("Download", selection.runDownloadTest) { value: Boolean ->
                selection = selection.copy(runDownloadTest = value)
                onSelectionChanged(selection)
            },
            Triple("Ping", selection.runPingTest) { value: Boolean ->
                selection = selection.copy(runPingTest = value)
                onSelectionChanged(selection)
            },
            Triple("DNS", selection.runDnsTest) { value: Boolean ->
                selection = selection.copy(runDnsTest = value)
                onSelectionChanged(selection)
            },
            Triple("Web", selection.runWebTest) { value: Boolean ->
                selection = selection.copy(runWebTest = value)
                onSelectionChanged(selection)
            },
            Triple("SMS", selection.runSmsTest) { value: Boolean ->
                selection = selection.copy(runSmsTest = value)
                onSelectionChanged(selection)
            }
        )

        FlowTestGrid(testItems)
    }
}
