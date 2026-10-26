package com.example.exit_plan.ui.components

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun KeepScreenOn(enabled: Boolean) {
    val view = LocalView.current
    DisposableEffect(view, enabled) {
        view.keepScreenOn = enabled
        onDispose { view.keepScreenOn = false }
    }
}

@Composable
fun CallWindowEffect(isCallScreen: Boolean, hideStatusBar: Boolean) {
    val activity = LocalActivity.current ?: return
    val view = LocalView.current
    val lightThemeIcons = !isSystemInDarkTheme()

    DisposableEffect(isCallScreen, hideStatusBar, lightThemeIcons) {
        val controller = WindowCompat.getInsetsController(activity.window, view)
        controller.isAppearanceLightStatusBars = if (isCallScreen) false else lightThemeIcons
        controller.isAppearanceLightNavigationBars = if (isCallScreen) false else lightThemeIcons
        if (hideStatusBar) {
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.statusBars())
        } else {
            controller.show(WindowInsetsCompat.Type.statusBars())
        }
        onDispose {
            controller.show(WindowInsetsCompat.Type.statusBars())
            controller.isAppearanceLightNavigationBars = lightThemeIcons
            controller.isAppearanceLightStatusBars = lightThemeIcons
        }
    }
}
