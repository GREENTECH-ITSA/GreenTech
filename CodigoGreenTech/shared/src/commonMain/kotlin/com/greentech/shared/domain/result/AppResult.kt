package com.greentech.shared.domain.result

/** Resultado tipado de cualquier operación: éxito con datos o fallo con un [AppError]. */
sealed interface AppResult<out T> {
    data class Exito<out T>(val datos: T) : AppResult<T>
    data class Fallo(val error: AppError) : AppResult<Nothing>
}

inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Exito -> AppResult.Exito(transform(datos))
    is AppResult.Fallo -> this
}

inline fun <T> AppResult<T>.onExito(accion: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Exito) accion(datos)
    return this
}

inline fun <T> AppResult<T>.onFallo(accion: (AppError) -> Unit): AppResult<T> {
    if (this is AppResult.Fallo) accion(error)
    return this
}

fun <T> AppResult<T>.datosOrNull(): T? = (this as? AppResult.Exito)?.datos

/** Ejecuta un bloque y convierte cualquier excepción inesperada en [AppError.Desconocido]. */
inline fun <T> resultadoSeguro(bloque: () -> AppResult<T>): AppResult<T> = try {
    bloque()
} catch (e: kotlin.coroutines.cancellation.CancellationException) {
    throw e
} catch (e: Exception) {
    AppResult.Fallo(AppError.Desconocido)
}
