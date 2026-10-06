package com.greentech.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.charts.Gauge
import com.greentech.app.ui.components.charts.esDesvioSevero
import com.greentech.app.ui.components.charts.estadoRango
import com.greentech.app.ui.components.charts.estilo
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.GtShapes

/**
 * Tarjeta de sensor: título con ícono, gauge grande y estado (ícono + texto) con el rango ideal.
 */
@Composable
fun SensorGauge(
    titulo: String,
    valor: Double,
    unidad: String,
    escalaMin: Double,
    escalaMax: Double,
    idealMin: Double,
    idealMax: Double,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    tamano: Dp = 168.dp,
) {
    val estilo = estadoRango(valor, idealMin, idealMax).estilo(esDesvioSevero(valor, idealMin, idealMax))
    Surface(modifier, shape = GtShapes.mosaico, color = MaterialTheme.colorScheme.surfaceContainerLow) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(titulo, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                Icon(icono, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(28.dp))
            }
            Gauge(valor, unidad, escalaMin, escalaMax, idealMin, idealMax, tamano = tamano, titulo = titulo)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(estilo.icono, null, tint = estilo.color, modifier = Modifier.size(20.dp))
                Text(estilo.texto, style = MaterialTheme.typography.labelLarge, color = estilo.color)
            }
            Text(
                "${Textos.Condiciones.IDEAL}: ${formatearNumero(idealMin)}–${formatearNumero(idealMax)} $unidad",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
