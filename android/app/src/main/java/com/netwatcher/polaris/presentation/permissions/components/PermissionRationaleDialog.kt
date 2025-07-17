package com.netwatcher.polaris.presentation.permissions.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.toUpperCase
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
        title = { Text("Permission Required", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column {
                Text("${permission.name.uppercase()} is ${permission.description.lowercase()}.")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Please go to ${permission.guide}")
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