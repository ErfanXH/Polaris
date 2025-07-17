package com.netwatcher.polaris.presentation.permissions.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.netwatcher.polaris.utils.AppPermission

@Composable
fun PermissionRationaleDialog(
    permission: AppPermission,
    onDismiss: () -> Unit,
    onNavigateToSettings: (AppPermission) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(
            Icons.Outlined.Policy,
            contentDescription = "Permission Icon",
            tint = MaterialTheme.colorScheme.onBackground
        ) },
        title = { Text("Permission Required", style = MaterialTheme.typography.titleMedium) },
        text = { Text("This app needs the '${permission.name}' permission.\nPlease grant it in the device settings:\n${permission.guide}") },
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