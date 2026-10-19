package com.greentech.app.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Mini línea de tendencia para mosaicos: sin guías ni etiquetas, solo la forma y el punto final.
 * Se dibuja de izquierda a derecha una sola vez.
 */
@Composable
fun Sparkline(
    valores: List<Double>,
    color: Color,
    modifier: Modifier = Modifier,
    alto: Dp = 32.dp,
    descripcion: String? = null,
) {
    val entrada by rememberEntrada(valores)
    Canvas(
        modifier.fillMaxWidth().height(alto).clearAndSetSemantics { if (descripcion != null) contentDescription = descripcion },
    ) {
        if (valores.size < 2) return@Canvas
        val radio = Grafica.radioPuntoMini.toPx()
        val min = valores.min()
        val max = valores.max()
        val rango = (max - min).takeIf { it > 0 } ?: 1.0
        val ancho = size.width - radio
        fun x(i: Int) = ancho * i / (valores.size - 1)
        fun y(v: Double) = radio + (size.height - radio * 2) * (1f - ((v - min) / rango).toFloat())
        val path = Path().apply {
            valores.forEachIndexed { i, v -> if (i == 0) moveTo(x(i), y(v)) else lineTo(x(i), y(v)) }
        }
        clipRect(right = size.width * progresoEscalonado(entrada, 0, 1)) {
            drawPath(path, color, style = Stroke(Grafica.grosorLinea.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
        if (entrada >= 1f) drawCircle(color, radius = radio, center = Offset(x(valores.lastIndex), y(valores.last())))
    }
}
