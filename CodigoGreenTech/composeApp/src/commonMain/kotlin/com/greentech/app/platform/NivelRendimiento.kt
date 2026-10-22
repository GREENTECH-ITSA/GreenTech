package com.greentech.app.platform

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Nivel de rendimiento del equipo (docs/RENDIMIENTO.md, sección 5). Se calcula una vez al arrancar.
 *
 * En nivel [BAJO] se usan las variantes ligeras de los efectos: sin desenfoque real (superficie
 * semi-opaca), sin animaciones en bucle (el fondo del Login queda quieto), entradas escalonadas más
 * cortas y sombras mínimas. En [MEDIO] y [ALTO] todo se ve como está diseñado.
 */
@Immutable
enum class NivelRendimiento {
    BAJO, MEDIO, ALTO;

    val bajo: Boolean get() = this == BAJO
}

/** Nivel del equipo actual. En previews y pruebas vale [NivelRendimiento.ALTO]. */
val LocalNivelRendimiento = staticCompositionLocalOf { NivelRendimiento.ALTO }

/** Lo detecta la plataforma (memoria, núcleos, clase de rendimiento). */
expect fun detectarNivelRendimiento(): NivelRendimiento

/** true si el sistema tiene las animaciones desactivadas (escala de animación en 0 o ahorro de batería extremo). */
expect fun animacionesDelSistemaDesactivadas(): Boolean
