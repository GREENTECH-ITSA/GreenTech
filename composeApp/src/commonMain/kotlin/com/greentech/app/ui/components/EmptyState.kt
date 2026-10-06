package com.greentech.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme

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
        Text(titulo, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Text(
            mensaje,
            style = MaterialTheme.typography.bodyLarge,
            color = GreenTechTheme.colors.inkSecondary,
            textAlign = TextAlign.Center,
        )
        if (textoAccion != null && onAccion != null) {
            Spacer(Modifier.height(Dimens.sm))
            GtButton(textoAccion, onAccion, icono = iconoAccion)
        }
    }
}
