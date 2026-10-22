package com.greentech.shared.domain.usecase

import com.greentech.shared.domain.model.MotivoMerma
import com.greentech.shared.domain.model.Salida
import com.greentech.shared.domain.model.TipoSalida
import com.greentech.shared.domain.repository.LoteRepository
import com.greentech.shared.domain.repository.SalidaRepository
import com.greentech.shared.domain.result.AppError
import com.greentech.shared.domain.result.AppResult
import kotlinx.datetime.LocalDate

class RegistrarSalidaUseCase(
    private val salidas: SalidaRepository,
    private val lotes: LoteRepository,
) {
    suspend operator fun invoke(
        loteId: String,
        tipo: TipoSalida?,
        cantidad: Int?,
        motivo: MotivoMerma?,
        fecha: LocalDate,
        responsable: String,
    ): AppResult<Salida> {
        if (tipo == null || cantidad == null || cantidad <= 0 || (tipo == TipoSalida.MERMA && motivo == null)) {
            return AppResult.Fallo(AppError.CamposObligatorios)
        }
        val lote = when (val r = lotes.obtenerLote(loteId)) {
            is AppResult.Exito -> r.datos
            is AppResult.Fallo -> return r
        }
        if (cantidad > lote.cantidadDisponible) {
            return AppResult.Fallo(AppError.CantidadExcedida(lote.cantidadDisponible))
        }
        return salidas.registrarSalida(loteId, tipo, cantidad, if (tipo == TipoSalida.MERMA) motivo else null, fecha, responsable)
    }
}
