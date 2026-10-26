package com.example.exit_plan.domain.money

fun parseAmount(text: String): Double? =
    text.filterNot { it.isWhitespace() }
        .replace(',', '.')
        .toDoubleOrNull()
        ?.takeIf { it >= 0 }
