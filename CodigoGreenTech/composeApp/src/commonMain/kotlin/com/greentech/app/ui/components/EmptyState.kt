package com.greentech.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtTexto

/**
 * Estado vacío dentro de un mosaico o tarjeta: ícono + texto en una línea, mismo estilo en toda la app
 * (en lugar de "Sin datos" suelto con estilos distintos).
 */
@Composable
fun VacioMosaico(icono: ImageVector, texto: String, modifier: Modifier = Modifier, colorIcono: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Row(modifier.semantics(mergeDescendants = true) {}, verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = colorIcono, modifier = Modifier.size(Dimens.icono))
        Spacer(Modifier.width(Dimens.iconoTexto))
        Text(texto, style = GtTexto.ayuda, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** Estado vacío: ilustración simple con ícono, mensaje y acción sugerida. */
@Composable
fun EmptyState(
    icono: ImageVector,
    titulo: String,
    mensaje: String,
    modifier: Modifier = Modifier,
    textoAccion: String? = null,
    iconoAccion: ImageVector? = null,
    onAccion: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = Dimens.margenLateral, vertical = Dimens.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.md),
    ) {
        Box(
            Modifier.size(Dimens.circuloVacio).clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icono, null, tint = GreenTechTheme.colors.brandGreen, modifier = Modifier.size(Dimens.iconoVacio))
        }
        Spacer(Modifier.height(Dimens.xs))
        Text(titulo, style = GtTexto.tituloTarjeta, textAlign = TextAlign.Center, modifier = Modifier.semantics { heading() })
        Text(
            mensaje,
            style = GtTexto.cuerpo,
            color = GreenTechTheme.colors.inkSecondary,
            textAlign = TextAlign.Center,
        )
        if (textoAccion != null && onAccion != null) {
            Spacer(Modifier.height(Dimens.sm))
            GtButton(textoAccion, onAccion, icono = iconoAccion)
        }
    }
}
