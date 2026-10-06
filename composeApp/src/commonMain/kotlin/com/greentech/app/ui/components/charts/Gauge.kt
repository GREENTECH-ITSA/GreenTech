package com.greentech.app.ui.components.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.formatearNumero
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.Motion
import com.greentech.app.ui.theme.tabular
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

enum class EstadoRango { BAJO, EN_RANGO, ALTO }

fun estadoRango(valor: Double, idealMin: Double, idealMax: Double) = when {
    valor < idealMin -> EstadoRango.BAJO
    valor > idealMax -> EstadoRango.ALTO
    else -> EstadoRango.EN_RANGO
}

/** Texto, ícono y color del estado de un sensor: fuera de rango nunca se indica solo con color. */
data class EstiloRango(val texto: String, val icono: ImageVector, val color: Color)

@Composable
fun EstadoRango.estilo(severo: Boolean = false): EstiloRango = when (this) {
    EstadoRango.EN_RANGO -> EstiloRango(Textos.Condiciones.EN_RANGO, Icons.Outlined.CheckCircle, GreenTechTheme.colors.success)
    EstadoRango.BAJO -> EstiloRango(
        Textos.Condiciones.BAJO, Icons.Outlined.ArrowDownward,
        if (severo) MaterialTheme.colorScheme.error else GreenTechTheme.colors.warning,
    )
    EstadoRango.ALTO -> EstiloRango(
        Textos.Condiciones.ALTO, Icons.Outlined.ArrowUpward,
        if (severo) MaterialTheme.colorScheme.error else GreenTechTheme.colors.warning,
    )
}

/** true si el valor se aleja del rango ideal más de la mitad de su ancho (se marca en error). */
fun esDesvioSevero(valor: Double, idealMin: Double, idealMax: Double): Boolean {
    val margen = (idealMax - idealMin) / 2
    return valor < idealMin - margen || valor > idealMax + margen
}

/**
 * Gauge de sensor: arco de 240°, banda del rango ideal de la especie en primaryContainer, punto
 * con el valor y el número grande al centro con su unidad. Fuera de rango el valor cambia a
 * warning (o error si el desvío es grande) y se agrega un ícono de flecha.
 */
@Composable
fun Gauge(
    valor: Double,
    unidad: String,
    escalaMin: Double,
    escalaMax: Double,
    idealMin: Double,
    idealMax: Double,
    modifier: Modifier = Modifier,
    tamano: Dp = 176.dp,
    decimales: Int = 1,
    titulo: String = "",
) {
    val compacto = tamano < 100.dp
    val mediano = tamano < 150.dp
    val estado = estadoRango(valor, idealMin, idealMax)
    val estilo = estado.estilo(esDesvioSevero(valor, idealMin, idealMax))
    val colorValor = if (estado == EstadoRango.EN_RANGO) MaterialTheme.colorScheme.onSurface else estilo.color
    val colorPunto = if (estado == EstadoRango.EN_RANGO) MaterialTheme.colorScheme.primary else estilo.color
    val pista = MaterialTheme.colorScheme.surfaceContainerHighest
    val banda = MaterialTheme.colorScheme.primaryContainer
    val anillo = MaterialTheme.colorScheme.surfaceContainerLow
    val objetivo = ((valor - escalaMin) / (escalaMax - escalaMin)).coerceIn(0.0, 1.0).toFloat()
    val progreso = remember { Animatable(if (Motion.reducir) objetivo else 0f) }
    LaunchedEffect(objetivo) { progreso.animateTo(objetivo, Motion.tween(Motion.CONTADOR)) }
    val textoValor = formatearNumero(valor, decimales)
    val rango = "${formatearNumero(idealMin, 0)}–${formatearNumero(idealMax, 0)} $unidad"

    Box(
        modifier.size(tamano).clearAndSetSemantics {
            contentDescription = "$titulo $textoValor $unidad, ${estilo.texto}. ${Textos.Condiciones.IDEAL} $rango".trim()
        },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(tamano)) {
            val grosor = when { compacto -> 8.dp.toPx(); mediano -> 11.dp.toPx(); else -> 14.dp.toPx() }
            val inset = grosor / 2 + 2.dp.toPx()
            val arco = Size(size.width - inset * 2, size.height - inset * 2)
            val esquina = Offset(inset, inset)
            val inicio = 150f
            val barrido = 240f
            drawArc(pista, inicio, barrido, false, esquina, arco, style = Stroke(grosor, cap = StrokeCap.Round))
            val fIni = ((idealMin - escalaMin) / (escalaMax - escalaMin)).coerceIn(0.0, 1.0).toFloat()
            val fFin = ((idealMax - escalaMin) / (escalaMax - escalaMin)).coerceIn(0.0, 1.0).toFloat()
            drawArc(banda, inicio + barrido * fIni, barrido * (fFin - fIni), false, esquina, arco, style = Stroke(grosor))
            val angulo = (inicio + barrido * progreso.value) * PI / 180.0
            val radio = arco.width / 2
            val centro = Offset(size.width / 2, size.height / 2)
            val punto = Offset(centro.x + radio * cos(angulo).toFloat(), centro.y + radio * sin(angulo).toFloat())
            drawCircle(anillo, radius = grosor * 0.85f, center = punto)
            drawCircle(colorPunto, radius = grosor * 0.6f, center = punto)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                if (estado != EstadoRango.EN_RANGO) {
                    Icon(estilo.icono, null, tint = estilo.color, modifier = Modifier.size(if (compacto) 16.dp else 24.dp))
                }
                Text(
                    textoValor,
                    style = when {
                        compacto -> MaterialTheme.typography.titleLarge
                        mediano -> MaterialTheme.typography.headlineMedium
                        else -> MaterialTheme.typography.displaySmall
                    }.tabular(),
                    color = colorValor,
                    maxLines = 1,
                )
            }
            Text(unidad, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
