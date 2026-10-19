package com.greentech.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalHapticFeedback
import com.greentech.app.haptics.HapticaVibrador

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Edge-to-edge; el estilo de las barras se ajusta al tema desde App() (AjustarBarrasSistema).
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            // Vibraciones cortas predefinidas del sistema (VibratorManager / Vibrator) para toda la app.
            val haptica = remember { HapticaVibrador(applicationContext) }
            CompositionLocalProvider(LocalHapticFeedback provides haptica) { App() }
        }
    }
}
