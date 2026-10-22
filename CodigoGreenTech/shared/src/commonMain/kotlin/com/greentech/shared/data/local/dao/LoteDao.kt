package com.greentech.shared.data.local.dao

/**
 * TODO(Offline): acceso a la tabla local de lotes generada por SQLDelight.
 * Guardará lotes y registros pendientes de sincronizar (columna `pendiente_sync`).
 */
interface LoteDao {
    suspend fun guardarLotes(lotes: List<Any>)
    suspend fun pendientesDeSincronizar(): Int
}
