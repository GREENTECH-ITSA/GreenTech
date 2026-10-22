package com.greentech.app.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

/**
 * Retroalimentación háptica de la app (LocalHapticFeedback) con el vibrador del sistema:
 * VibratorManager en Android 12+ o Vibrator antes, con efectos cortos predefinidos
 * (EFFECT_CLICK / EFFECT_TICK / EFFECT_DOUBLE_CLICK) cuando existen (Android 10+).
 * En Android 13+ se marca como vibración táctil para respetar el ajuste del sistema.
 */
class HapticaVibrador(context: Context) : HapticFeedback {

    private val vibrador: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
        val vibrador = vibrador?.takeIf { it.hasVibrator() } ?: return
        val efecto = when (hapticFeedbackType) {
            HapticFeedbackType.Confirm, HapticFeedbackType.LongPress, HapticFeedbackType.ToggleOn -> Efecto.CLIC
            HapticFeedbackType.Reject -> Efecto.DOBLE
            else -> Efecto.TICK
        }
        vibrar(vibrador, efecto)
    }

    private fun vibrar(vibrador: Vibrator, efecto: Efecto) {
        val vibracion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            VibrationEffect.createPredefined(efecto.predefinido)
        } else {
            VibrationEffect.createOneShot(efecto.duracionMs, VibrationEffect.DEFAULT_AMPLITUDE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            vibrador.vibrate(vibracion, VibrationAttributes.createForUsage(VibrationAttributes.USAGE_TOUCH))
        } else {
            vibrador.vibrate(vibracion)
        }
    }

    private enum class Efecto(val predefinido: Int, val duracionMs: Long) {
        TICK(VibrationEffect.EFFECT_TICK, 10),
        CLIC(VibrationEffect.EFFECT_CLICK, 20),
        DOBLE(VibrationEffect.EFFECT_DOUBLE_CLICK, 40),
    }
}
