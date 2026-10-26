package com.example.exit_plan.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.exit_plan.AntitrapApp
import com.example.exit_plan.ui.home.HomeScreen
import com.example.exit_plan.ui.money.MoneyScreen
import com.example.exit_plan.ui.money.MoneySettingsScreen
import com.example.exit_plan.ui.money.MoneyViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val container = (LocalContext.current.applicationContext as AntitrapApp).container

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onOpenMoney = { navController.navigate(Routes.MONEY) },
                onOpenEscape = {},
                onOpenCheck = {}
            )
        }
        composable(Routes.MONEY) {
            MoneyScreen(
                viewModel = viewModel { MoneyViewModel(container.moneyRepository) },
                onOpenSettings = { navController.navigate(Routes.MONEY_SETTINGS) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.MONEY_SETTINGS) {
            MoneySettingsScreen(
                viewModel = viewModel { MoneyViewModel(container.moneyRepository) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
