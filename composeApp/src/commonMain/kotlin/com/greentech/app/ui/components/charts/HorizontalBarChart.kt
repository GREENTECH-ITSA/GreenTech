package com.greentech.app.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.formatearNumero
import com.greentech.app.ui.theme.tabular

/**
 * Barras horizontales (ranking): etiqueta a la izquierda, barra con el extremo redondeado y el
 * valor como etiqueta directa al final. La barra mayor (o la tocada) va en [color]; el resto en
 * onSurfaceVariant al 40 %.
 */
@Composable
fun HorizontalBarChart(
    datos: List<Pair<String, Int>>,
    color: Color,
    descripcion: String,
    modifier: Modifier = Modifier,
    sufijo: String = "",
) {
    val entrada by rememberEntrada(datos, datos.size)
    val maximo = (datos.maxOfOrNull { it.second } ?: 0).coerceAtLeast(1)
    val indiceMayor = datos.indices.maxByOrNull { datos[it].second } ?: -1
    var tocado by remember { mutableStateOf<Int?>(null) }
    val atenuado = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = Grafica.ALFA_ATENUADO)
    val resumen = "$descripcion. " + datos.joinToString(". ") { "${it.first}: ${formatearNumero(it.second.toDouble())}$sufijo" }

    Column(modifier.clearAndSetSemantics { contentDescription = resumen }, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        datos.forEachIndexed { i, (nombre, valor) ->
            val destacado = i == (tocado ?: indiceMayor)
            Row(
                Modifier.fillMaxWidth().heightIn(min = 32.dp).inspeccionar { tocado = if (it != null) i else null },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    nombre,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (destacado) FontWeight.SemiBold else FontWeight.Medium),
                    color = if (destacado) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(104.dp),
                )
                val p = progresoEscalonado(entrada, i, datos.size)
                Canvas(Modifier.weight(1f).height(16.dp)) {
                    barraHorizontal(if (destacado) color else atenuado, 0f, size.height, size.width * (valor.toFloat() / maximo) * p)
                }
                Text(
                    formatearNumero(valor.toDouble()) + sufijo,
                    style = MaterialTheme.typography.labelLarge.tabular(),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(56.dp),
                )
            }
        }
    }
}
