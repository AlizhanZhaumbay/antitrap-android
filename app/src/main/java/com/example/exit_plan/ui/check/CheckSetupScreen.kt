package com.example.exit_plan.ui.check

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.exit_plan.domain.check.CheckDurations
import com.example.exit_plan.domain.escape.Scenario
import com.example.exit_plan.ui.components.AppTopBar
import com.example.exit_plan.ui.components.EmptyState
import com.example.exit_plan.ui.components.SectionCard

@Composable
fun CheckSetupScreen(
    scenarios: List<Scenario>,
    checkState: CheckState,
    onStart: (durationSeconds: Int, scenario: Scenario) -> Unit,
    onOpenCountdown: () -> Unit,
    onBack: () -> Unit
) {
    var durationSeconds by rememberSaveable { mutableIntStateOf(CheckDurations.optionsSeconds[2]) }
    var selectedId by rememberSaveable { mutableStateOf<Long?>(null) }
    val selected = scenarios.find { it.id == selectedId } ?: scenarios.firstOrNull()

    Scaffold(topBar = { AppTopBar(title = "Проверка", onBack = onBack) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Если не нажмёшь «Я в порядке» до дедлайна, выбранный сценарий побега запустится сам.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (checkState is CheckState.Running) {
                Card(
                    onClick = onOpenCountdown,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Text(
                        "Идёт проверка для «${checkState.scenarioTitle}». Нажми, чтобы открыть.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            SectionCard(title = "Дедлайн через") {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CheckDurations.optionsSeconds.forEach { seconds ->
                        FilterChip(
                            selected = seconds == durationSeconds,
                            onClick = { durationSeconds = seconds },
                            label = { Text(CheckDurations.label(seconds)) }
                        )
                    }
                }
            }

            SectionCard(title = "Сценарий") {
                if (scenarios.isEmpty()) EmptyState("Сначала создай сценарий.")
                scenarios.forEach { scenario ->
                    ScenarioOption(
                        scenario = scenario,
                        selected = scenario.id == selected?.id,
                        onSelect = { selectedId = scenario.id }
                    )
                }
            }

            Button(
                onClick = { selected?.let { onStart(durationSeconds, it) } },
                enabled = selected != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Начать проверку")
            }
        }
    }
}

@Composable
private fun ScenarioOption(scenario: Scenario, selected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onSelect, role = Role.RadioButton),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(scenario.title, style = MaterialTheme.typography.bodyLarge)
            Text(scenario.callerName, style = MaterialTheme.typography.bodySmall)
        }
    }
}
