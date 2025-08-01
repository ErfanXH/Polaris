package com.netwatcher.polaris.presentation.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.netwatcher.polaris.presentation.home.components.HomeStateContent
import com.netwatcher.polaris.presentation.home.components.HomeTopBar
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    onSettingsClick: () -> Unit,
    onLogout: () -> Unit,
    onPermissionsClick: () -> Unit,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val context = viewModel.context
    LaunchedEffect(Unit) { viewModel.loadInitialState() }
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val savedStateHandle = navController.currentBackStackEntry
        ?.savedStateHandle

    
    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getLiveData<Boolean>("refresh_home")?.observeForever { refresh ->
            if (refresh == true) {
                viewModel.loadSelectedSim()
                savedStateHandle.remove<Boolean>("refresh_home") // prevent repeated refresh
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
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