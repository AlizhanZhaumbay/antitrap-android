package com.example.exit_plan.ui.check

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exit_plan.domain.escape.Scenario
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CheckViewModel : ViewModel() {

    private val _state = MutableStateFlow<CheckState>(CheckState.Idle)
    val state: StateFlow<CheckState> = _state.asStateFlow()

    private val _expired = Channel<Long>(Channel.BUFFERED)
    val expired: Flow<Long> = _expired.receiveAsFlow()

    private var timerJob: Job? = null

    fun start(durationSeconds: Int, scenario: Scenario) {
        val deadline = System.currentTimeMillis() + durationSeconds * 1_000L
        _state.value = CheckState.Running(deadline, scenario.id, scenario.title)

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            delay(deadline - System.currentTimeMillis())
            _state.value = CheckState.Idle
            _expired.send(scenario.id)
        }
    }

    fun confirmSafe() {
        timerJob?.cancel()
        timerJob = null
        _state.value = CheckState.Idle
    }
}
