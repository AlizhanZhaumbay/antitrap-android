package com.example.exit_plan.domain.money

data class IncomeSettings(
    val monthlyIncome: Double,
    val hoursPerWeek: Double,
    val currency: String = "₸"
) {
    companion object {
        val DEFAULT = IncomeSettings(monthlyIncome = 400_000.0, hoursPerWeek = 40.0)
    }
}
