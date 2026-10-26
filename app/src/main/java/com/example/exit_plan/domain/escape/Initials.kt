package com.example.exit_plan.domain.escape

fun initialsOf(name: String): String =
    name.split(' ')
        .mapNotNull { word -> word.firstOrNull { it.isLetterOrDigit() } }
        .take(2)
        .joinToString("") { it.uppercase() }
        .ifEmpty { "?" }
