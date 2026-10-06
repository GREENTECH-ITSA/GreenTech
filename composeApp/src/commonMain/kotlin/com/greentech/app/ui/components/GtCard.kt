package com.greentech.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.theme.GtShapes

/**
 * Tarjeta de ancho completo para listas: 20 dp de radio y sin sombra. La jerarquía viene del
 * tono de superficie (surfaceContainerLow); [destacada] usa primaryContainer.
 */
@Composable
fun GtCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    destacada: Boolean = false,
    colorFondo: Color? = null,
    colorBorde: Color? = null,
    relleno: Dp = 16.dp,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    val fondo = colorFondo ?: if (destacada) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow
    val borde = colorBorde?.let { BorderStroke(1.dp, it) }
    if (onClick != null) {
        val interaccion = remember { MutableInteractionSource() }
        Surface(
            onClick = onClick,
            modifier = modifier.fillMaxWidth().efectoPresion(interaccion),
            shape = GtShapes.tarjeta,
            color = fondo,
            border = borde,
            interactionSource = interaccion,
        ) { Column(Modifier.padding(relleno), content = contenido) }
    } else {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = GtShapes.tarjeta,
            color = fondo,
            border = borde,
        ) { Column(Modifier.padding(relleno), content = contenido) }
    }
}
