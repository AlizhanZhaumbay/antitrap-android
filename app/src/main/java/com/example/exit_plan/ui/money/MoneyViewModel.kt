package com.example.exit_plan.ui.money

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exit_plan.data.MoneyRepository
import com.example.exit_plan.domain.money.IncomeSettings
import com.example.exit_plan.domain.money.LifeCost
import com.example.exit_plan.domain.money.LifeTime
import com.example.exit_plan.domain.money.WishItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class WishItemUi(val item: WishItem, val lifeTime: LifeTime?)

data class MoneyUiState(
    val settings: IncomeSettings = IncomeSettings.DEFAULT,
    val hourlyRate: Double? = null,
    val items: List<WishItemUi> = emptyList(),
    val totalPrice: Double = 0.0,
    val totalLifeTime: LifeTime? = null
)

class MoneyViewModel(private val repository: MoneyRepository) : ViewModel() {

    val state: StateFlow<MoneyUiState> =
        combine(repository.settings, repository.wishlist) { settings, items -> buildState(settings, items) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MoneyUiState())

    fun updateSettings(monthlyIncome: Double, hoursPerWeek: Double, onDone: () -> Unit) {
        viewModelScope.launch {
            val updated = state.value.settings.copy(monthlyIncome = monthlyIncome, hoursPerWeek = hoursPerWeek)
            repository.updateSettings(updated)
            onDone()
        }
    }

    fun addWish(title: String, price: Double) {
        viewModelScope.launch { repository.addWish(title.trim(), price) }
    }

    fun removeWish(id: Long) {
        viewModelScope.launch { repository.removeWish(id) }
    }

    private fun buildState(settings: IncomeSettings, items: List<WishItem>): MoneyUiState {
        val total = LifeCost.totalPrice(items)
        return MoneyUiState(
            settings = settings,
            hourlyRate = LifeCost.hourlyRate(settings),
            items = items.map { WishItemUi(it, LifeCost.lifeTimeFor(it.price, settings)) },
            totalPrice = total,
            totalLifeTime = LifeCost.lifeTimeFor(total, settings)
        )
    }
}
