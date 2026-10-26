package com.example.exit_plan.domain.money

data class LifeTime(val hours: Double, val workDays: Double)

object LifeCost {
    const val WEEKS_PER_MONTH = 52.0 / 12.0
    const val WORK_DAYS_PER_WEEK = 5.0
    const val MAX_HOURS_PER_WEEK = 168.0

    fun hourlyRate(settings: IncomeSettings): Double? {
        val validHours = settings.hoursPerWeek > 0 && settings.hoursPerWeek <= MAX_HOURS_PER_WEEK
        if (settings.monthlyIncome <= 0 || !validHours) return null
        return settings.monthlyIncome / (settings.hoursPerWeek * WEEKS_PER_MONTH)
    }

    fun lifeTimeFor(price: Double, settings: IncomeSettings): LifeTime? {
        val rate = hourlyRate(settings) ?: return null
        if (price < 0) return null
        val hours = price / rate
        val hoursPerWorkDay = settings.hoursPerWeek / WORK_DAYS_PER_WEEK
        return LifeTime(hours = hours, workDays = hours / hoursPerWorkDay)
    }

    fun totalPrice(items: List<WishItem>): Double = items.sumOf { it.price }
}
