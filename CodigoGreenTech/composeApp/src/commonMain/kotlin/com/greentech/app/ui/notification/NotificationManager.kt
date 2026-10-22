package com.greentech.app.ui.notification

import com.greentech.app.ui.strings.Textos

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

enum class TipoNotificacion { EXITO, ERROR, ADVERTENCIA, INFO }

data class Notificacion(
    val id: Long,
    val tipo: TipoNotificacion,
    val titulo: String,
    val mensaje: String,
)

/**
 * Cola global de notificaciones. App.kt la muestra con GtNotificationHost:
 * se presentan una por una (nunca se enciman) y se cierran solas.
 */
class NotificationManager {
    private val _notificaciones = MutableSharedFlow<Notificacion>(extraBufferCapacity = 32)
    val notificaciones: SharedFlow<Notificacion> = _notificaciones.asSharedFlow()

    private var siguienteId = 0L

    fun mostrar(tipo: TipoNotificacion, mensaje: String, titulo: String? = null) {
        _notificaciones.tryEmit(Notificacion(++siguienteId, tipo, titulo ?: tituloPorDefecto(tipo), mensaje))
    }

    fun exito(mensaje: String) = mostrar(TipoNotificacion.EXITO, mensaje)
    fun error(mensaje: String) = mostrar(TipoNotificacion.ERROR, mensaje)
    fun advertencia(mensaje: String) = mostrar(TipoNotificacion.ADVERTENCIA, mensaje)
    fun info(mensaje: String) = mostrar(TipoNotificacion.INFO, mensaje)

    private fun tituloPorDefecto(tipo: TipoNotificacion) = when (tipo) {
        TipoNotificacion.EXITO -> Textos.Notificacion.EXITO
        TipoNotificacion.ERROR -> Textos.Notificacion.ERROR
        TipoNotificacion.ADVERTENCIA -> Textos.Notificacion.ADVERTENCIA
        TipoNotificacion.INFO -> Textos.Notificacion.INFO
    }

    companion object {
        const val DURACION_MS = 3_000L
        const val DURACION_ERROR_MS = 5_000L
    }
}
