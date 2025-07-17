package com.netwatcher.polaris.presentation.home.components

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        item {
            RunTestButton(onClick = { onRunTest(testSelection) })
        }

        item {
            TestSelectionSection(
                initialSelection = testSelection,
                onSelectionChanged = {
                    TestConfigManager.setTestSelection(context, it)
                    testSelection = it
                }
            )
        }

        networkResults(networkData)
    }
}