package com.netwatcher.polaris.presentation.home.components

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
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
    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    DotsLoader()
                }
            }
            is HomeUiState.Error -> Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.ErrorOutline,
                    contentDescription = "Error",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = uiState.message,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
            is HomeUiState.Empty -> Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Inbox,
                    contentDescription = "No Data",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "No Test Data Available",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(24.dp))
                RunTestButton(onClick = {
                    onRunTest(TestConfigManager.getTestSelection(context))
                })
            }
            is HomeUiState.Success -> HomeContent(
                networkData = uiState.data,
                onRunTest = onRunTest,
                context = context
            )
            // This state can be integrated into the Success screen for a better UX
            is HomeUiState.LocationSuccess -> Text(
                "Lat: ${uiState.location.latitude}, Lon: ${uiState.location.longitude}",
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
