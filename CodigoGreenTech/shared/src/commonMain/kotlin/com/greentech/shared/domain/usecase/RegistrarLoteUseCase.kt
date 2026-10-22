package com.greentech.shared.domain.usecase

import com.greentech.shared.domain.model.Lote
import com.greentech.shared.domain.repository.LoteRepository
import com.greentech.shared.domain.result.AppError
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.domain.validation.LoteValidator
import kotlinx.datetime.LocalDate

class RegistrarLoteUseCase(private val repo: LoteRepository) {
    suspend operator fun invoke(
        especieId: String?,
        zonaId: String?,
        cantidad: Int?,
        fechaSiembra: LocalDate,
        responsable: String,
    ): AppResult<Lote> {
        val invalido = LoteValidator.validarEspecie(especieId) != null ||
            LoteValidator.validarZona(zonaId) != null ||
            LoteValidator.validarCantidad(cantidad) != null
        if (invalido) return AppResult.Fallo(AppError.CamposObligatorios)
        return repo.registrarLote(especieId!!, zonaId!!, cantidad!!, fechaSiembra, responsable)
    }
}
