package com.example.exit_plan.ui.scenarios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exit_plan.data.ScenarioRepository
import com.example.exit_plan.domain.escape.Scenario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScenarioEditViewModel(
    private val scenarioId: Long,
    private val repository: ScenarioRepository
) : ViewModel() {

    private val _initial = MutableStateFlow<Scenario?>(null)
    val initial: StateFlow<Scenario?> = _initial.asStateFlow()

    val isNew: Boolean get() = scenarioId == Scenario.NEW_ID

    init {
        viewModelScope.launch {
            _initial.value = if (isNew) Scenario.blank() else repository.get(scenarioId) ?: Scenario.blank()
        }
    }

    fun save(scenario: Scenario, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.save(scenario.copy(id = scenarioId))
            onDone()
        }
    }

    fun delete(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.delete(scenarioId)
            onDone()
        }
    }
}
