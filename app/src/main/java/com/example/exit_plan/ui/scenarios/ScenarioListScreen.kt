package com.example.exit_plan.ui.scenarios

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.exit_plan.domain.escape.Scenario
import com.example.exit_plan.ui.components.AppTopBar
import com.example.exit_plan.ui.components.ConfirmDialog
import com.example.exit_plan.ui.components.EmptyState
import com.example.exit_plan.ui.components.InitialsAvatar
import com.example.exit_plan.ui.pluralRu

@Composable
fun ScenarioListScreen(
    viewModel: ScenariosViewModel,
    onRun: (Long) -> Unit,
    onEdit: (Long) -> Unit,
    onCreate: () -> Unit,
    onBack: () -> Unit
) {
    val scenarios by viewModel.scenarios.collectAsStateWithLifecycle()
    ScenarioListContent(
        scenarios = scenarios,
        onRun = onRun,
        onEdit = onEdit,
        onCreate = onCreate,
        onDelete = viewModel::delete,
        onBack = onBack
    )
}

@Composable
fun ScenarioListContent(
    scenarios: List<Scenario>,
    onRun: (Long) -> Unit,
    onEdit: (Long) -> Unit,
    onCreate: () -> Unit,
    onDelete: (Long) -> Unit,
    onBack: () -> Unit
) {
    var pendingDeleteId by rememberSaveable { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = { AppTopBar(title = "Сценарии побега", onBack = onBack) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreate,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Новый сценарий") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (scenarios.isEmpty()) {
                item { EmptyState("Сценариев пока нет. Создай первый, чтобы спланировать побег.") }
            }
            items(scenarios, key = { it.id }) { scenario ->
                ScenarioRow(
                    scenario = scenario,
                    onClick = { onEdit(scenario.id) },
                    onRun = { onRun(scenario.id) },
                    onDelete = { pendingDeleteId = scenario.id }
                )
            }
        }
    }

    val toDelete = scenarios.find { it.id == pendingDeleteId }
    if (toDelete != null) {
        ConfirmDialog(
            title = "Удалить сценарий?",
            text = "«${toDelete.title}» будет удалён.",
            confirmLabel = "Удалить",
            onConfirm = {
                onDelete(toDelete.id)
                pendingDeleteId = null
            },
            onDismiss = { pendingDeleteId = null }
        )
    }
}

@Composable
private fun ScenarioRow(scenario: Scenario, onClick: () -> Unit, onRun: () -> Unit, onDelete: () -> Unit) {
    Card(onClick = onClick) {
        ListItem(
            leadingContent = { InitialsAvatar(name = scenario.callerName) },
            headlineContent = { Text(scenario.title) },
            supportingContent = { Text(scenario.summary()) },
            trailingContent = {
                Row {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить ${scenario.title}")
                    }
                    FilledIconButton(onClick = onRun) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Запустить ${scenario.title}")
                    }
                }
            }
        )
    }
}

private fun Scenario.summary(): String {
    val stepWord = pluralRu(steps.size, one = "шаг", few = "шага", many = "шагов")
    return "$callerName · ${steps.size}\u00A0$stepWord · $totalDelaySeconds\u00A0с"
}
