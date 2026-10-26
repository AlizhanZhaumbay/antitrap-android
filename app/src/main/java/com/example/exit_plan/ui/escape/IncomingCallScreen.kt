package com.example.exit_plan.ui.escape

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.exit_plan.domain.escape.fakePhoneNumber
import com.example.exit_plan.ui.components.InitialsAvatar
import com.example.exit_plan.ui.components.PulsingAvatar
import com.example.exit_plan.ui.components.SwipeToAnswer
import com.example.exit_plan.ui.theme.CallAccept
import com.example.exit_plan.ui.theme.CallBackground
import com.example.exit_plan.ui.theme.CallDecline

@Composable
fun IncomingCallScreen(
    callerName: String,
    isRinging: Boolean,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    CallAlertEffect(active = isRinging)
    BackHandler {}

    CallLayout(callerName = callerName, subtitle = "Входящий вызов · Мобильный", pulsing = true) {
        SwipeToAnswer(
            acceptColor = CallAccept,
            declineColor = CallDecline,
            onAccept = onAccept,
            onDecline = onDecline
        )
    }
}

@Composable
fun CallLayout(
    callerName: String,
    subtitle: String,
    pulsing: Boolean,
    actions: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CallBackground)
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        Text(callerName, style = MaterialTheme.typography.displaySmall, color = Color.White)
        Spacer(Modifier.height(8.dp))
        Text(fakePhoneNumber(callerName), style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.8f))
        Spacer(Modifier.height(4.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = 0.6f))
        Spacer(Modifier.height(32.dp))
        if (pulsing) PulsingAvatar(name = callerName) else InitialsAvatar(name = callerName, size = 120.dp)
        Spacer(Modifier.weight(1f))
        actions()
    }
}
