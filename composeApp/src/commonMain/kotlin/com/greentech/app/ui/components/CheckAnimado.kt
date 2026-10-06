package com.greentech.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import com.greentech.app.ui.theme.Motion

/**
 * Check que se dibuja con un trazo animado (300 ms) dentro de un círculo de contorno.
 * Se usa en el banner de éxito y en el botón tras guardar.
 */
@Composable
fun CheckAnimado(modifier: Modifier = Modifier, color: Color, conCirculo: Boolean = true) {
    val progreso = remember { Animatable(if (Motion.reducir) 1f else 0f) }
    LaunchedEffect(Unit) { progreso.animateTo(1f, Motion.tween(Motion.LARGA)) }
    val trazo = remember { Path() }
    val medida = remember { PathMeasure() }
    Canvas(modifier) {
        val grosor = size.minDimension * 0.1f
        val estilo = Stroke(width = grosor, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val p = progreso.value
        if (conCirculo) {
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * minOf(1f, p * 1.4f),
                useCenter = false,
                topLeft = Offset(grosor / 2, grosor / 2),
                size = androidx.compose.ui.geometry.Size(size.width - grosor, size.height - grosor),
                style = Stroke(width = grosor * 0.8f, cap = StrokeCap.Round),
            )
        }
        val check = Path().apply {
            moveTo(size.width * 0.28f, size.height * 0.52f)
            lineTo(size.width * 0.44f, size.height * 0.67f)
            lineTo(size.width * 0.73f, size.height * 0.36f)
        }
        medida.setPath(check, false)
        trazo.reset()
        val avance = ((p - 0.3f) / 0.7f).coerceIn(0f, 1f)
        medida.getSegment(0f, medida.length * avance, trazo, true)
        drawPath(trazo, color, style = estilo)
    }
}
