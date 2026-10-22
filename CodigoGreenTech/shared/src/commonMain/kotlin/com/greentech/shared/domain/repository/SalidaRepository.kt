package com.greentech.shared.domain.repository

import com.greentech.shared.domain.model.MotivoMerma
import com.greentech.shared.domain.model.Salida
import com.greentech.shared.domain.model.TipoSalida
import com.greentech.shared.domain.result.AppResult
import kotlinx.datetime.LocalDate

interface SalidaRepository {
    suspend fun registrarSalida(
        loteId: String,
        tipo: TipoSalida,
        cantidad: Int,
        motivo: MotivoMerma?,
        fecha: LocalDate,
        responsable: String,
    ): AppResult<Salida>
}
