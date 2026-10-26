package com.example.exit_plan.ui.scenarios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exit_plan.data.ScenarioRepository
import com.example.exit_plan.domain.escape.Scenario
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScenariosViewModel(private val repository: ScenarioRepository) : ViewModel() {

    val scenarios: StateFlow<List<Scenario>> = repository.scenarios
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun delete(id: Long) {
        viewModelScope.launch { repository.delete(id) }
    }
}
