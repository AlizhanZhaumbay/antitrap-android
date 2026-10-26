package com.example.exit_plan.data

import com.example.exit_plan.domain.money.IncomeSettings
import com.example.exit_plan.domain.money.WishItem
import kotlinx.coroutines.flow.Flow

interface MoneyRepository {
    val settings: Flow<IncomeSettings>
    val wishlist: Flow<List<WishItem>>

    suspend fun updateSettings(settings: IncomeSettings)
    suspend fun addWish(title: String, price: Double)
    suspend fun removeWish(id: Long)
}
