package com.greentech.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterAltOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.strings.conValores

/**
 * Línea de estado de una lista: "12 lotes · 2 filtros activos" y, si hay filtros, el botón
 * "Limpiar filtros". El conteo se anuncia al lector de pantalla cuando cambia.
 */
@Composable
fun FiltrosActivos(
    resultados: String,
    filtrosActivos: Int,
    onLimpiar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier.fillMaxWidth().heightIn(min = 56.dp), verticalAlignment = Alignment.CenterVertically) {
        val texto = if (filtrosActivos > 0) "$resultados · ${textoFiltrosActivos(filtrosActivos)}" else resultados
        Text(
            texto,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f).semantics { liveRegion = LiveRegionMode.Polite },
        )
        if (filtrosActivos > 0) {
            TextButton(onClick = onLimpiar, modifier = Modifier.heightIn(min = 56.dp)) {
                Icon(Icons.Outlined.FilterAltOff, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(Textos.Comun.LIMPIAR_FILTROS, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

fun textoFiltrosActivos(n: Int): String = if (n == 1) Textos.Comun.FILTRO_ACTIVO else Textos.Comun.FILTROS_ACTIVOS.conValores(n)

/** "1 lote" / "12 lotes" a partir de los textos singular y plural ("%d lotes"). */
fun textoConteo(n: Int, singular: String, plural: String): String = if (n == 1) singular else plural.conValores(n)

