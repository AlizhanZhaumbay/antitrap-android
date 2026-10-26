package com.example.exit_plan.data

import com.example.exit_plan.domain.escape.Scenario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryScenarioRepository(initial: List<Scenario>) : ScenarioRepository {
    private var nextId = 1L
    private val _scenarios = MutableStateFlow(initial.map { it.copy(id = nextId++) })

    override val scenarios: StateFlow<List<Scenario>> = _scenarios.asStateFlow()

    override suspend fun get(id: Long): Scenario? = _scenarios.value.find { it.id == id }

    override suspend fun save(scenario: Scenario): Long {
        if (scenario.id == Scenario.NEW_ID) {
            val created = scenario.copy(id = nextId++)
            _scenarios.update { it + created }
            return created.id
        }
        _scenarios.update { list -> list.map { if (it.id == scenario.id) scenario else it } }
        return scenario.id
    }

    override suspend fun delete(id: Long) {
        _scenarios.update { list -> list.filterNot { it.id == id } }
    }
}
