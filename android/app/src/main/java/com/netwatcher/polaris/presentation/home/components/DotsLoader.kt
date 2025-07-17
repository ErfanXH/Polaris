package com.netwatcher.polaris.presentation.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DotsLoader(
    modifier: Modifier = Modifier,
    dotSize: Dp = 8.dp,
    spaceBetween: Dp = 4.dp,
    travelDistance: Dp = 10.dp,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val dotCount = 3
    val animatables = remember {
        List(dotCount) { Animatable(initialValue = 0f) }
    }

    LaunchedEffect(Unit) {
        animatables.forEachIndexed { index, animatable ->
            launch {
                delay(index * 100L)
                while (true) {
                    animatable.animateTo(
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(300, easing = LinearOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                }
            }
        }
    }

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(spaceBetween)) {
        animatables.forEach { anim ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .offset(y = -travelDistance * anim.value)
                    .background(color, shape = CircleShape)
            )
        }
    }
}
