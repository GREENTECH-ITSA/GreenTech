package com.greentech.shared.data.remote.source

import com.greentech.shared.data.remote.dto.LoteDto

/**
 * TODO(Supabase): consultas a PostgREST para lotes.
 * - obtenerLotes(): select * from lotes join especies, zonas
 * - buscarPorCodigo(codigo): select … where codigo = :codigo
 * - insertarLote(dto): insert … returning *
 */
interface LoteRemoteSource {
    suspend fun obtenerLotes(): List<LoteDto>
    suspend fun buscarPorCodigo(codigo: String): LoteDto?
    suspend fun insertarLote(dto: LoteDto): LoteDto
}
