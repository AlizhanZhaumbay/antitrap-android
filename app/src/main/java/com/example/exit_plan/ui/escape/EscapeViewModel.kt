package com.example.exit_plan.ui.escape

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exit_plan.data.ScenarioRepository
import com.example.exit_plan.domain.escape.EscalationTimeouts
import com.example.exit_plan.domain.escape.Scenario
import com.example.exit_plan.domain.escape.StepResponse
import com.example.exit_plan.domain.escape.StepType
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class EscapeViewModel(private val repository: ScenarioRepository) : ViewModel() {

    private val _state = MutableStateFlow<EscapeState>(EscapeState.Idle)
    val state: StateFlow<EscapeState> = _state.asStateFlow()

    private var escalationJob: Job? = null

    private var pendingResponse: CompletableDeferred<StepResponse>? = null

    fun start(scenarioId: Long) {
        escalationJob?.cancel()
        escalationJob = viewModelScope.launch {
            val scenario = repository.get(scenarioId) ?: return@launch
            runEscalation(scenario)
        }
    }

    fun reply() = respond(StepResponse.ANSWERED)
    fun ignore() = respond(StepResponse.DISMISSED)
    fun accept() = respond(StepResponse.ANSWERED)
    fun decline() = respond(StepResponse.DISMISSED)

    fun endCall() {
        val current = _state.value
        if (current is EscapeState.InCall) _state.value = EscapeState.Legend(current.scenario)
    }

    fun showLegend() {
        val current = _state.value
        if (current is EscapeState.Conversation) _state.value = EscapeState.Legend(current.scenario)
    }

    fun cancel() {
        escalationJob?.cancel()
        escalationJob = null
        pendingResponse = null
        _state.value = EscapeState.Idle
    }

    private suspend fun runEscalation(scenario: Scenario) {
        for ((index, step) in scenario.steps.withIndex()) {
            val delayMillis = step.delaySeconds * 1_000L
            _state.value = EscapeState.Waiting(scenario, index, System.currentTimeMillis() + delayMillis)
            delay(delayMillis)

            val shown = when (step.type) {
                StepType.SMS -> EscapeState.SmsShown(scenario, index)
                StepType.CALL -> EscapeState.Ringing(scenario, index)
            }
            val response = awaitResponse(shown, EscalationTimeouts.forStep(step.type))

            if (response == StepResponse.ANSWERED) {
                _state.value = when (step.type) {
                    StepType.SMS -> EscapeState.Conversation(scenario, System.currentTimeMillis())
                    StepType.CALL -> EscapeState.InCall(scenario, System.currentTimeMillis())
                }
                return
            }
        }
        _state.value = EscapeState.Idle
    }

    private suspend fun awaitResponse(shown: EscapeState, timeoutMillis: Long): StepResponse {
        val response = CompletableDeferred<StepResponse>()
        pendingResponse = response
        _state.value = shown
        return try {
            withTimeoutOrNull(timeoutMillis) { response.await() } ?: StepResponse.TIMED_OUT
        } finally {
            pendingResponse = null
        }
    }

    private fun respond(response: StepResponse) {
        pendingResponse?.complete(response)
    }
}
