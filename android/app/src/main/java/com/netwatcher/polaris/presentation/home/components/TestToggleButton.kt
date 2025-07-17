package com.netwatcher.polaris.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TestToggleButton(
    label: String,
    selected: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor: Color
    val contentColor: Color
    val borderColor: Color

    if (selected) {
        containerColor = MaterialTheme.colorScheme.onTertiaryContainer
        contentColor = MaterialTheme.colorScheme.onSurface
        borderColor = Color.Green
    } else {
        containerColor = MaterialTheme.colorScheme.tertiaryContainer
        contentColor = MaterialTheme.colorScheme.onSurface
        borderColor = Color.Red
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .border(1.dp, borderColor, shape = RoundedCornerShape(12.dp))
            .clickable { onCheckedChange(!selected) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = contentColor
        )
    }
}