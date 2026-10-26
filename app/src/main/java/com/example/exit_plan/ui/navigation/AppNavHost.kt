package com.example.exit_plan.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.exit_plan.AntitrapApp
import com.example.exit_plan.domain.escape.Scenario
import com.example.exit_plan.ui.check.CheckCountdownScreen
import com.example.exit_plan.ui.check.CheckSetupScreen
import com.example.exit_plan.ui.check.CheckState
import com.example.exit_plan.ui.check.CheckViewModel
import com.example.exit_plan.ui.components.CallWindowEffect
import com.example.exit_plan.ui.components.KeepScreenOn
import com.example.exit_plan.ui.components.SmsBanner
import com.example.exit_plan.ui.escape.ActiveCallScreen
import com.example.exit_plan.ui.escape.ConversationScreen
import com.example.exit_plan.ui.escape.EscapeRunScreen
import com.example.exit_plan.ui.escape.EscapeState
import com.example.exit_plan.ui.escape.EscapeViewModel
import com.example.exit_plan.ui.escape.IncomingCallScreen
import com.example.exit_plan.ui.escape.LegendScreen
import com.example.exit_plan.ui.escape.playSmsAlert
import com.example.exit_plan.ui.home.HomeScreen
import com.example.exit_plan.ui.money.MoneyScreen
import com.example.exit_plan.ui.money.MoneySettingsScreen
import com.example.exit_plan.ui.money.MoneyViewModel
import com.example.exit_plan.ui.scenarios.ScenarioEditScreen
import com.example.exit_plan.ui.scenarios.ScenarioEditViewModel
import com.example.exit_plan.ui.scenarios.ScenarioListScreen
import com.example.exit_plan.ui.scenarios.ScenariosViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val container = (context.applicationContext as AntitrapApp).container

    val escapeViewModel: EscapeViewModel = viewModel { EscapeViewModel(container.scenarioRepository) }
    val escapeState by escapeViewModel.state.collectAsStateWithLifecycle()

    val checkViewModel: CheckViewModel = viewModel { CheckViewModel() }
    val checkState by checkViewModel.state.collectAsStateWithLifecycle()

    val escapeRoute = escapeState.route()
    LaunchedEffect(escapeRoute) { navController.syncEscapeRoute(escapeRoute) }

    KeepScreenOn(enabled = escapeState != EscapeState.Idle || checkState is CheckState.Running)
    CallWindowEffect(
        isCallScreen = escapeState is EscapeState.Ringing || escapeState is EscapeState.InCall,
        hideStatusBar = escapeState is EscapeState.Ringing
    )

    LaunchedEffect(checkViewModel) {
        checkViewModel.expired.collect { scenarioId -> escapeViewModel.start(scenarioId) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = Routes.HOME) {
            composable(Routes.HOME) {
                val runningCheck = checkState as? CheckState.Running
                HomeScreen(
                    onOpenMoney = { navController.navigate(Routes.MONEY) },
                    onOpenEscape = { navController.navigate(Routes.SCENARIOS) },
                    onOpenCheck = {
                        navController.navigate(if (runningCheck != null) Routes.CHECK_COUNTDOWN else Routes.CHECK_SETUP)
                    },
                    checkSubtitle = runningCheck?.let { "Идёт: ${it.scenarioTitle}. Нажми, чтобы подтвердить, что всё в порядке" }
                        ?: "Фейковый звонок, если не отметишься вовремя"
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

            composable(Routes.SCENARIOS) {
                ScenarioListScreen(
                    viewModel = viewModel { ScenariosViewModel(container.scenarioRepository) },
                    onRun = escapeViewModel::start,
                    onEdit = { id -> navController.navigate(Routes.scenarioEdit(id)) },
                    onCreate = { navController.navigate(Routes.scenarioEdit(Scenario.NEW_ID)) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.SCENARIO_EDIT,
                arguments = listOf(navArgument(Routes.ARG_SCENARIO_ID) { type = NavType.LongType })
            ) { entry ->
                val scenarioId = entry.arguments?.getLong(Routes.ARG_SCENARIO_ID) ?: Scenario.NEW_ID
                ScenarioEditScreen(
                    viewModel = viewModel { ScenarioEditViewModel(scenarioId, container.scenarioRepository) },
                    onDone = { navController.popBackStack() }
                )
            }

            composable(Routes.CHECK_SETUP) {
                val scenariosViewModel = viewModel { ScenariosViewModel(container.scenarioRepository) }
                val scenarios by scenariosViewModel.scenarios.collectAsStateWithLifecycle()
                CheckSetupScreen(
                    scenarios = scenarios,
                    checkState = checkState,
                    onStart = { seconds, scenario ->
                        checkViewModel.start(seconds, scenario)
                        navController.navigate(Routes.CHECK_COUNTDOWN) { popUpTo(Routes.CHECK_SETUP) { inclusive = true } }
                    },
                    onOpenCountdown = {
                        navController.navigate(Routes.CHECK_COUNTDOWN) { popUpTo(Routes.CHECK_SETUP) { inclusive = true } }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.CHECK_COUNTDOWN) {
                CheckCountdownScreen(
                    state = checkState,
                    onConfirmSafe = {
                        checkViewModel.confirmSafe()
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.ESCAPE_RUN) {
                EscapeRunScreen(state = escapeState, onCancel = escapeViewModel::cancel)
            }
            composable(Routes.ESCAPE_CHAT) {
                val chat = escapeState as? EscapeState.Conversation ?: return@composable
                ConversationScreen(
                    scenario = chat.scenario,
                    receivedAtMillis = chat.receivedAtMillis,
                    onShowLegend = escapeViewModel::showLegend
                )
            }
            composable(Routes.ESCAPE_CALL) {
                val scenario = (escapeState as? EscapeState.Active)?.scenario ?: return@composable
                IncomingCallScreen(
                    callerName = scenario.callerName,
                    isRinging = escapeState is EscapeState.Ringing,
                    onAccept = escapeViewModel::accept,
                    onDecline = escapeViewModel::decline
                )
            }
            composable(Routes.ESCAPE_TALK) {
                val scenario = (escapeState as? EscapeState.Active)?.scenario ?: return@composable
                ActiveCallScreen(
                    callerName = scenario.callerName,
                    startedAtMillis = (escapeState as? EscapeState.InCall)?.startedAtMillis,
                    onEnd = escapeViewModel::endCall
                )
            }
            composable(Routes.ESCAPE_LEGEND) {
                val scenario = (escapeState as? EscapeState.Active)?.scenario ?: return@composable
                LegendScreen(scenario = scenario, onDone = escapeViewModel::cancel)
            }
        }

        val sms = escapeState as? EscapeState.SmsShown
        AnimatedVisibility(
            visible = sms != null,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(8.dp)
        ) {
            val shown = remember { sms } ?: return@AnimatedVisibility
            LaunchedEffect(Unit) { playSmsAlert(context) }
            SmsBanner(
                sender = shown.scenario.callerName,
                message = shown.scenario.smsText,
                onReply = escapeViewModel::reply,
                onIgnore = escapeViewModel::ignore
            )
        }
    }
}
