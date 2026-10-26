package com.example.exit_plan

import com.example.exit_plan.data.InMemoryMoneyRepository
import com.example.exit_plan.data.MoneyRepository

class AppContainer {
    val moneyRepository: MoneyRepository = InMemoryMoneyRepository()
}
