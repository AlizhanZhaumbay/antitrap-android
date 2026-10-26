package com.example.exit_plan.ui.escape

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.exit_plan.R
import com.example.exit_plan.domain.formatClock
import com.example.exit_plan.domain.secondsSince
import com.example.exit_plan.ui.components.CallActionButton
import com.example.exit_plan.ui.components.CallControlButton
import com.example.exit_plan.ui.components.rememberCurrentTimeMillis
import com.example.exit_plan.ui.theme.CallDecline

private val KeypadKeys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#")

@Composable
fun ActiveCallScreen(
    callerName: String,
    startedAtMillis: Long?,
    onEnd: () -> Unit
) {
    BackHandler {}
    val now = rememberCurrentTimeMillis()
    val elapsed = startedAtMillis?.let { secondsSince(it, now) } ?: 0L

    var muted by rememberSaveable { mutableStateOf(false) }
    var speaker by rememberSaveable { mutableStateOf(false) }
    var onHold by rememberSaveable { mutableStateOf(false) }
    var keypadOpen by rememberSaveable { mutableStateOf(false) }
    var typedDigits by rememberSaveable { mutableStateOf("") }

    val subtitle = when {
        onHold -> "На удержании"
        typedDigits.isNotEmpty() && keypadOpen -> typedDigits
        else -> formatClock(elapsed)
    }

    CallLayout(callerName = callerName, subtitle = subtitle, pulsing = false) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
            if (keypadOpen) {
                Keypad(onKey = { typedDigits += it }, onHide = { keypadOpen = false })
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    CallControlButton(R.drawable.ic_mic_off, "Без звука", muted, onClick = { muted = !muted })
                    CallControlButton(R.drawable.ic_dialpad, "Клавиатура", false, onClick = { keypadOpen = true })
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    CallControlButton(R.drawable.ic_volume_up, "Динамик", speaker, onClick = { speaker = !speaker })
                    CallControlButton(R.drawable.ic_pause, "Удержание", onHold, onClick = { onHold = !onHold })
                }
            }
            CallActionButton(
                icon = Icons.Default.Call,
                label = "Завершить",
                color = CallDecline,
                onClick = onEnd,
                iconRotation = 135f
            )
        }
    }
}

@Composable
private fun Keypad(onKey: (String) -> Unit, onHide: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        KeypadKeys.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                row.forEach { key ->
                    Text(
                        text = key,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .size(64.dp)
                            .clickable { onKey(key) }
                            .padding(top = 12.dp)
                    )
                }
            }
        }
        TextButton(onClick = onHide) { Text("Скрыть клавиатуру", color = Color.White) }
    }
}
