package com.netwatcher.polaris.presentation.permissions

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.netwatcher.polaris.presentation.permissions.components.PermissionRationaleDialog
import com.netwatcher.polaris.presentation.permissions.components.PermissionTopBar
import com.netwatcher.polaris.presentation.permissions.components.PermissionsList
import com.netwatcher.polaris.utils.AppPermission
import com.netwatcher.polaris.utils.requiredPermissions

@Composable
fun PermissionScreen(navController: NavController, context: Context) {
    val permissions = remember { requiredPermissions(context) }
    var showRationaleDialog by remember { mutableStateOf<AppPermission?>(null) }

    showRationaleDialog?.let { permission ->
        PermissionRationaleDialog(
            permission = permission,
            onDismiss = { showRationaleDialog = null },
            onNavigateToSettings = {
                it.settingsIntent?.let { intentAction ->
                    context.startActivity(Intent(intentAction))
                }
                showRationaleDialog = null
            }
        )
    }

    Scaffold(
        topBar = { PermissionTopBar { navController.popBackStack() } }
    ) { padding ->
        PermissionsList(
            context = context,
            permissions = permissions,
            onShowRationale = { showRationaleDialog = it },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        )
    }
}
