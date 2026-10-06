package com.greentech.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.Motion
import com.greentech.app.ui.theme.ParColores

/**
 * Grupo de chips grandes (56 dp) de selección única. Con [columnas] > 0 se acomodan
 * en cuadrícula del mismo ancho; si no, fluyen en filas.
 */
@Composable
fun <T> GtChipGroup(
    opciones: List<T>,
    seleccionado: T?,
    onSeleccion: (T) -> Unit,
    etiqueta: (T) -> String,
    modifier: Modifier = Modifier,
    titulo: String? = null,
    icono: ((T) -> ImageVector)? = null,
    colores: (@Composable (T) -> ParColores)? = null,
    columnas: Int = 0,
    error: String? = null,
) {
    Column(modifier.fillMaxWidth().sacudir(disparadorSacudida(error, enfocado = false))) {
        if (titulo != null) {
            Text(titulo, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.size(8.dp))
        }
        if (columnas > 0) {
            Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                opciones.chunked(columnas).forEach { fila ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        fila.forEach { op ->
                            GtChip(
                                texto = etiqueta(op), seleccionado = op == seleccionado, onClick = { onSeleccion(op) },
                                icono = icono?.invoke(op), colores = colores?.invoke(op), modifier = Modifier.weight(1f),
                            )
                        }
                        repeat(columnas - fila.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        } else {
            FlowRow(
                modifier = Modifier.selectableGroup(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                opciones.forEach { op ->
                    GtChip(
                        texto = etiqueta(op), seleccionado = op == seleccionado, onClick = { onSeleccion(op) },
                        icono = icono?.invoke(op), colores = colores?.invoke(op),
                    )
                }
            }
        }
        if (error != null) {
            Spacer(Modifier.size(4.dp))
            MensajeError(error)
        }
    }
}

/** Fila de chips con desplazamiento horizontal para filtros. */
@Composable
fun <T> GtChipFiltro(
    opciones: List<T>,
    seleccionado: T?,
    onSeleccion: (T) -> Unit,
    etiqueta: (T) -> String,
    modifier: Modifier = Modifier,
    margen: Dp = 20.dp,
    icono: ((T) -> ImageVector)? = null,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth().selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = margen),
    ) {
        items(opciones, key = { etiqueta(it) }) { op ->
            GtChip(texto = etiqueta(op), seleccionado = op == seleccionado, onClick = { onSeleccion(op) }, icono = icono?.invoke(op))
        }
    }
}

/**
 * Chip de selección: 56 dp de alto, 16 dp de radio. Seleccionado: contenedor tonal y check;
 * sin seleccionar: contorno en outline (3:1).
 */
@Composable
fun GtChip(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    colores: ParColores? = null,
) {
    val esquema = MaterialTheme.colorScheme
    val acento = colores ?: ParColores(esquema.onSecondaryContainer, esquema.secondaryContainer)
    val fondo by animateColorAsState(
        if (seleccionado) acento.fondo else esquema.surface, Motion.media(), label = "fondoChip",
    )
    val borde by animateColorAsState(
        if (seleccionado) acento.fondo else esquema.outline, Motion.media(), label = "bordeChip",
    )
    val contenido by animateColorAsState(
        if (seleccionado) acento.contenido else esquema.onSurface, Motion.media(), label = "textoChip",
    )
    val interaccion = remember { MutableInteractionSource() }
    Surface(
        modifier = modifier
            .heightIn(min = 56.dp)
            .efectoPresion(interaccion)
            .selectable(selected = seleccionado, onClick = onClick, role = Role.RadioButton, interactionSource = interaccion, indication = ripple()),
        shape = GtShapes.campo,
        color = fondo,
        border = BorderStroke(1.dp, borde),
    ) {
        Row(
            modifier = Modifier.heightIn(min = 56.dp).padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            when {
                seleccionado -> Icon(Icons.Outlined.Check, null, tint = contenido, modifier = Modifier.size(20.dp))
                icono != null -> Icon(icono, null, tint = if (colores != null) colores.contenido else esquema.onSurfaceVariant, modifier = Modifier.size(20.dp))
            }
            if (seleccionado || icono != null) Spacer(Modifier.width(8.dp))
            Text(texto, style = MaterialTheme.typography.labelLarge, color = contenido, textAlign = TextAlign.Center)
        }
    }
}
