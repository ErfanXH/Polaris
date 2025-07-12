package com.netwatcher.polaris.presentation.home.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.netwatcher.polaris.domain.model.TestSelection
import com.netwatcher.polaris.presentation.home.HomeUiState
import com.netwatcher.polaris.utils.TestConfigManager

@Composable
fun HomeStateContent(
    uiState: HomeUiState,
    onRunTest: (TestSelection) -> Unit,
    context: Context
) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        when (uiState) {
            is HomeUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            is HomeUiState.Error -> Text(uiState.message, Modifier.align(Alignment.Center))
            is HomeUiState.Empty -> Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RunTestButton(onClick = {
                    onRunTest(TestConfigManager.getTestSelection(context))
                })
                Spacer(Modifier.height(16.dp))
                Text("No Test Data Available")
            }

            is HomeUiState.Success -> HomeContent(
                networkData = uiState.data,
                onRunTest = onRunTest,
                context = context
            )

            is HomeUiState.LocationSuccess -> Text(
                "Lat: ${uiState.location.latitude}, Lon: ${uiState.location.longitude}",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
