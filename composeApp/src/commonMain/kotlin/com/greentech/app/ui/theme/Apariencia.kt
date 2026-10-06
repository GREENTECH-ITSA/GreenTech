package com.greentech.app.ui.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.russhwolf.settings.Settings

/** Tema elegido en Perfil → Apariencia. Por defecto Claro: la app se usa al sol. */
enum class ThemeMode { SISTEMA, CLARO, OSCURO }

/**
 * Preferencias visuales del dispositivo (tema y "Reducir animaciones").
 * Persisten entre aperturas con multiplatform-settings (SharedPreferences / NSUserDefaults).
 * Son solo de presentación: no pasan por los Logic ni por el dominio.
 */
@Stable
class PreferenciasApariencia(private val settings: Settings? = null) {

    var modo: ThemeMode by mutableStateOf(
        settings?.getStringOrNull(CLAVE_MODO)?.let { v -> ThemeMode.entries.firstOrNull { it.name == v } } ?: ThemeMode.CLARO,
    )
        private set

    var reducirAnimaciones: Boolean by mutableStateOf(settings?.getBoolean(CLAVE_REDUCIR, false) ?: false)
        private set

    init {
        Motion.reducir = reducirAnimaciones
    }

    fun cambiarModo(nuevo: ThemeMode) {
        modo = nuevo
        settings?.putString(CLAVE_MODO, nuevo.name)
    }

    fun cambiarReducirAnimaciones(activo: Boolean) {
        reducirAnimaciones = activo
        Motion.reducir = activo
        settings?.putBoolean(CLAVE_REDUCIR, activo)
    }

    private companion object {
        const val CLAVE_MODO = "apariencia.modo"
        const val CLAVE_REDUCIR = "apariencia.reducir_animaciones"
    }
}

/** Preferencias de apariencia de la app. En previews vale un valor en memoria (sin persistir). */
val LocalApariencia = staticCompositionLocalOf { PreferenciasApariencia() }
