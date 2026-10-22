package com.greentech.shared.domain.usecase

import com.greentech.shared.domain.model.EstadoSalud
import com.greentech.shared.domain.model.Etapa
import com.greentech.shared.domain.repository.LoteRepository
import com.greentech.shared.domain.result.AppError
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.domain.validation.LoteValidator

class ObtenerLotesUseCase(private val repo: LoteRepository) {
    suspend operator fun invoke() = repo.obtenerLotes()
}

class ObtenerLoteUseCase(private val repo: LoteRepository) {
    suspend operator fun invoke(id: String) = repo.obtenerLote(id)
}

class BuscarLotePorCodigoUseCase(private val repo: LoteRepository) {
    suspend operator fun invoke(codigo: String) =
        if (!LoteValidator.codigoValido(codigo)) AppResult.Fallo(AppError.LoteNoEncontrado)
        else repo.buscarPorCodigo(LoteValidator.normalizarCodigo(codigo))
}

class ObtenerCatalogosLoteUseCase(private val repo: LoteRepository) {
    suspend fun especies() = repo.obtenerEspecies()
    suspend fun zonas() = repo.obtenerZonas()
}

class ActualizarEtapaUseCase(private val repo: LoteRepository) {
    suspend operator fun invoke(
        loteId: String,
        etapa: Etapa?,
        estadoSalud: EstadoSalud,
        nota: String,
        autor: String,
    ) = if (etapa == null) AppResult.Fallo(AppError.CamposObligatorios)
    else repo.actualizarEtapa(loteId, etapa, estadoSalud, nota.trim(), autor)
}
