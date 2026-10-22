package com.greentech.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.ParColores

/**
 * Segmentado grande de selección única con el ButtonGroup conectado de Material 3 Expressive:
 * los extremos son asimétricos y el seleccionado se vuelve pill (shape morph con resorte).
 * Se usa en Venta/Merma, Activos/Inactivos, periodo del dashboard y apariencia.
 *
 * @param descripcion texto opcional bajo la etiqueta (hace el segmento más alto).
 * @param colores colores del segmento seleccionado (por defecto secondaryContainer).
 */
@Composable
fun <T> GtSegmentado(
    opciones: List<T>,
    seleccionado: T?,
    onSeleccion: (T) -> Unit,
    etiqueta: (T) -> String,
    modifier: Modifier = Modifier,
    icono: ((T) -> ImageVector)? = null,
    descripcion: ((T) -> String)? = null,
    colores: (@Composable (T) -> ParColores)? = null,
    alto: Dp = if (descripcion != null) 76.dp else 56.dp,
) {
    val haptica = LocalHapticFeedback.current
    Row(
        modifier.fillMaxWidth().selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
    ) {
        opciones.forEachIndexed { indice, opcion ->
            val marcado = opcion == seleccionado
            val par = colores?.invoke(opcion)
            ToggleButton(
                checked = marcado,
                onCheckedChange = {
                    if (!marcado) haptica.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    onSeleccion(opcion)
                },
                modifier = Modifier.weight(1f).heightIn(min = alto).semantics { role = Role.RadioButton },
                shapes = when (indice) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    opciones.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
                colors = ToggleButtonDefaults.toggleButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    checkedContainerColor = par?.fondo ?: MaterialTheme.colorScheme.secondaryContainer,
                    checkedContentColor = par?.contenido ?: MaterialTheme.colorScheme.onSecondaryContainer,
                ),
                contentPadding = PaddingValues(horizontal = Dimens.md, vertical = Dimens.sm),
            ) {
                val textoIcono = icono?.invoke(opcion)
                if (descripcion != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (textoIcono != null) {
                                Icon(textoIcono, null, modifier = Modifier.size(Dimens.icono))
                                Spacer(Modifier.size(Dimens.sm))
                            }
                            TextoAjustable(etiqueta(opcion), GtTexto.tituloTarjeta)
                        }
                        TextoAjustable(descripcion(opcion), GtTexto.ayuda)
                    }
                } else {
                    if (textoIcono != null) {
                        Icon(textoIcono, null, modifier = Modifier.size(Dimens.iconoChico))
                        Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                    }
                    TextoAjustable(etiqueta(opcion), GtTexto.boton)
                }
            }
        }
    }
}
