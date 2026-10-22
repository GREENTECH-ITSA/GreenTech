package com.greentech.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.ParColores

/**
 * Acción en mosaico S (ícono outlined de 28 dp + texto), pensada para usarse con guantes.
 * Con [relleno] usa primaryContainer (acciones principales de un mosaico L).
 */
@Composable
fun AccionGrande(
    icono: ImageVector,
    titulo: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    descripcion: String? = null,
    relleno: Boolean = false,
    colorFondo: Color = if (relleno) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerLow,
    colorContenido: Color = if (relleno) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
    colorIcono: Color = if (relleno) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
) {
    val interaccion = remember { MutableInteractionSource() }
    Surface(
        onClick = onClick,
        modifier = modifier.heightIn(min = 112.dp).efectoPresion(interaccion),
        shape = GtShapes.mosaico,
        color = colorFondo,
        contentColor = colorContenido,
        interactionSource = interaccion,
    ) {
        Column(Modifier.padding(Dimens.lg), verticalArrangement = Arrangement.spacedBy(Dimens.md)) {
            Icon(icono, null, tint = colorIcono, modifier = Modifier.size(Dimens.iconoMediano))
            Spacer(Modifier.weight(1f, fill = false))
            // Palabras largas como "Recomendaciones" se reducen un poco en lugar de partirse.
            BasicText(
                titulo,
                style = GtTexto.tituloTarjeta.copy(color = colorContenido),
                maxLines = 1,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = GtTexto.etiqueta.fontSize,
                    maxFontSize = GtTexto.tituloTarjeta.fontSize,
                ),
            )
            if (descripcion != null) {
                Text(
                    descripcion,
                    style = GtTexto.ayuda,
                    color = if (relleno) colorContenido else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** Tarjeta grande de opción única con descripción. */
@Composable
fun TarjetaOpcion(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    seleccionada: Boolean,
    onClick: () -> Unit,
    colores: ParColores,
    modifier: Modifier = Modifier,
) {
    val interaccion = remember { MutableInteractionSource() }
    Surface(
        modifier = modifier
            .heightIn(min = 112.dp)
            .efectoPresion(interaccion)
            .selectable(seleccionada, onClick = onClick, role = Role.RadioButton, interactionSource = interaccion, indication = ripple()),
        shape = GtShapes.mosaico,
        color = if (seleccionada) colores.fondo else MaterialTheme.colorScheme.surfaceContainerLow,
        border = if (seleccionada) BorderStroke(2.dp, colores.contenido) else null,
    ) {
        Column(Modifier.fillMaxWidth().padding(Dimens.lg), verticalArrangement = Arrangement.spacedBy(Dimens.xs)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icono, null, tint = colores.contenido, modifier = Modifier.size(Dimens.iconoMediano))
                Spacer(Modifier.weight(1f))
                Icon(
                    if (seleccionada) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    null,
                    tint = if (seleccionada) colores.contenido else MaterialTheme.colorScheme.outline,
                )
            }
            Text(titulo, style = GtTexto.tituloTarjeta, color = if (seleccionada) colores.contenido else MaterialTheme.colorScheme.onSurface)
            Text(descripcion, style = GtTexto.ayuda, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
