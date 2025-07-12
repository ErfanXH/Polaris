package com.netwatcher.polaris.presentation.home.components

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.netwatcher.polaris.domain.model.NetworkData
import com.netwatcher.polaris.domain.model.TestSelection
import com.netwatcher.polaris.utils.TestConfigManager

@Composable
fun HomeContent(
    networkData: NetworkData,
    onRunTest: (TestSelection) -> Unit,
    context: Context
) {
    var testSelection by remember {
        mutableStateOf(TestConfigManager.getTestSelection(context))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RunTestButton(
            onClick = { onRunTest(testSelection) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(16.dp))

        TestSelectionSection(
            initialSelection = testSelection,
            onSelectionChanged = {
                TestConfigManager.setTestSelection(context, it)
                testSelection = it
            }
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "Last Test Results",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
            )

        Spacer(Modifier.height(16.dp))

        NetworkResults(networkData = networkData)
    }
}
