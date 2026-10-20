package com.greentech.app.ui.core

import androidx.compose.runtime.Immutable
import com.greentech.shared.domain.result.AppResult

/** Estado de una consulta: cargando, lista con datos o con error. */
@Immutable
sealed interface EstadoCarga<out T> {
    data object Cargando : EstadoCarga<Nothing>
    data class Listo<out T>(val datos: T) : EstadoCarga<T>
    data class Error(val mensaje: String) : EstadoCarga<Nothing>

    val datosOrNull: T? get() = (this as? Listo)?.datos
}

fun <T> AppResult<T>.aEstadoCarga(): EstadoCarga<T> = when (this) {
    is AppResult.Exito -> EstadoCarga.Listo(datos)
    is AppResult.Fallo -> EstadoCarga.Error(error.mensaje)
}
