package com.example.exit_plan.ui.check

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.exit_plan.domain.formatClock
import com.example.exit_plan.domain.secondsUntil
import com.example.exit_plan.ui.components.AppTopBar
import com.example.exit_plan.ui.components.EmptyState
import com.example.exit_plan.ui.components.rememberCurrentTimeMillis

@Composable
fun CheckCountdownScreen(
    state: CheckState,
    onConfirmSafe: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(topBar = { AppTopBar(title = "Проверка", onBack = onBack) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
        ) {
            when (state) {
                CheckState.Idle -> EmptyState("Нет активной проверки.")
                is CheckState.Running -> RunningCheck(state, onConfirmSafe)
            }
        }
    }
}

@Composable
private fun RunningCheck(state: CheckState.Running, onConfirmSafe: () -> Unit) {
    val now = rememberCurrentTimeMillis()
    val secondsLeft = secondsUntil(state.deadlineMillis, now)

    Text("Осталось", style = MaterialTheme.typography.titleMedium)
    Text(formatClock(secondsLeft), style = MaterialTheme.typography.displayLarge)
    Text(
        "Если не подтвердишь, запустится «${state.scenarioTitle}».",
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Button(
        onClick = onConfirmSafe,
        modifier = Modifier.size(200.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text("Я в порядке", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
    }
}
