package com.netwatcher.polaris.presentation.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.netwatcher.polaris.presentation.home.components.HomeStateContent
import com.netwatcher.polaris.presentation.home.components.HomeTopBar
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSettingsClick: () -> Unit,
    onLogout: () -> Unit,
    onPermissionsClick: () -> Unit,
    context: Context
) {
    LaunchedEffect(Unit) { viewModel.loadInitialState() }
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.background(Color.White)) {
        HomeTopBar(
            onRefresh = { viewModel.loadInitialState() },
            onSettingsClick = onSettingsClick,
            onPermissionsClick = onPermissionsClick,
            onLogoutClick = {
                coroutineScope.launch {
                    if (viewModel.onLogoutClick()) onLogout()
                }
            },
        )

        HomeStateContent(
            uiState = uiState,
            onRunTest = { viewModel.runNetworkTest(it) },
            context = context
        )
    }
}
