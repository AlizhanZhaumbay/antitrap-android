package com.example.exit_plan.data

import com.example.exit_plan.domain.money.IncomeSettings
import com.example.exit_plan.domain.money.WishItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryMoneyRepository : MoneyRepository {
    private val _settings = MutableStateFlow(IncomeSettings.DEFAULT)
    private val _wishlist = MutableStateFlow<List<WishItem>>(emptyList())
    private var nextId = 1L

    override val settings: StateFlow<IncomeSettings> = _settings.asStateFlow()
    override val wishlist: StateFlow<List<WishItem>> = _wishlist.asStateFlow()

    override suspend fun updateSettings(settings: IncomeSettings) {
        _settings.value = settings
    }

    override suspend fun addWish(title: String, price: Double) {
        val item = WishItem(id = nextId++, title = title, price = price)
        _wishlist.update { it + item }
    }

    override suspend fun removeWish(id: Long) {
        _wishlist.update { list -> list.filterNot { it.id == id } }
    }
}
