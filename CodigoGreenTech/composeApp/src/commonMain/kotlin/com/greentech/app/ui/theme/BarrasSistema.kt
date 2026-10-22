package com.greentech.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

/**
 * Pantallas siempre oscuras (el escáner) piden íconos claros en las barras del sistema mientras están
 * visibles, aunque el tema de la app sea Claro. App lo combina con el tema activo.
 */
object BarrasSistema {
    var pantallasOscuras by mutableIntStateOf(0)
        private set

    @Composable
    fun ForzarOscuras() {
        DisposableEffect(Unit) {
            pantallasOscuras++
            onDispose { pantallasOscuras-- }
        }
    }
}
