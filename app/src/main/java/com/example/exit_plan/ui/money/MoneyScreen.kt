package com.example.exit_plan.ui.money

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.exit_plan.domain.money.IncomeSettings
import com.example.exit_plan.domain.money.LifeCost
import com.example.exit_plan.domain.money.parseAmount
import com.example.exit_plan.ui.components.AppTopBar
import com.example.exit_plan.ui.components.EmptyState
import com.example.exit_plan.ui.components.NumberField
import com.example.exit_plan.ui.components.SectionCard

@Composable
fun MoneyScreen(
    viewModel: MoneyViewModel,
    onOpenSettings: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    MoneyContent(
        state = state,
        onAddWish = viewModel::addWish,
        onRemoveWish = viewModel::removeWish,
        onOpenSettings = onOpenSettings,
        onBack = onBack
    )
}

@Composable
fun MoneyContent(
    state: MoneyUiState,
    onAddWish: (String, Double) -> Unit,
    onRemoveWish: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    val currency = state.settings.currency

    Scaffold(
        topBar = {
            AppTopBar(title = "Цена в часах жизни", onBack = onBack) {
                IconButton(onClick = onOpenSettings) {
                    Icon(Icons.Default.Settings, contentDescription = "Настройки дохода")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { HourlyRateCard(hourlyRate = state.hourlyRate, currency = currency, onOpenSettings = onOpenSettings) }
            item { CalculatorCard(settings = state.settings) }
            item {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Хотелки", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                    TextButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Добавить")
                    }
                }
            }
            if (state.items.isEmpty()) {
                item { EmptyState("Пока пусто. Добавь то, что хочешь купить.") }
            }
            items(state.items, key = { it.item.id }) { wish ->
                WishRow(wish = wish, currency = currency, onRemove = { onRemoveWish(wish.item.id) })
            }
            if (state.items.isNotEmpty()) {
                item { TotalCard(state) }
            }
        }
    }

    if (showAddDialog) {
        AddWishDialog(
            currency = currency,
            onConfirm = { title, price ->
                onAddWish(title, price)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun HourlyRateCard(hourlyRate: Double?, currency: String, onOpenSettings: () -> Unit) {
    Card(
        onClick = onOpenSettings,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Час твоей жизни стоит", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = hourlyRate?.let { formatMoney(it, currency) } ?: "Укажи доход",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

@Composable
private fun CalculatorCard(settings: IncomeSettings) {
    var priceText by rememberSaveable { mutableStateOf("") }
    val result = parseAmount(priceText)?.let { LifeCost.lifeTimeFor(it, settings) }

    SectionCard(title = "Калькулятор") {
        NumberField(
            value = priceText,
            onValueChange = { priceText = it },
            label = "Цена",
            suffix = settings.currency,
            modifier = Modifier.fillMaxWidth()
        )
        when {
            priceText.isBlank() -> Text("Введи цену, чтобы узнать её настоящую стоимость.")
            result == null -> Text("Проверь цену и настройки дохода.", color = MaterialTheme.colorScheme.error)
            else -> {
                Text("= ${formatHours(result.hours)} твоей жизни", style = MaterialTheme.typography.headlineSmall)
                Text("≈ ${formatWorkDays(result.workDays)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun WishRow(wish: WishItemUi, currency: String, onRemove: () -> Unit) {
    Card {
        ListItem(
            headlineContent = { Text(wish.item.title) },
            supportingContent = { Text(formatMoney(wish.item.price, currency)) },
            overlineContent = if (wish.lifeTime != null) { { Text(wish.lifeTime.short()) } } else null,
            trailingContent = {
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить ${wish.item.title}")
                }
            }
        )
    }
}

@Composable
private fun TotalCard(state: MoneyUiState) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Итого: ${formatMoney(state.totalPrice, state.settings.currency)}", style = MaterialTheme.typography.titleMedium)
            state.totalLifeTime?.let {
                Text("= ${formatHours(it.hours)} твоей жизни", style = MaterialTheme.typography.headlineSmall)
                Text("≈ ${formatWorkDays(it.workDays)}")
            }
        }
    }
}

@Composable
private fun AddWishDialog(
    currency: String,
    onConfirm: (String, Double) -> Unit,
    onDismiss: () -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var priceText by rememberSaveable { mutableStateOf("") }
    val price = parseAmount(priceText)
    val canAdd = title.isNotBlank() && price != null && price > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая хотелка") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название") },
                    singleLine = true
                )
                NumberField(value = priceText, onValueChange = { priceText = it }, label = "Цена", suffix = currency)
            }
        },
        confirmButton = {
            TextButton(onClick = { if (price != null) onConfirm(title, price) }, enabled = canAdd) { Text("Добавить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}
