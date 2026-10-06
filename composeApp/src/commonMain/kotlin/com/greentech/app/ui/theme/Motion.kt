package com.greentech.app.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Movimiento de GreenTech UI: duraciones cortas (100 · 200 · 300 ms) y resortes suaves.
 *
 * Con [reducir] activo (Perfil → "Reducir animaciones") todas las duraciones valen 0,
 * los resortes se vuelven instantáneos y no hay animaciones en bucle. Es estado observable:
 * las pantallas se recomponen solas al cambiarlo.
 */
object Motion {
    const val CORTA = 100
    const val MEDIA = 200
    const val LARGA = 300
    const val NAVEGACION = 250
    const val CONTADOR = 600
    const val ENTRADA_GRAFICA = 600
    const val ESCALON_GRAFICA = 40
    const val ESCALON_LISTA = 30L
    const val ESCALON_MAXIMO = 10
    const val CHECK_GUARDADO = 600L
    const val ESCALA_PRESION = 0.97f

    /** Preferencia "Reducir animaciones". La escribe [PreferenciasApariencia]. */
    var reducir by mutableStateOf(false)

    /** Curva "emphasized" de M3 para tweens. */
    val easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    /** Duración efectiva: 0 cuando se reducen las animaciones. */
    fun ms(duracion: Int): Int = if (reducir) 0 else duracion

    fun <T> rapida(): FiniteAnimationSpec<T> = tween(ms(CORTA), easing = easing)
    fun <T> media(): FiniteAnimationSpec<T> = tween(ms(MEDIA), easing = easing)
    fun <T> lenta(): FiniteAnimationSpec<T> = tween(ms(LARGA), easing = easing)
    fun <T> tween(duracion: Int, retraso: Int = 0): FiniteAnimationSpec<T> =
        androidx.compose.animation.core.tween(ms(duracion), delayMillis = if (reducir) 0 else retraso, easing = easing)

    /** Resorte expresivo por defecto: amortiguado (0.8) y de rigidez media-baja. */
    fun <T> resorte(): FiniteAnimationSpec<T> =
        if (reducir) snap() else spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow)

    /** Resorte para respuestas al toque (presión, morph). */
    fun <T> resorteRapido(): FiniteAnimationSpec<T> =
        if (reducir) snap() else spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium)

    /** Resorte con rebote leve para entrada de hojas inferiores e indicadores. */
    fun <T> resorteEntrada(): FiniteAnimationSpec<T> =
        if (reducir) snap() else spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessLow)
}
