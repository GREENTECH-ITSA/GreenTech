package com.greentech.app.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.formatearNumero
import com.greentech.app.ui.theme.tabular

/**
 * Línea minimalista (ej. 24 h de un sensor): una serie de 2.5 dp con extremos redondeados,
 * banda opcional del rango ideal en primaryContainer, 3 líneas guía, punto final de 4 dp con
 * etiqueta directa y tooltip al tocar. Se dibuja de izquierda a derecha una sola vez.
 */
@Composable
fun LineChart(
    valores: List<Double>,
    color: Color,
    unidad: String,
    descripcion: String,
    modifier: Modifier = Modifier,
    escalaMin: Double = valores.minOrNull() ?: 0.0,
    escalaMax: Double = valores.maxOrNull() ?: 1.0,
    idealMin: Double? = null,
    idealMax: Double? = null,
    etiquetaInicio: String? = null,
    etiquetaFin: String? = null,
    decimales: Int = 1,
    alto: Dp = 160.dp,
    etiquetaPunto: (Int) -> String = { "" },
) {
    val entrada by rememberEntrada(valores)
    val medidor = rememberTextMeasurer()
    val guia = MaterialTheme.colorScheme.outlineVariant
    val banda = MaterialTheme.colorScheme.primaryContainer
    val texto = MaterialTheme.colorScheme.onSurface
    val fondo = MaterialTheme.colorScheme.surfaceContainerLow
    val estiloValor = MaterialTheme.typography.labelLarge.tabular()
    var tocado by remember { mutableStateOf<Int?>(null) }
    val ultimo = valores.lastOrNull()
    val resumen = buildString {
        append(descripcion)
        if (ultimo != null) append(". Ahora: ${formatearNumero(ultimo, decimales)} $unidad")
        if (idealMin != null && idealMax != null) append(". Rango ideal ${formatearNumero(idealMin, 0)} a ${formatearNumero(idealMax, 0)} $unidad")
    }

    Column(modifier.clearAndSetSemantics { contentDescription = resumen }) {
        BoxWithConstraints(Modifier.fillMaxWidth().height(alto)) {
            val anchoPx = constraints.maxWidth.toFloat()
            Canvas(
                Modifier.fillMaxWidth().height(alto).inspeccionar { pos ->
                    tocado = if (pos == null || valores.size < 2) null
                    else ((pos.x / anchoPx) * (valores.size - 1) + 0.5f).toInt().coerceIn(0, valores.lastIndex)
                },
            ) {
                val margenSuperior = 24.dp.toPx()
                val margenDerecho = 8.dp.toPx()
                val altoUtil = size.height - margenSuperior
                val ancho = size.width - margenDerecho
                val rango = (escalaMax - escalaMin).takeIf { it > 0 } ?: 1.0
                fun y(v: Double) = margenSuperior + altoUtil * (1f - ((v - escalaMin) / rango).toFloat().coerceIn(0f, 1f))
                fun x(i: Int) = if (valores.size < 2) 0f else ancho * i / (valores.size - 1)

                if (idealMin != null && idealMax != null) {
                    drawRect(banda, topLeft = Offset(0f, y(idealMax)), size = Size(size.width, y(idealMin) - y(idealMax)))
                }
                lineasGuia(guia)
                if (valores.size < 2) return@Canvas
                val path = Path().apply {
                    valores.forEachIndexed { i, v -> if (i == 0) moveTo(x(i), y(v)) else lineTo(x(i), y(v)) }
                }
                clipRect(right = size.width * entrada) {
                    drawPath(path, color, style = Stroke(Grafica.grosorLinea.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                }
                if (entrada >= 1f) {
                    val fin = Offset(x(valores.lastIndex), y(valores.last()))
                    drawCircle(fondo, radius = Grafica.radioPunto.toPx() + 2.dp.toPx(), center = fin)
                    drawCircle(color, radius = Grafica.radioPunto.toPx(), center = fin)
                    if (tocado == null) {
                        val medida = medidor.measure("${formatearNumero(valores.last(), decimales)} $unidad", estiloValor.copy(color = texto))
                        drawText(
                            medida,
                            topLeft = Offset(
                                (fin.x - medida.size.width).coerceAtLeast(0f),
                                (fin.y - medida.size.height - 8.dp.toPx()).coerceAtLeast(0f),
                            ),
                        )
                    }
                }
                tocado?.let { i ->
                    val px = x(i)
                    drawLine(texto.copy(alpha = 0.5f), Offset(px, 0f), Offset(px, size.height), strokeWidth = 1.dp.toPx())
                    drawCircle(color, radius = Grafica.radioPunto.toPx(), center = Offset(px, y(valores[i])))
                }
            }
            tocado?.let { i ->
                val centro = if (valores.size < 2) 0.dp else maxWidth * (i.toFloat() / (valores.size - 1))
                TooltipGrafica(
                    titulo = etiquetaPunto(i),
                    lineas = listOf("${formatearNumero(valores[i], decimales)} $unidad"),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = (centro - 56.dp).coerceIn(0.dp, maxWidth - 112.dp))
                        .width(112.dp),
                )
            }
        }
        if (etiquetaInicio != null || etiquetaFin != null) {
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth()) {
                Text(etiquetaInicio.orEmpty(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                Text(etiquetaFin.orEmpty(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
