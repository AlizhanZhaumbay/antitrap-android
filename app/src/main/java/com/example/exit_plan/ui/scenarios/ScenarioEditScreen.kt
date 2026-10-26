package com.example.exit_plan.ui.scenarios

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.exit_plan.domain.escape.EscalationStep
import com.example.exit_plan.domain.escape.Scenario
import com.example.exit_plan.domain.escape.StepType
import com.example.exit_plan.ui.components.AppTopBar
import com.example.exit_plan.ui.components.ConfirmDialog
import com.example.exit_plan.ui.components.SectionCard
import com.example.exit_plan.ui.label

@Composable
fun ScenarioEditScreen(viewModel: ScenarioEditViewModel, onDone: () -> Unit) {
    val initial by viewModel.initial.collectAsStateWithLifecycle()
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(title = if (viewModel.isNew) "Новый сценарий" else "Изменить сценарий", onBack = onDone) {
                if (!viewModel.isNew) {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить сценарий")
                    }
                }
            }
        }
    ) { padding ->
        val loaded = initial
        if (loaded == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            ScenarioForm(
                initial = loaded,
                onSave = { viewModel.save(it, onDone) },
                modifier = Modifier.padding(padding)
            )
        }
    }

    if (showDeleteDialog) {
        ConfirmDialog(
            title = "Удалить сценарий?",
            text = "Это действие нельзя отменить.",
            confirmLabel = "Удалить",
            onConfirm = {
                showDeleteDialog = false
                viewModel.delete(onDone)
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

private val StepsSaver = listSaver<List<EscalationStep>, String>(
    save = { steps -> steps.map { "${it.type.name}:${it.delaySeconds}" } },
    restore = { saved ->
        saved.map {
            val (type, delay) = it.split(":")
            EscalationStep(StepType.valueOf(type), delay.toInt())
        }
    }
)

@Composable
fun ScenarioForm(
    initial: Scenario,
    onSave: (Scenario) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by rememberSaveable { mutableStateOf(initial.title) }
    var callerName by rememberSaveable { mutableStateOf(initial.callerName) }
    var smsText by rememberSaveable { mutableStateOf(initial.smsText) }
    var legend by rememberSaveable { mutableStateOf(initial.legend) }
    var steps by rememberSaveable(stateSaver = StepsSaver) { mutableStateOf(initial.steps) }

    val draft = initial.copy(title = title, callerName = callerName, smsText = smsText, legend = legend, steps = steps)
    val errors = draft.validate()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionCard(title = "Сценарий") {
            FormTextField(value = title, onValueChange = { title = it }, label = "Название")
            FormTextField(value = callerName, onValueChange = { callerName = it }, label = "Имя звонящего")
            FormTextField(value = smsText, onValueChange = { smsText = it }, label = "Текст СМС", singleLine = false)
            FormTextField(value = legend, onValueChange = { legend = it }, label = "Легенда: что сказать окружающим", singleLine = false)
        }

        SectionCard(title = "Шаги эскалации") {
            steps.forEachIndexed { index, step ->
                StepEditorRow(
                    number = index + 1,
                    step = step,
                    onChange = { changed -> steps = steps.toMutableList().apply { set(index, changed) } },
                    onRemove = { steps = steps.toMutableList().apply { removeAt(index) } }
                )
            }
            OutlinedButton(
                onClick = { steps = steps + EscalationStep(StepType.CALL, 20) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Добавить шаг")
            }
        }

        errors.forEach { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            onClick = { onSave(draft.copy(title = title.trim(), callerName = callerName.trim())) },
            enabled = errors.isEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сохранить")
        }
    }
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 2,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun StepEditorRow(
    number: Int,
    step: EscalationStep,
    onChange: (EscalationStep) -> Unit,
    onRemove: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("$number.", style = MaterialTheme.typography.titleMedium)
            StepType.entries.forEach { type ->
                FilterChip(
                    selected = step.type == type,
                    onClick = { onChange(step.copy(type = type)) },
                    label = { Text(type.label()) }
                )
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, contentDescription = "Удалить шаг $number")
            }
        }
        DelayStepper(seconds = step.delaySeconds, onChange = { onChange(step.copy(delaySeconds = it)) })
    }
}

@Composable
fun DelayStepper(seconds: Int, onChange: (Int) -> Unit) {
    val stepSize = EscalationStep.DELAY_STEP_SECONDS
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Ждать перед шагом:", style = MaterialTheme.typography.bodyMedium)
        FilledTonalIconButton(onClick = { onChange((seconds - stepSize).coerceAtLeast(0)) }, enabled = seconds > 0) {
            Text("−")
        }
        Text("$seconds с", modifier = Modifier.widthIn(min = 48.dp), textAlign = TextAlign.Center)
        FilledTonalIconButton(
            onClick = { onChange((seconds + stepSize).coerceAtMost(EscalationStep.MAX_DELAY_SECONDS)) },
            enabled = seconds < EscalationStep.MAX_DELAY_SECONDS
        ) {
            Text("+")
        }
    }
}
