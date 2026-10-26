package com.example.exit_plan.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

private const val TRIGGER_FRACTION = 0.8f

@Composable
fun SwipeToAnswer(
    acceptColor: Color,
    declineColor: Color,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    modifier: Modifier = Modifier
) {
    val handleSize = 72.dp
    val offset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(handleSize + 8.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.12f))
    ) {
        val maxOffset = with(LocalDensity.current) { ((maxWidth - handleSize) / 2 - 4.dp).toPx() }
        val fraction = (offset.value / maxOffset).coerceIn(-1f, 1f)

        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("‹ Отклонить", color = Color.White.copy(alpha = 0.6f))
            Box(Modifier.weight(1f))
            Text("Ответить ›", color = Color.White.copy(alpha = 0.6f))
        }

        val handleColor = when {
            fraction > 0 -> lerp(Color.White, acceptColor, fraction)
            else -> lerp(Color.White, declineColor, fraction.absoluteValue)
        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset { IntOffset(offset.value.roundToInt(), 0) }
                .size(handleSize)
                .background(handleColor, CircleShape)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        scope.launch { offset.snapTo((offset.value + delta).coerceIn(-maxOffset, maxOffset)) }
                    },
                    onDragStopped = {
                        when {
                            offset.value >= maxOffset * TRIGGER_FRACTION -> onAccept()
                            offset.value <= -maxOffset * TRIGGER_FRACTION -> onDecline()
                        }
                        offset.animateTo(0f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            val iconColor = if (fraction.absoluteValue > 0.5f) Color.White else acceptColor
            Icon(
                Icons.Default.Call,
                contentDescription = "Свайп вправо — ответить, влево — отклонить",
                tint = iconColor,
                modifier = Modifier.size(32.dp).rotate(if (fraction < 0) 135f * fraction.absoluteValue else 0f)
            )
        }
    }
}
