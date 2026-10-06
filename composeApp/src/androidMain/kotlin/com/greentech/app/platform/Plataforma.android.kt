package com.greentech.app.platform

import android.graphics.Color
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.input.key.KeyEvent

actual val desenfoqueDisponible: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

actual fun KeyEvent.bloqMayusActivo(): Boolean? = nativeKeyEvent.isCapsLockOn

@Composable
actual fun AjustarBarrasSistema(oscuro: Boolean) {
    val actividad = LocalActivity.current as? ComponentActivity ?: return
    LaunchedEffect(actividad, oscuro) {
        val estilo = if (oscuro) {
            SystemBarStyle.dark(Color.TRANSPARENT)
        } else {
            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        }
        actividad.enableEdgeToEdge(statusBarStyle = estilo, navigationBarStyle = estilo)
    }
}
