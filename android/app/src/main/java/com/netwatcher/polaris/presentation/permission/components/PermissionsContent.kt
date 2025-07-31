package com.netwatcher.polaris.presentation.permission.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.netwatcher.polaris.presentation.permission.PermissionItemState

@Composable
fun PermissionsContent(
    modifier: Modifier = Modifier,
    permissionStates: List<PermissionItemState>,
    onGrantAllClick: () -> Unit,
    onPermissionClick: (PermissionItemState) -> Unit
) {
    val grantedCount = permissionStates.count { it.isGranted }
    val totalCount = permissionStates.size
    val allGranted = grantedCount == totalCount

    LazyColumn(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Permissions Status",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "This app requires several permissions to monitor your network effectively. Please grant them all for full functionality.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$grantedCount / $totalCount Granted",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (allGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        if (!allGranted) {
                            Button(onClick = onGrantAllClick) {
                                Text("Grant All Missing")
                            }
                        }
                    }
                }
            }
        }

        items(permissionStates, key = { it.permission.name }) { itemState ->
            PermissionItemCard(
                permissionState = itemState,
                onRequestPermission = { onPermissionClick(itemState) }
            )
        }
    }
}