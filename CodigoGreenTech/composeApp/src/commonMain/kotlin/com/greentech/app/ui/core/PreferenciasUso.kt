package com.greentech.app.ui.core

import com.russhwolf.settings.Settings

/**
 * Preferencias de uso del dispositivo que mejoran la experiencia y persisten entre aperturas
 * (multiplatform-settings): la zona del último lote registrado (valor por defecto del formulario)
 * qué usuarios ya vieron la introducción de primer uso y si ya se explicó el permiso de notificaciones.
 */
class PreferenciasUso(private val settings: Settings) {

    var ultimaZonaId: String?
        get() = settings.getStringOrNull(CLAVE_ZONA)
        set(valor) {
            if (valor == null) settings.remove(CLAVE_ZONA) else settings.putString(CLAVE_ZONA, valor)
        }

    fun introVista(usuarioId: String): Boolean = settings.getBoolean(CLAVE_INTRO + usuarioId, false)

    fun marcarIntroVista(usuarioId: String) = settings.putBoolean(CLAVE_INTRO + usuarioId, true)

    /** Se pregunta una sola vez por equipo; después se cambia desde Perfil → Permisos. */
    var notificacionesPreguntadas: Boolean
        get() = settings.getBoolean(CLAVE_NOTIFICACIONES, false)
        set(valor) = settings.putBoolean(CLAVE_NOTIFICACIONES, valor)

    private companion object {
        const val CLAVE_ZONA = "formularios.ultima_zona"
        const val CLAVE_INTRO = "intro.vista."
        const val CLAVE_NOTIFICACIONES = "permisos.notificaciones_preguntadas"
    }
}
