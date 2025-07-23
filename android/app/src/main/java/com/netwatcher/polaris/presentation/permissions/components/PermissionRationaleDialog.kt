package com.netwatcher.polaris.presentation.permissions.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.netwatcher.polaris.utils.permissions.AppPermission
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight

@Composable
fun PermissionRationaleDialog(
    permission: AppPermission,
    onDismiss: () -> Unit,
    onNavigateToSettings: (AppPermission) -> Unit
) {
    val guideText = buildAnnotatedString {
        append("This app needs the '${permission.name}' permission.\n")
        append("Please grant it in the device settings:\n")
        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
        append(permission.guide)
        pop()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Outlined.Policy,
                contentDescription = "Permission Icon",
                tint = MaterialTheme.colorScheme.onBackground
            )
        },
        title = { Text("Permission Required", style = MaterialTheme.typography.titleMedium) },
        text = { Text(guideText) },
        confirmButton = {
            TextButton(onClick = { onNavigateToSettings(permission) }) {
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
