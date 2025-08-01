package com.netwatcher.polaris.presentation.permission

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.netwatcher.polaris.presentation.permission.components.PermissionRationaleDialog
import com.netwatcher.polaris.presentation.permission.components.PermissionsContent
import com.netwatcher.polaris.presentation.permission.components.PermissionTopBar

@Composable
fun PermissionScreen(
    navController: NavController
) {
    val viewModel: PermissionsViewModel = hiltViewModel()
    val context = viewModel.context
    val permissionStates by viewModel.permissionStates.collectAsState()

    DisposableEffect(navController) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.updatePermissionStates()
            }
        }
        navController.currentBackStackEntry?.lifecycle?.addObserver(observer)
        onDispose {
            navController.currentBackStackEntry?.lifecycle?.removeObserver(observer)
        }
    }
    
    viewModel.showRationaleDialog?.let { permission ->
        PermissionRationaleDialog(
            permission = permission,
            onDismiss = { viewModel.onDismissRationale() },
            onNavigateToSettings = {
                it.settingsIntent?.let { intent ->
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Could not open settings.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                viewModel.onDismissRationale()
            }
        )
    }

    Scaffold(
        topBar = { PermissionTopBar { navController.popBackStack() } },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        PermissionsContent(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            permissionStates = permissionStates,
            onPermissionClick = { itemState ->
                val permission = itemState.permission
                viewModel.onShowRationale(permission)
            }
        )
    }
}