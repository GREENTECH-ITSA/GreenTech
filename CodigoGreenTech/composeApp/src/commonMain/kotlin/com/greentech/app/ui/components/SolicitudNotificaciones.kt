package com.greentech.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.greentech.app.platform.Permiso
import com.greentech.app.platform.mostrarAlertaSensor
import com.greentech.app.platform.rememberEstadoPermiso
import com.greentech.app.ui.core.PreferenciasUso
import com.greentech.app.ui.strings.Textos
import com.greentech.shared.di.USE_FAKE_DATA
import com.greentech.shared.domain.model.TipoAlerta
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.domain.usecase.ObtenerAlertasUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Después del primer login, cuando se cierra la bienvenida: explica para qué sirven las
 * notificaciones antes de mostrar el diálogo del sistema. Se pregunta una sola vez por equipo.
 */
@Composable
fun SolicitudNotificaciones(introVisible: Boolean) {
    val preferencias = koinInject<PreferenciasUso>()
    val obtenerAlertas = koinInject<ObtenerAlertasUseCase>()
    val alcance = rememberCoroutineScope()
    val permiso = rememberEstadoPermiso(Permiso.NOTIFICACIONES) { permitido ->
        // Con datos Fake se dispara una vez la alerta simulada de humedad baja para comprobar el canal.
        if (permitido && USE_FAKE_DATA) alcance.launch { probarAlertaSimulada(obtenerAlertas) }
    }
    var mostrar by remember { mutableStateOf(false) }
    LaunchedEffect(introVisible) {
        if (!introVisible && !permiso.concedido && !preferencias.notificacionesPreguntadas) {
            delay(600)
            mostrar = true
        }
    }
    if (!mostrar) return
    ConfirmSheet(
        titulo = Textos.Permisos.NOTIFICACIONES_TITULO,
        mensaje = Textos.Permisos.NOTIFICACIONES_MENSAJE,
        textoConfirmar = Textos.Permisos.PERMITIR,
        textoCancelar = Textos.Permisos.AHORA_NO,
        icono = Icons.Outlined.NotificationsActive,
        onConfirmar = {
            preferencias.notificacionesPreguntadas = true
            mostrar = false
            permiso.solicitar()
        },
        onCancelar = {
            preferencias.notificacionesPreguntadas = true
            mostrar = false
        },
    )
}

private suspend fun probarAlertaSimulada(obtenerAlertas: ObtenerAlertasUseCase) {
    val alertas = (obtenerAlertas() as? AppResult.Exito)?.datos ?: return
    val alerta = alertas.firstOrNull { it.tipo == TipoAlerta.HUMEDAD_BAJA } ?: return
    mostrarAlertaSensor(
        lote = "${alerta.tipo.etiqueta} · ${alerta.zona.nombre}",
        mensaje = "Humedad del sustrato en ${formatearNumero(alerta.valor, 1)} % (mínimo ideal ${formatearNumero(alerta.rangoMin)} %). " +
            "Afecta: ${alerta.especiesAfectadas.joinToString(", ")}.",
    )
}
