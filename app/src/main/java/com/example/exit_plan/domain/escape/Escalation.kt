package com.example.exit_plan.domain.escape

enum class StepResponse { ANSWERED, DISMISSED, TIMED_OUT }

object EscalationTimeouts {
    const val SMS_RESPONSE_MILLIS = 15_000L
    const val RING_MILLIS = 30_000L

    fun forStep(type: StepType): Long = when (type) {
        StepType.SMS -> SMS_RESPONSE_MILLIS
        StepType.CALL -> RING_MILLIS
    }
}
