package com.example.exit_plan.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.exit_plan.ui.components.AppTopBar
import com.example.exit_plan.ui.components.FeatureCard

@Composable
fun HomeScreen(
    onOpenMoney: () -> Unit,
    onOpenEscape: () -> Unit,
    onOpenCheck: () -> Unit,
    checkSubtitle: String = "Фейковый звонок, если не отметишься вовремя"
) {
    Scaffold(topBar = { AppTopBar(title = "Antitrap") }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Защищает твоё время и деньги от ловушек.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FeatureCard(
                icon = Icons.Default.ShoppingCart,
                title = "Деньги",
                subtitle = "Цена любой покупки в часах твоей жизни",
                onClick = onOpenMoney
            )
            FeatureCard(
                icon = Icons.Default.Call,
                title = "Побег",
                subtitle = "Уйди из неловкой ситуации с помощью фейковой СМС или звонка",
                onClick = onOpenEscape,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
            FeatureCard(
                icon = Icons.Default.Notifications,
                title = "Проверка",
                subtitle = checkSubtitle,
                onClick = onOpenCheck,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        }
    }
}
