package com.greentech.app.ui.components.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.GlassSurface
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.Motion
import com.greentech.app.ui.theme.tabular

/**
 * Reglas visuales comunes de las gráficas minimalistas de GreenTech UI (ver design-system/MASTER.md):
 * - Un solo grosor de línea (2.5 dp) para líneas y sparklines; guías de 1 dp en outlineVariant.
 * - Colores por serie desde `GreenTechTheme.colors.series` (ventas, merma, humedad, temperatura).
 * - Etiqueta directa en el periodo resaltado o el último punto; tooltip de vidrio de 128 dp al tocar.
 * - Entrada única de 600 ms (+40 ms por elemento) con desaceleración cúbica; 0 ms con "Reducir animaciones".
 */
internal object Grafica {
    /** Máximo 3 líneas guía horizontales de 1 dp. */
    const val LINEAS_GUIA = 3
    val radioBarra = 6.dp
    val grosorLinea = 2.5.dp
    val grosorGuia = 1.dp
    val radioPunto = 4.dp
    val radioPuntoMini = 3.dp
    val altoBarraHorizontal = 16.dp
    const val ALFA_ATENUADO = 0.4f
    /** Desaceleración cúbica (ease-out) de la entrada; la misma que aplica [progresoEscalonado]. */
    val easingEntrada = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)
}

/**
 * Progreso de entrada (0→1) que se reproduce una sola vez por [clave]: 600 ms más 40 ms por
 * elemento escalonado. Con "Reducir animaciones" empieza completo.
 */
@Composable
internal fun rememberEntrada(clave: Any?, elementos: Int = 1): State<Float> {
    val progreso = remember(clave) { Animatable(if (Motion.reducir) 1f else 0f) }
    LaunchedEffect(clave) {
        val total = Motion.ENTRADA_GRAFICA + Motion.ESCALON_GRAFICA * (elementos - 1).coerceAtLeast(0)
        progreso.animateTo(1f, tween(Motion.ms(total), easing = LinearEasing))
    }
    return progreso.asState()
}

/** Progreso del elemento [indice] dentro de una entrada escalonada de [elementos]. */
internal fun progresoEscalonado(global: Float, indice: Int, elementos: Int): Float {
    val total = Motion.ENTRADA_GRAFICA + Motion.ESCALON_GRAFICA * (elementos - 1).coerceAtLeast(0)
    val inicio = indice * Motion.ESCALON_GRAFICA.toFloat() / total
    val fin = inicio + Motion.ENTRADA_GRAFICA.toFloat() / total
    val t = ((global - inicio) / (fin - inicio)).coerceIn(0f, 1f)
    // Desaceleración suave (ease-out cúbico).
    return 1f - (1f - t) * (1f - t) * (1f - t)
}

/** Líneas guía horizontales (base, mitad y tope) en outlineVariant. */
internal fun DrawScope.lineasGuia(color: Color, alto: Float = size.height) {
    repeat(Grafica.LINEAS_GUIA) { i ->
        val y = alto * i / (Grafica.LINEAS_GUIA - 1)
        drawLine(color, Offset(0f, y), Offset(size.width, y), strokeWidth = Grafica.grosorGuia.toPx())
    }
}

/** Rectángulo con solo las esquinas superiores redondeadas (barras verticales). */
internal fun DrawScope.barraVertical(color: Color, izquierda: Float, ancho: Float, alto: Float, base: Float) {
    if (alto <= 0f) return
    val r = minOf(Grafica.radioBarra.toPx(), ancho / 2, alto)
    val path = Path().apply {
        addRoundRect(
            RoundRect(
                left = izquierda, top = base - alto, right = izquierda + ancho, bottom = base,
                topLeftCornerRadius = CornerRadius(r, r), topRightCornerRadius = CornerRadius(r, r),
                bottomLeftCornerRadius = CornerRadius.Zero, bottomRightCornerRadius = CornerRadius.Zero,
            ),
        )
    }
    drawPath(path, color)
}

/** Barra horizontal con el extremo final redondeado. */
internal fun DrawScope.barraHorizontal(color: Color, top: Float, alto: Float, largo: Float) {
    if (largo <= 0f) return
    val r = minOf(Grafica.radioBarra.toPx(), alto / 2, largo)
    val path = Path().apply {
        addRoundRect(
            RoundRect(
                left = 0f, top = top, right = largo, bottom = top + alto,
                topLeftCornerRadius = CornerRadius.Zero, bottomLeftCornerRadius = CornerRadius.Zero,
                topRightCornerRadius = CornerRadius(r, r), bottomRightCornerRadius = CornerRadius(r, r),
            ),
        )
    }
    drawPath(path, color)
}

/**
 * Toque para inspeccionar: mientras el dedo está abajo se reporta la posición; al soltar, null.
 * No consume el gesto, así el scroll de la pantalla sigue funcionando.
 */
internal fun Modifier.inspeccionar(alCambiar: (Offset?) -> Unit): Modifier = pointerInput(Unit) {
    detectarInspeccion(alCambiar)
}

private suspend fun PointerInputScope.detectarInspeccion(alCambiar: (Offset?) -> Unit) {
    awaitEachGesture {
        val abajo = awaitFirstDown(requireUnconsumed = false)
        alCambiar(abajo.position)
        while (true) {
            val evento = awaitPointerEvent()
            val cambio = evento.changes.firstOrNull { it.id == abajo.id } ?: break
            if (!cambio.pressed) break
            alCambiar(cambio.position)
        }
        alCambiar(null)
    }
}

/** Tooltip pequeño de vidrio ligero con el valor exacto. */
@Composable
internal fun TooltipGrafica(titulo: String, lineas: List<String>, modifier: Modifier = Modifier) {
    // Sin fuente de desenfoque propia: la gráfica está dentro del contenido desenfocable,
    // así que se usa la superficie translúcida de respaldo.
    GlassSurface(modifier, shape = GtShapes.campo, fuente = null, elevada = true) {
        Column(Modifier.padding(horizontal = Dimens.md, vertical = Dimens.sm)) {
            Text(titulo, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
            lineas.forEach {
                Text(it, style = MaterialTheme.typography.labelLarge.tabular(), color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

internal fun Size.centroX(indice: Int, total: Int): Float = width * (indice + 0.5f) / total
