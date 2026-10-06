package com.greentech.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.strings.conValores
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.tabular
import com.greentech.app.util.formatoMiles
import com.greentech.shared.domain.model.Lote

/**
 * Tarjeta limpia de lote (ancho completo): ícono de etapa en contenedor tonal, especie, código,
 * chip de etapa, salud y barra de disponibilidad.
 */
@Composable
fun LoteCard(lote: Lote, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val disponibles = Textos.Lotes.DISPONIBLES_DE.conValores(lote.cantidadDisponible.formatoMiles(), lote.cantidadInicial.formatoMiles())
    val resumen = "${lote.especie.nombre}, ${lote.codigo}, ${lote.etapa.etiqueta}, $disponibles, ${lote.estadoSalud.etiqueta}, ${lote.zona.nombre}"
    GtCard(modifier = modifier.semantics(mergeDescendants = true) { contentDescription = resumen }, onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(GtShapes.campo).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(IconosDominio.etapa(lote.etapa), null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(lote.especie.nombre, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    lote.codigo,
                    style = MaterialTheme.typography.bodySmall.tabular(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(12.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            EtapaChip(lote.etapa)
            SaludChip(lote.estadoSalud)
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                disponibles,
                style = MaterialTheme.typography.bodyMedium.tabular(),
                modifier = Modifier.weight(1f),
            )
            Icon(Icons.Outlined.Place, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(4.dp))
            Text(lote.zona.nombre, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(8.dp))
        BarraDisponibilidad(lote.proporcionDisponible)
    }
}

/** Barra fina de disponibilidad (plantas disponibles / iniciales), con extremos redondeados. */
@Composable
fun BarraDisponibilidad(proporcion: Float, modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxWidth().height(8.dp).clip(GtShapes.pastilla).background(MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Box(
            Modifier.fillMaxWidth(proporcion.coerceIn(0f, 1f)).height(8.dp).clip(GtShapes.pastilla)
                .background(MaterialTheme.colorScheme.primary),
        )
    }
}
