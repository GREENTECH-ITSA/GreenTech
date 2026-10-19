package com.greentech.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.Motion

/** Estado de sincronización mostrado en el banner (por ahora simulado). */
data class EstadoSincronizacion(
    val enLinea: Boolean = true,
    val pendientes: Int = 0,
    val sincronizando: Boolean = false,
    val ultimaHora: String = "",
)

/**
 * Banner de conexión y sincronización. El SyncManager (shared/data/sync) lo alimentará
 * cuando exista el modo sin conexión.
 */
@Composable
fun OfflineBanner(estado: EstadoSincronizacion, onSincronizar: () -> Unit, modifier: Modifier = Modifier) {
    val colores = when {
        estado.sincronizando -> GreenTechTheme.colors.info
        !estado.enLinea || estado.pendientes > 0 -> GreenTechTheme.colors.advertencia
        else -> GreenTechTheme.colors.exito
    }
    // El ícono gira solo mientras sincroniza y si no se redujeron las animaciones.
    val angulo = if (estado.sincronizando && !Motion.reducir) {
        val giro by rememberInfiniteTransition(label = "sync").animateFloat(
            0f, 360f, infiniteRepeatable(tween(1_000, easing = LinearEasing), RepeatMode.Restart), label = "giro",
        )
        giro
    } else {
        0f
    }
    GtCard(
        modifier = modifier.semantics { liveRegion = LiveRegionMode.Polite },
        colorFondo = colores.fondo,
                relleno = Dimens.md,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                when {
                    estado.sincronizando -> Icons.Outlined.Sync
                    !estado.enLinea -> Icons.Outlined.CloudOff
                    else -> Icons.Outlined.CloudDone
                },
                contentDescription = null,
                tint = colores.contenido,
                modifier = Modifier.size(Dimens.icono).then(if (estado.sincronizando) Modifier.rotate(-angulo) else Modifier),
            )
            Spacer(Modifier.width(Dimens.md))
            Column(Modifier.weight(1f)) {
                Text(
                    when {
                        estado.sincronizando -> Textos.Sync.SINCRONIZANDO
                        !estado.enLinea -> "${Textos.Sync.SIN_CONEXION} · ${estado.pendientes} ${Textos.Sync.PENDIENTES}"
                        estado.pendientes > 0 -> "${estado.pendientes} ${Textos.Sync.PENDIENTES}"
                        else -> Textos.Sync.SINCRONIZADO
                    },
                    style = GtTexto.tituloTarjeta,
                    color = colores.contenido,
                )
                Text(
                    "${Textos.Sync.ULTIMA}: ${estado.ultimaHora}",
                    style = GtTexto.ayuda,
                    color = colores.contenido,
                )
            }
            TextButton(
                onClick = onSincronizar,
                enabled = !estado.sincronizando,
                modifier = Modifier.heightIn(min = Dimens.tactilMin),
            ) { Text(Textos.Sync.SINCRONIZAR, style = GtTexto.boton, color = colores.contenido) }
        }
    }
}
