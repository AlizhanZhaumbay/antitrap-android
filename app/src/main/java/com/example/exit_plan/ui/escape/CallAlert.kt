package com.example.exit_plan.ui.escape

import android.content.Context
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

class CallAlert(private val context: Context) {
    private var ringtone: Ringtone? = null
    private val vibrator: Vibrator = vibratorOf(context)

    fun start() {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        ringtone = RingtoneManager.getRingtone(context, uri)?.apply {
            audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) isLooping = true
            play()
        }
        vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 800, 700), 0))
    }

    fun stop() {
        ringtone?.stop()
        ringtone = null
        vibrator.cancel()
    }
}

fun playSmsAlert(context: Context) {
    val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    RingtoneManager.getRingtone(context, uri)?.apply {
        audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        play()
    }
    vibratorOf(context).vibrate(VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 150), -1))
}

private fun vibratorOf(context: Context): Vibrator =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.getSystemService(VibratorManager::class.java).defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Vibrator::class.java)
    }

@Composable
fun CallAlertEffect(active: Boolean) {
    val context = LocalContext.current
    DisposableEffect(active) {
        val alert = if (active) CallAlert(context).also { it.start() } else null
        onDispose { alert?.stop() }
    }
}
