package com.example.exit_plan.ui.escape

import com.example.exit_plan.domain.escape.Scenario

sealed interface EscapeState {
    data object Idle : EscapeState

    sealed interface Active : EscapeState {
        val scenario: Scenario
    }

    data class Waiting(
        override val scenario: Scenario,
        val stepIndex: Int,
        val nextStepAtMillis: Long
    ) : Active

    data class SmsShown(override val scenario: Scenario, val stepIndex: Int) : Active

    data class Ringing(override val scenario: Scenario, val stepIndex: Int) : Active

    data class Conversation(override val scenario: Scenario, val receivedAtMillis: Long) : Active

    data class InCall(override val scenario: Scenario, val startedAtMillis: Long) : Active

    data class Legend(override val scenario: Scenario) : Active
}
