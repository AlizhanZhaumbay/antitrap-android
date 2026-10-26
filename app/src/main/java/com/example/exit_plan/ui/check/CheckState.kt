package com.example.exit_plan.ui.check

sealed interface CheckState {
    data object Idle : CheckState

    data class Running(
        val deadlineMillis: Long,
        val scenarioId: Long,
        val scenarioTitle: String
    ) : CheckState
}
