package com.example.exit_plan.domain.escape

import kotlin.math.absoluteValue

private val KzMobilePrefixes = listOf("701", "702", "705", "707", "747", "771", "775", "777")

fun fakePhoneNumber(name: String): String {
    val hash = name.trim().lowercase().hashCode().toLong().absoluteValue
    val prefix = KzMobilePrefixes[(hash % KzMobilePrefixes.size).toInt()]
    val digits = (hash % 10_000_000).toString().padStart(7, '0')
    return "+7 $prefix ${digits.substring(0, 3)} ${digits.substring(3, 5)} ${digits.substring(5, 7)}"
}
