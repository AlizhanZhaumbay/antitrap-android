package com.example.exit_plan.domain.escape

enum class StepType { SMS, CALL }

data class EscalationStep(
    val type: StepType,
    val delaySeconds: Int
) {
    companion object {
        const val DELAY_STEP_SECONDS = 5
        const val MAX_DELAY_SECONDS = 600
    }
}

data class Scenario(
    val id: Long,
    val title: String,
    val callerName: String,
    val smsText: String,
    val legend: String,
    val steps: List<EscalationStep>
) {
    val totalDelaySeconds: Int get() = steps.sumOf { it.delaySeconds }

    fun validate(): List<String> = buildList {
        if (title.isBlank()) add("Укажи название")
        if (callerName.isBlank()) add("Укажи имя звонящего")
        if (steps.isEmpty()) add("Добавь хотя бы один шаг")
        if (steps.any { it.type == StepType.SMS } && smsText.isBlank()) add("Для шагов СМС нужен текст СМС")
    }

    companion object {
        const val NEW_ID = 0L

        fun blank() = Scenario(
            id = NEW_ID,
            title = "",
            callerName = "",
            smsText = "",
            legend = "",
            steps = listOf(EscalationStep(StepType.SMS, 10), EscalationStep(StepType.CALL, 20))
        )
    }
}
