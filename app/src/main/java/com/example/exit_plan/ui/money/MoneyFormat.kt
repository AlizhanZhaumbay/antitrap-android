package com.example.exit_plan.ui.money

import com.example.exit_plan.domain.money.LifeTime
import java.util.Locale
import kotlin.math.roundToInt

private val Russian = Locale.forLanguageTag("ru")

fun formatMoney(amount: Double, currency: String): String =
    String.format(Locale.US, "%,.0f", amount).replace(',', ' ') + " $currency"

fun formatHours(hours: Double): String = when {
    hours < 1 -> "${(hours * 60).roundToInt()} мин"
    hours < 10 -> String.format(Russian, "%.1f ч", hours)
    else -> "${hours.roundToInt()} ч"
}

fun formatWorkDays(days: Double): String = String.format(Russian, "%.1f рабочего дня", days)

fun LifeTime.short(): String = "${formatHours(hours)} · ${formatWorkDays(workDays)}"

fun Double.toInputText(): String = if (this % 1.0 == 0.0) toLong().toString() else toString()
