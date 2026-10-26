package com.example.exit_plan.domain.check

object CheckDurations {
    val optionsSeconds: List<Int> = listOf(30, 60, 5 * 60, 15 * 60, 30 * 60, 60 * 60)

    fun label(seconds: Int): String = when {
        seconds < 60 -> "$seconds с"
        seconds < 3600 -> "${seconds / 60} мин"
        else -> "${seconds / 3600} ч"
    }
}
