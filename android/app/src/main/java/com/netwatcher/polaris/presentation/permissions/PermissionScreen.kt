package com.netwatcher.polaris.presentation.permissions

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.netwatcher.polaris.presentation.permissions.components.PermissionRationaleDialog
import com.netwatcher.polaris.presentation.permissions.components.PermissionsContent
import com.netwatcher.polaris.presentation.permissions.components.PermissionTopBar

@Composable
fun PermissionScreen(
    navController: NavController,
    context: Context,
    viewModel: PermissionsViewModel = viewModel()
) {
    val permissionStates by viewModel.permissionStates.collectAsState()

    // ActivityResultLauncher for requesting multiple permissions
    val multiplePermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {
            // After the user responds, refresh the states
            viewModel.updatePermissionStates(context)
        }
    )

    // Re-check permissions when the user returns to the screen (e.g., from settings)
    DisposableEffect(navController) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.updatePermissionStates(context)
            }
        }
        navController.currentBackStackEntry?.lifecycle?.addObserver(observer)
        onDispose {
            navController.currentBackStackEntry?.lifecycle?.removeObserver(observer)
        }
    }

    // Handle rationale dialog display
    viewModel.showRationaleDialog?.let { permission ->
        PermissionRationaleDialog(
            permission = permission,
            onDismiss = { viewModel.onDismissRationale() },
            onNavigateToSettings = {
                it.settingsIntent?.let { intent ->
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Could not open settings.", Toast.LENGTH_SHORT).show()
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
            onGrantAllClick = {
                val permissionsToRequest = permissionStates
                    .filter { !it.isGranted && it.permission.settingsIntent == null }
                    .mapNotNull { it.permission.permissionString }
                    .toTypedArray()

                if (permissionsToRequest.isNotEmpty()) {
                    multiplePermissionsLauncher.launch(permissionsToRequest)
                } else {
                    Toast.makeText(context, "All standard permissions granted.", Toast.LENGTH_SHORT).show()
                }
            },
            onPermissionClick = { itemState ->
                val permission = itemState.permission
                if (permission.settingsIntent != null) {
                    // For special permissions, show rationale or navigate to settings
                    viewModel.onShowRationale(permission)
                } else if (permission.permissionString != null) {
                    // For standard runtime permissions
                    multiplePermissionsLauncher.launch(arrayOf(permission.permissionString))
                }
            }
        )
    }
}