package com.example.exit_plan.ui.money

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import com.example.exit_plan.domain.money.IncomeSettings
import com.example.exit_plan.domain.money.LifeCost
import com.example.exit_plan.domain.money.parseAmount
import com.example.exit_plan.ui.components.AppTopBar
import com.example.exit_plan.ui.components.NumberField
import com.example.exit_plan.ui.components.SectionCard
import java.util.Locale

@Composable
fun MoneySettingsScreen(viewModel: MoneyViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    MoneySettingsContent(
        settings = state.settings,
        onSave = { income, hours -> viewModel.updateSettings(income, hours, onDone = onBack) },
        onBack = onBack
    )
}

@Composable
fun MoneySettingsContent(
    settings: IncomeSettings,
    onSave: (monthlyIncome: Double, hoursPerWeek: Double) -> Unit,
    onBack: () -> Unit
) {
    var incomeText by rememberSaveable(settings) { mutableStateOf(settings.monthlyIncome.toInputText()) }
    var hoursText by rememberSaveable(settings) { mutableStateOf(settings.hoursPerWeek.toInputText()) }

    val income = parseAmount(incomeText)
    val hours = parseAmount(hoursText)
    val draft = IncomeSettings(income ?: 0.0, hours ?: 0.0, settings.currency)
    val rate = LifeCost.hourlyRate(draft)

    val incomeError = if (income == null || income <= 0) "Введи доход за месяц" else null
    val hoursError = if (hours == null || hours <= 0 || hours > LifeCost.MAX_HOURS_PER_WEEK) "От 1 до 168 часов" else null

    Scaffold(topBar = { AppTopBar(title = "Настройки дохода", onBack = onBack) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard(title = "Твоя работа") {
                NumberField(
                    value = incomeText,
                    onValueChange = { incomeText = it },
                    label = "Доход в месяц",
                    suffix = settings.currency,
                    errorText = incomeError,
                    modifier = Modifier.fillMaxWidth()
                )
                NumberField(
                    value = hoursText,
                    onValueChange = { hoursText = it },
                    label = "Рабочих часов в неделю",
                    suffix = "ч",
                    errorText = hoursError,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            SectionCard(title = "Результат") {
                Text(
                    text = rate?.let { "1 час = ${formatMoney(it, settings.currency)}" } ?: "Заполни оба поля",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    "Расчёт: ${String.format(Locale.forLanguageTag("ru"), "%.2f", LifeCost.WEEKS_PER_MONTH)} недели в месяце.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = { onSave(draft.monthlyIncome, draft.hoursPerWeek) },
                enabled = rate != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить")
            }
        }
    }
}
