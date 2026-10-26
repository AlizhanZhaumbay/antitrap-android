package com.example.exit_plan.ui.navigation

import androidx.navigation.NavController
import com.example.exit_plan.ui.escape.EscapeState

fun EscapeState.route(): String? = when (this) {
    EscapeState.Idle -> null
    is EscapeState.Waiting, is EscapeState.SmsShown -> Routes.ESCAPE_RUN
    is EscapeState.Conversation -> Routes.ESCAPE_CHAT
    is EscapeState.Ringing -> Routes.ESCAPE_CALL
    is EscapeState.InCall -> Routes.ESCAPE_TALK
    is EscapeState.Legend -> Routes.ESCAPE_LEGEND
}

fun NavController.syncEscapeRoute(target: String?) {
    val current = currentDestination?.route
    when {
        target == current -> Unit
        target == null -> if (current in Routes.ESCAPE_ROUTES) popBackStack(Routes.HOME, inclusive = false)
        else -> {
            if (!hasEntry(Routes.ESCAPE_RUN)) navigate(Routes.ESCAPE_RUN) { popUpTo(Routes.HOME) }
            if (target == Routes.ESCAPE_RUN) {
                popBackStack(Routes.ESCAPE_RUN, inclusive = false)
            } else {
                navigate(target) { popUpTo(Routes.ESCAPE_RUN) }
            }
        }
    }
}

private fun NavController.hasEntry(route: String): Boolean =
    runCatching { getBackStackEntry(route) }.isSuccess
