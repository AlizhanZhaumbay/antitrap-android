package com.example.exit_plan.domain

import java.util.Locale

fun secondsUntil(deadlineMillis: Long, nowMillis: Long): Long =
    ((deadlineMillis - nowMillis).coerceAtLeast(0) + 999) / 1000

fun secondsSince(startMillis: Long, nowMillis: Long): Long =
    ((nowMillis - startMillis).coerceAtLeast(0)) / 1000

fun formatClock(totalSeconds: Long): String =
    String.format(Locale.US, "%02d:%02d", totalSeconds / 60, totalSeconds % 60)
