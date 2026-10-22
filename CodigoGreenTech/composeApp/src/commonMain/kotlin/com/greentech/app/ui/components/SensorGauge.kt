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
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.GtTexto

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
            Modifier.fillMaxWidth().padding(Dimens.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.sm),
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.sm)) {
                Text(titulo, style = GtTexto.etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                Icon(icono, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(Dimens.iconoMediano))
            }
            Gauge(valor, unidad, escalaMin, escalaMax, idealMin, idealMax, tamano = tamano, titulo = titulo)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.sm)) {
                Icon(estilo.icono, null, tint = estilo.color, modifier = Modifier.size(Dimens.iconoChico))
                Text(estilo.texto, style = GtTexto.etiqueta, color = estilo.color)
            }
            Text(
                "${Textos.Condiciones.IDEAL}: ${formatearNumero(idealMin)}–${formatearNumero(idealMax)} $unidad",
                style = GtTexto.ayuda,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
