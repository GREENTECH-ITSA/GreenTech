package com.greentech.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.ParColores
import com.greentech.shared.domain.model.EstadoSalud
import com.greentech.shared.domain.model.Etapa
import com.greentech.shared.domain.model.Severidad

/** Insignia de estado (pill) con fondo tonal, texto del mismo tono e ícono de 20 dp opcional. */
@Composable
fun StatusChip(
    texto: String,
    colores: ParColores,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
) {
    Row(
        modifier = modifier
            .clip(GtShapes.pastilla)
            .background(colores.fondo)
            .padding(horizontal = Dimens.md, vertical = Dimens.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.xs),
    ) {
        if (icono != null) Icon(icono, contentDescription = null, tint = colores.contenido, modifier = Modifier.size(Dimens.iconoChico))
        Text(texto, style = GtTexto.etiqueta, color = colores.contenido)
    }
}

@Composable
fun EtapaChip(etapa: Etapa, modifier: Modifier = Modifier) =
    StatusChip(etapa.etiqueta, etapa.colores(), modifier, IconosDominio.etapa(etapa))

/** Atajo de [PlantHealthIndicator] en su variante chip. */
@Composable
fun SaludChip(estado: EstadoSalud, modifier: Modifier = Modifier) = PlantHealthIndicator(estado, modifier, VarianteSalud.CHIP)

@Composable
fun SeveridadChip(severidad: Severidad, modifier: Modifier = Modifier) =
    StatusChip(severidad.etiqueta, severidad.colores(), modifier, Icons.Outlined.ErrorOutline)

/**
 * Las etapas comparten un solo acento (primario) para no pasar de dos acentos por pantalla;
 * se distinguen por ícono y texto. La última ("lista para venta") usa el tono de éxito.
 */
@Composable
fun Etapa.colores(): ParColores = when (this) {
    Etapa.LISTA_PARA_VENTA -> GreenTechTheme.colors.exito
    else -> GreenTechTheme.colors.primario
}

@Composable
fun Severidad.colores(): ParColores = when (this) {
    Severidad.BAJA -> GreenTechTheme.colors.advertencia
    Severidad.MEDIA -> GreenTechTheme.colors.peligro
    Severidad.ALTA -> GreenTechTheme.colors.error
}
