package com.netwatcher.polaris.presentation.permissions.components

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.netwatcher.polaris.MainActivity.Companion.PERMISSION_REQUEST_CODE
import com.netwatcher.polaris.utils.AppPermission
import com.netwatcher.polaris.utils.isBatteryOptimizationDisabled
import com.netwatcher.polaris.utils.permissionStatus

@Composable
fun PermissionsList(
    context: Context,
    permissions: List<AppPermission>,
    onShowRationale: (AppPermission) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        item {
            Text(
                text = "These permissions are required for the app to function properly:",
                modifier = Modifier.padding(bottom = 12.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(permissions) { permission ->
            val isGranted = when (permission.name) {
                "Ignore Battery Optimizations" -> context.isBatteryOptimizationDisabled()
                else -> permission.permissionString?.let {
                    permissionStatus(context, it)
                } ?: false
            }

            PermissionItemCard(
                permission = permission,
                isGranted = isGranted,
                onRequestPermission = {
                    if (permission.settingsIntent != null && !permissionStatus(context, permission.name)) {
                        onShowRationale(permission)
                    } else {
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(permission.name),
                            PERMISSION_REQUEST_CODE
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
