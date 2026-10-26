package com.example.exit_plan.ui.escape

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.exit_plan.domain.escape.EscalationStep
import com.example.exit_plan.domain.escape.StepType
import com.example.exit_plan.domain.formatClock
import com.example.exit_plan.domain.secondsUntil
import com.example.exit_plan.ui.components.AppTopBar
import com.example.exit_plan.ui.components.InitialsAvatar
import com.example.exit_plan.ui.components.SectionCard
import com.example.exit_plan.ui.components.rememberCurrentTimeMillis
import com.example.exit_plan.ui.label

@Composable
fun EscapeRunScreen(state: EscapeState, onCancel: () -> Unit) {
    BackHandler(onBack = onCancel)
    val active = state as? EscapeState.Active ?: return
    val scenario = active.scenario
    val currentIndex = when (state) {
        is EscapeState.Waiting -> state.stepIndex
        is EscapeState.SmsShown -> state.stepIndex
        is EscapeState.Ringing -> state.stepIndex
        else -> scenario.steps.size
    }

    Scaffold(topBar = { AppTopBar(title = "Побег начался") }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InitialsAvatar(name = scenario.callerName, size = 56.dp)
                Column {
                    Text(scenario.title, style = MaterialTheme.typography.titleLarge)
                    Text("Звонящий: ${scenario.callerName}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            StatusText(state)

            SectionCard(title = "Шаги") {
                scenario.steps.forEachIndexed { index, step ->
                    StepStatusRow(number = index + 1, step = step, isDone = index < currentIndex, isCurrent = index == currentIndex)
                }
            }

            Text(
                "Не закрывай приложение и положи телефон экраном вниз. Он зазвонит сам.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Отменить побег")
            }
        }
    }
}

@Composable
private fun StatusText(state: EscapeState) {
    when (state) {
        is EscapeState.Waiting -> {
            val now = rememberCurrentTimeMillis()
            val step = state.scenario.steps[state.stepIndex]
            Text(
                "Далее: ${step.type.label()} через ${formatClock(secondsUntil(state.nextStepAtMillis, now))}",
                style = MaterialTheme.typography.headlineSmall
            )
        }
        is EscapeState.SmsShown -> Text("Пришла СМС. Ответь или проигнорируй.", style = MaterialTheme.typography.headlineSmall)
        else -> Unit
    }
}

@Composable
private fun StepStatusRow(number: Int, step: EscalationStep, isDone: Boolean, isCurrent: Boolean) {
    val color = when {
        isCurrent -> MaterialTheme.colorScheme.primary
        isDone -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurface
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        val icon = when {
            isDone -> Icons.Default.CheckCircle
            step.type == StepType.SMS -> Icons.Default.Email
            else -> Icons.Default.Call
        }
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Text("$number. ${step.type.label()} через ${step.delaySeconds} с", color = color)
    }
}
