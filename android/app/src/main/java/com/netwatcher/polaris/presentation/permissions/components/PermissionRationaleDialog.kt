package com.netwatcher.polaris.presentation.permissions.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.netwatcher.polaris.utils.AppPermission

@Composable
fun PermissionRationaleDialog(
    permission: AppPermission,
    onDismiss: () -> Unit,
    onNavigateToSettings: (AppPermission) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permission Required") },
        text = {
            Column {
                Text("This app requires ${permission.name.substringAfterLast('.')} permission to function properly.")
                Spacer(modifier = Modifier.height(4.dp))
                Text(permission.description)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Please grant this permission in system settings.")
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onNavigateToSettings(permission) }
            ) {
                Text("Open Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}