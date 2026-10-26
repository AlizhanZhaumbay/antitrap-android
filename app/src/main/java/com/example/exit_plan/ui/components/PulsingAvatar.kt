package com.example.exit_plan.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PulsingAvatar(name: String, modifier: Modifier = Modifier, size: Dp = 120.dp) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1_600, easing = LinearEasing), RepeatMode.Restart),
        label = "pulseProgress"
    )

    Box(modifier = modifier.size(size * 1.7f), contentAlignment = Alignment.Center) {
        listOf(progress, (progress + 0.5f) % 1f).forEach { ring ->
            Box(
                Modifier
                    .size(size)
                    .graphicsLayer {
                        val scale = 1f + ring * 0.7f
                        scaleX = scale
                        scaleY = scale
                        alpha = 1f - ring
                    }
                    .background(Color.White.copy(alpha = 0.18f), CircleShape)
            )
        }
        InitialsAvatar(name = name, size = size)
    }
}
