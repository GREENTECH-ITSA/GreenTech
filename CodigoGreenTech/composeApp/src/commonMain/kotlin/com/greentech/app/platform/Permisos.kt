package com.greentech.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

/** Permisos que la app pide en tiempo de ejecución, solo cuando hacen falta. */
enum class Permiso {
    CAMARA,
    NOTIFICACIONES,

    /** Guardar imágenes en la galería: solo existe en Android 8–9 (en 10+ siempre está concedido). */
    GUARDAR_IMAGEN,
}

/**
 * Estado observable de un permiso. Se vuelve a revisar al regresar a la app (ON_RESUME),
 * por ejemplo después de cambiarlo en Ajustes.
 */
@Stable
interface EstadoPermiso {
    val concedido: Boolean

    /** Se negó y el sistema ya no mostrará el diálogo: solo queda abrir Ajustes. */
    val negadoPermanente: Boolean

    /** Muestra el diálogo del sistema. */
    fun solicitar()

    /** Abre los ajustes de la app (Settings.ACTION_APPLICATION_DETAILS_SETTINGS). */
    fun abrirAjustes()
}

/** Estado fijo para previews y equipos donde el permiso no aplica. */
class PermisoFijo(override val concedido: Boolean, override val negadoPermanente: Boolean = false) : EstadoPermiso {
    override fun solicitar() = Unit
    override fun abrirAjustes() = Unit
}

/** [alResponder] recibe la respuesta del diálogo del sistema (true = permitido). */
@Composable
expect fun rememberEstadoPermiso(permiso: Permiso, alResponder: (Boolean) -> Unit = {}): EstadoPermiso
