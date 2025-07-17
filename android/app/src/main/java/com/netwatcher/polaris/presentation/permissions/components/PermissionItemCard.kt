package com.netwatcher.polaris.presentation.permissions.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.netwatcher.polaris.utils.AppPermission

@Composable
fun PermissionItemCard(
    permission: AppPermission,
    isGranted: Boolean,
    onRequestPermission: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isGranted -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onBackground
            },
        ),
        border = BorderStroke(
            width = 2.dp,
            color = if (isGranted) Color.Green else Color.Red
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = permission.name.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = if (isGranted) Color.Black else Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = permission.description,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = if (isGranted) Color.Black else Color.White
                    )
                }

                Text(
                    text = if (isGranted) "✓ Granted" else "✗ Required",
                    color = if (isGranted) Color(0xFF4CAF50) else Color(0xFFF44336),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }


            if (!isGranted) {
                Button(
                    onClick = onRequestPermission,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Grant Permission", color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }
    }
}
