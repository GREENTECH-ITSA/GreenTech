package com.greentech.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.notification.Notificacion
import com.greentech.app.ui.notification.NotificationManager
import com.greentech.app.ui.notification.TipoNotificacion
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.Motion
import com.greentech.app.ui.theme.ParColores
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Banner de notificación de vidrio: borde de color según el tipo, ícono animado (el check de éxito
 * se dibuja con un trazo), título, mensaje y botón cerrar de 48 dp.
 */
@Composable
fun GtNotification(notificacion: Notificacion, onCerrar: () -> Unit, modifier: Modifier = Modifier) {
    val colores = coloresNotificacion(notificacion.tipo)
    GlassSurface(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = Dimens.anchoMaximo)
            .semantics { liveRegion = LiveRegionMode.Assertive },
        shape = GtShapes.mosaico,
        elevada = true,
        bordeColor = colores.contenido,
    ) {
        Row(Modifier.padding(start = Dimens.lg, top = Dimens.md, bottom = Dimens.md, end = Dimens.xs), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(Dimens.iconoGrande), contentAlignment = Alignment.Center) {
                IconoNotificacion(notificacion.tipo, colores)
            }
            Spacer(Modifier.width(Dimens.md))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Dimens.xxs)) {
                Text(notificacion.titulo, style = GtTexto.tituloTarjeta, color = MaterialTheme.colorScheme.onSurface)
                Text(notificacion.mensaje, style = GtTexto.ayuda, color = MaterialTheme.colorScheme.onSurface)
            }
            IconButton(onClick = onCerrar, modifier = Modifier.size(Dimens.tactilMin)) {
                Icon(Icons.Outlined.Close, contentDescription = Textos.Comun.CERRAR, tint = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun coloresNotificacion(tipo: TipoNotificacion): ParColores = when (tipo) {
    TipoNotificacion.EXITO -> GreenTechTheme.colors.exito
    TipoNotificacion.ERROR -> GreenTechTheme.colors.error
    TipoNotificacion.ADVERTENCIA -> GreenTechTheme.colors.advertencia
    TipoNotificacion.INFO -> GreenTechTheme.colors.info
}

@Composable
private fun IconoNotificacion(tipo: TipoNotificacion, colores: ParColores) {
    if (tipo == TipoNotificacion.EXITO) {
        CheckAnimado(Modifier.size(Dimens.iconoMediano), color = colores.contenido)
        return
    }
    val icono: ImageVector = when (tipo) {
        TipoNotificacion.ERROR -> Icons.Outlined.ErrorOutline
        TipoNotificacion.ADVERTENCIA -> Icons.Outlined.WarningAmber
        else -> Icons.Outlined.Info
    }
    // Entrada con un pequeño "pop" (escala con resorte).
    val escala = remember { Animatable(if (Motion.reducir) 1f else 0.6f) }
    LaunchedEffect(Unit) { escala.animateTo(1f, Motion.resorteEntrada()) }
    Icon(
        icono,
        contentDescription = null,
        tint = colores.contenido,
        modifier = Modifier.size(Dimens.iconoMediano).graphicsLayer {
            scaleX = escala.value
            scaleY = escala.value
        },
    )
}

/**
 * Muestra las notificaciones de [manager] una por una desde arriba (desliza + fade).
 * Se cierran solas a los 3 s (errores a los 5 s) o con la X. Vibra en éxito y error.
 */
@Composable
fun GtNotificationHost(manager: NotificationManager, modifier: Modifier = Modifier) {
    var actual by remember { mutableStateOf<Notificacion?>(null) }
    var visible by remember { mutableStateOf(false) }
    val cierre = remember { Channel<Unit>(Channel.CONFLATED) }
    val haptica = LocalHapticFeedback.current

    LaunchedEffect(manager) {
        manager.notificaciones.collect { notificacion ->
            cierre.tryReceive()
            actual = notificacion
            visible = true
            when (notificacion.tipo) {
                TipoNotificacion.EXITO -> haptica.performHapticFeedback(HapticFeedbackType.Confirm)
                TipoNotificacion.ERROR -> haptica.performHapticFeedback(HapticFeedbackType.Reject)
                else -> Unit
            }
            val duracion = if (notificacion.tipo == TipoNotificacion.ERROR) {
                NotificationManager.DURACION_ERROR_MS
            } else {
                NotificationManager.DURACION_MS
            }
            withTimeoutOrNull(duracion) { cierre.receive() }
            visible = false
            delay(Motion.LARGA.toLong())
        }
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = Dimens.lg, vertical = Dimens.sm),
        enter = slideInVertically(Motion.resorte()) { -it } + fadeIn(Motion.media()),
        exit = slideOutVertically(Motion.media()) { -it } + fadeOut(Motion.media()),
    ) {
        actual?.let { n ->
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                // key: cada notificación nueva vuelve a animar su ícono.
                androidx.compose.runtime.key(n.id) { GtNotification(n, onCerrar = { cierre.trySend(Unit) }) }
            }
        }
    }
}
