package com.example.exit_plan

import com.example.exit_plan.data.InMemoryMoneyRepository
import com.example.exit_plan.data.InMemoryScenarioRepository
import com.example.exit_plan.data.MoneyRepository
import com.example.exit_plan.data.ScenarioRepository
import com.example.exit_plan.domain.escape.DefaultScenarios

class AppContainer {
    val moneyRepository: MoneyRepository = InMemoryMoneyRepository()
    val scenarioRepository: ScenarioRepository = InMemoryScenarioRepository(DefaultScenarios.all)
}
