package com.greentech.app.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.formatearNumero
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.tabular

/** Serie de una gráfica de barras. */
data class SerieBarras(val nombre: String, val color: Color, val valores: List<Int>)

/**
 * Barras agrupadas minimalistas: sin ejes ni bordes, 3 líneas guía, barras de ancho uniforme con
 * esquinas superiores de 6 dp. El periodo [resaltado] (por defecto el último) va a color completo
 * con etiquetas directas; los demás, atenuados al 40 %. Al tocar aparece un tooltip con el valor
 * exacto y una guía vertical. Las barras crecen desde la base (600 ms, escalonado 40 ms).
 */
@Composable
fun BarChart(
    etiquetas: List<String>,
    series: List<SerieBarras>,
    descripcion: String,
    modifier: Modifier = Modifier,
    // Por defecto se resalta el último periodo con datos (el mes en curso puede estar casi vacío).
    resaltado: Int = etiquetas.indices.lastOrNull { i -> series.any { it.valores.getOrElse(i) { 0 } > 0 } } ?: etiquetas.lastIndex,
    alto: Dp = 168.dp,
) {
    val entrada by rememberEntrada(series, etiquetas.size)
    val medidor = rememberTextMeasurer()
    val guia = MaterialTheme.colorScheme.outlineVariant
    val texto = MaterialTheme.colorScheme.onSurface
    val estiloEtiqueta = MaterialTheme.typography.labelMedium.tabular()
    var tocado by remember { mutableStateOf<Int?>(null) }
    val activo = tocado ?: resaltado
    val maximo = (series.flatMap { it.valores }.maxOrNull() ?: 0).coerceAtLeast(1)
    val resumen = descripcion + ". " + etiquetas.mapIndexed { i, e ->
        e + ": " + series.joinToString(", ") { "${it.nombre} ${formatearNumero(it.valores.getOrElse(i) { 0 }.toDouble())}" }
    }.joinToString(". ")

    Column(modifier.clearAndSetSemantics { contentDescription = resumen }) {
        if (series.size > 1) {
            Leyenda(series.map { it.nombre to it.color })
            Spacer(Modifier.height(Dimens.sm))
        }
        BoxWithConstraints(Modifier.fillMaxWidth().height(alto)) {
            val anchoPx = constraints.maxWidth.toFloat()
            Canvas(
                Modifier.fillMaxWidth().height(alto).inspeccionar { pos ->
                    tocado = pos?.let { ((it.x / anchoPx) * etiquetas.size).toInt().coerceIn(0, etiquetas.lastIndex) }
                },
            ) {
                val margenSuperior = 22.dp.toPx()
                val base = size.height
                val altoUtil = base - margenSuperior
                lineasGuia(guia)
                if (etiquetas.isEmpty()) return@Canvas
                val grupo = size.width / etiquetas.size
                val separacion = 4.dp.toPx()
                val anchoBarra = ((grupo * 0.62f) - separacion * (series.size - 1)) / series.size.coerceAtLeast(1)
                etiquetas.indices.forEach { i ->
                    val p = progresoEscalonado(entrada, i, etiquetas.size)
                    val inicio = grupo * i + (grupo - (anchoBarra * series.size + separacion * (series.size - 1))) / 2
                    series.forEachIndexed { j, s ->
                        val valor = s.valores.getOrElse(i) { 0 }
                        val h = altoUtil * (valor.toFloat() / maximo) * p
                        val x = inicio + j * (anchoBarra + separacion)
                        val color = if (i == activo) s.color else s.color.copy(alpha = Grafica.ALFA_ATENUADO)
                        barraVertical(color, x, anchoBarra, h, base)
                        // Etiqueta directa encima de cada barra del periodo resaltado.
                        if (i == activo && p >= 1f) {
                            val medida = medidor.measure(formatearNumero(valor.toDouble()), estiloEtiqueta.copy(color = texto))
                            drawText(
                                medida,
                                topLeft = Offset(
                                    (x + anchoBarra / 2 - medida.size.width / 2).coerceIn(0f, size.width - medida.size.width),
                                    (base - h - medida.size.height - 2.dp.toPx()).coerceAtLeast(0f),
                                ),
                            )
                        }
                    }
                }
                tocado?.let { i ->
                    val x = size.centroX(i, etiquetas.size)
                    drawLine(texto.copy(alpha = 0.5f), Offset(x, 0f), Offset(x, base), strokeWidth = Grafica.grosorGuia.toPx())
                }
            }
            tocado?.let { i ->
                val anchoGrupo = maxWidth / etiquetas.size
                val centro = anchoGrupo * (i + 0.5f)
                TooltipGrafica(
                    titulo = etiquetas[i],
                    lineas = series.map { "${it.nombre}: ${formatearNumero(it.valores.getOrElse(i) { 0 }.toDouble())}" },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .wrapContentSize()
                        .offset(x = (centro - Dimens.tooltipGrafica / 2).coerceIn(0.dp, maxWidth - Dimens.tooltipGrafica))
                        .width(Dimens.tooltipGrafica),
                )
            }
        }
        Spacer(Modifier.height(Dimens.sm))
        Row(Modifier.fillMaxWidth()) {
            etiquetas.forEachIndexed { i, e ->
                Text(
                    e,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (i == activo) FontWeight.SemiBold else FontWeight.Medium),
                    color = if (i == activo) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/**
 * Leyenda pequeña, en línea y arriba (solo cuando hay más de una serie). Cada serie tiene además su
 * forma (círculo, cuadrado…) para no depender solo del color.
 */
@Composable
internal fun Leyenda(series: List<Pair<String, Color>>) {
    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.lg), verticalAlignment = Alignment.CenterVertically) {
        series.forEachIndexed { i, (nombre, color) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(Dimens.puntoLeyenda).clip(if (i % 2 == 0) GtShapes.circulo else RoundedCornerShape(Dimens.xxs)).background(color))
                Spacer(Modifier.width(Dimens.sm))
                Text(nombre, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
