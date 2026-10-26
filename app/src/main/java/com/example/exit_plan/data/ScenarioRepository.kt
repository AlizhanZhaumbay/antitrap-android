package com.example.exit_plan.data

import com.example.exit_plan.domain.escape.Scenario
import kotlinx.coroutines.flow.Flow

interface ScenarioRepository {
    val scenarios: Flow<List<Scenario>>

    suspend fun get(id: Long): Scenario?

    suspend fun save(scenario: Scenario): Long

    suspend fun delete(id: Long)
}
