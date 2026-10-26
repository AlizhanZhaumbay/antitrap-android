package com.example.exit_plan.ui

import com.example.exit_plan.domain.escape.StepType
import kotlin.math.absoluteValue

fun pluralRu(count: Int, one: String, few: String, many: String): String {
    val n = count.absoluteValue
    return when {
        n % 100 in 11..14 -> many
        n % 10 == 1 -> one
        n % 10 in 2..4 -> few
        else -> many
    }
}

fun StepType.label(): String = when (this) {
    StepType.SMS -> "СМС"
    StepType.CALL -> "Звонок"
}
