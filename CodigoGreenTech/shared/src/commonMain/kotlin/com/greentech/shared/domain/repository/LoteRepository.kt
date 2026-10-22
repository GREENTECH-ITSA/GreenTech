package com.greentech.shared.domain.repository

import com.greentech.shared.domain.model.Especie
import com.greentech.shared.domain.model.EstadoSalud
import com.greentech.shared.domain.model.Etapa
import com.greentech.shared.domain.model.Lote
import com.greentech.shared.domain.model.Zona
import com.greentech.shared.domain.result.AppResult
import kotlinx.datetime.LocalDate

interface LoteRepository {
    suspend fun obtenerLotes(): AppResult<List<Lote>>

    suspend fun obtenerLote(id: String): AppResult<Lote>

    suspend fun buscarPorCodigo(codigo: String): AppResult<Lote>

    suspend fun obtenerEspecies(): AppResult<List<Especie>>

    suspend fun obtenerZonas(): AppResult<List<Zona>>

    suspend fun registrarLote(
        especieId: String,
        zonaId: String,
        cantidad: Int,
        fechaSiembra: LocalDate,
        responsable: String,
    ): AppResult<Lote>

    suspend fun actualizarEtapa(
        loteId: String,
        etapa: Etapa,
        estadoSalud: EstadoSalud,
        nota: String,
        autor: String,
    ): AppResult<Lote>
}
