package com.greentech.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.theme.GreenTechTheme
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
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (icono != null) Icon(icono, contentDescription = null, tint = colores.contenido, modifier = Modifier.size(20.dp))
        Text(texto, style = MaterialTheme.typography.labelMedium, color = colores.contenido)
    }
}

@Composable
fun EtapaChip(etapa: Etapa, modifier: Modifier = Modifier) =
    StatusChip(etapa.etiqueta, etapa.colores(), modifier, IconosDominio.etapa(etapa))

@Composable
fun SaludChip(estado: EstadoSalud, modifier: Modifier = Modifier) =
    StatusChip(estado.etiqueta, estado.colores(), modifier, estado.icono())

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
fun EstadoSalud.colores(): ParColores = when (this) {
    EstadoSalud.SANA -> GreenTechTheme.colors.exito
    EstadoSalud.EN_OBSERVACION -> GreenTechTheme.colors.advertencia
    EstadoSalud.AFECTADA -> GreenTechTheme.colors.peligro
    EstadoSalud.CRITICA -> GreenTechTheme.colors.error
}

@Composable
fun Severidad.colores(): ParColores = when (this) {
    Severidad.BAJA -> GreenTechTheme.colors.advertencia
    Severidad.MEDIA -> GreenTechTheme.colors.peligro
    Severidad.ALTA -> GreenTechTheme.colors.error
}

fun EstadoSalud.icono(): ImageVector = when (this) {
    EstadoSalud.SANA -> Icons.Outlined.CheckCircle
    EstadoSalud.EN_OBSERVACION -> Icons.Outlined.Visibility
    EstadoSalud.AFECTADA -> Icons.Outlined.WarningAmber
    EstadoSalud.CRITICA -> Icons.Outlined.ReportProblem
}
