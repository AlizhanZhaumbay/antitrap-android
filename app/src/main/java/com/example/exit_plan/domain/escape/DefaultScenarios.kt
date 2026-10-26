package com.example.exit_plan.domain.escape

import com.example.exit_plan.domain.escape.StepType.CALL
import com.example.exit_plan.domain.escape.StepType.SMS

object DefaultScenarios {
    val all: List<Scenario> = listOf(
        Scenario(
            id = Scenario.NEW_ID,
            title = "Скучная встреча",
            callerName = "Айгерим (работа)",
            smsText = "Срочно: клиент на линии, можешь выйти на 5 минут?",
            legend = "Скажи: «Извините, у клиента всё сломалось, и починить могу только я». " +
                "Выйди и не возвращайся минимум 10 минут.",
            steps = listOf(EscalationStep(SMS, 10), EscalationStep(CALL, 15), EscalationStep(CALL, 30))
        ),
        Scenario(
            id = Scenario.NEW_ID,
            title = "Свидание не задалось",
            callerName = "Мама",
            smsText = "Перезвони срочно, это насчёт бабушки.",
            legend = "Скажи: «Это мама, бабушке плохо, мне нужно ехать». " +
                "Извинись один раз, оплати свою часть и уходи.",
            steps = listOf(EscalationStep(SMS, 15), EscalationStep(CALL, 20))
        ),
        Scenario(
            id = Scenario.NEW_ID,
            title = "Навязчивый продавец",
            callerName = "Брат Ерлан",
            smsText = "Я внизу, машина перекрыла дорогу. Выходи быстрее!",
            legend = "Скажи: «Брат ждёт меня на улице, мне надо бежать». " +
                "Ничего не подписывай и не оставляй свой номер.",
            steps = listOf(EscalationStep(CALL, 10), EscalationStep(CALL, 20))
        )
    )
}
