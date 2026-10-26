package com.example.exit_plan.ui.navigation

object Routes {
    const val HOME = "home"
    const val MONEY = "money"
    const val MONEY_SETTINGS = "money/settings"
    const val SCENARIOS = "scenarios"
    const val CHECK_SETUP = "check/setup"
    const val CHECK_COUNTDOWN = "check/countdown"

    const val ARG_SCENARIO_ID = "scenarioId"
    const val SCENARIO_EDIT = "scenario/edit/{$ARG_SCENARIO_ID}"
    fun scenarioEdit(id: Long) = "scenario/edit/$id"

    const val ESCAPE_RUN = "escape/run"
    const val ESCAPE_CHAT = "escape/chat"
    const val ESCAPE_CALL = "escape/call"
    const val ESCAPE_TALK = "escape/talk"
    const val ESCAPE_LEGEND = "escape/legend"
    val ESCAPE_ROUTES = setOf(ESCAPE_RUN, ESCAPE_CHAT, ESCAPE_CALL, ESCAPE_TALK, ESCAPE_LEGEND)
}
