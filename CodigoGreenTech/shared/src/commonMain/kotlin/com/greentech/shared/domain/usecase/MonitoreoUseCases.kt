package com.greentech.shared.domain.usecase

import com.greentech.shared.domain.model.EstadoSalud
import com.greentech.shared.domain.model.Severidad
import com.greentech.shared.domain.model.TipoActividad
import com.greentech.shared.domain.model.TipoAnomalia
import com.greentech.shared.domain.repository.MonitoreoRepository
import com.greentech.shared.domain.result.AppError
import com.greentech.shared.domain.result.AppResult

class RegistrarObservacionUseCase(private val repo: MonitoreoRepository) {
    suspend operator fun invoke(loteId: String, texto: String, estado: EstadoSalud, conFoto: Boolean, autor: String) =
        if (texto.isBlank()) AppResult.Fallo(AppError.CamposObligatorios)
        else repo.registrarObservacion(loteId, texto.trim(), estado, conFoto, autor)
}

class RegistrarActividadUseCase(private val repo: MonitoreoRepository) {
    suspend operator fun invoke(loteId: String, tipo: TipoActividad?, observacion: String, responsable: String) =
        if (tipo == null) AppResult.Fallo(AppError.CamposObligatorios)
        else repo.registrarActividad(loteId, tipo, observacion.trim(), responsable)
}

class RegistrarAnomaliaUseCase(private val repo: MonitoreoRepository) {
    suspend operator fun invoke(
        loteId: String,
        tipo: TipoAnomalia?,
        nombre: String?,
        severidad: Severidad?,
        descripcion: String,
        autor: String,
    ) = if (tipo == null || nombre.isNullOrBlank() || severidad == null || descripcion.isBlank()) {
        AppResult.Fallo(AppError.CamposObligatorios)
    } else {
        repo.registrarAnomalia(loteId, tipo, nombre, severidad, descripcion.trim(), autor)
    }
}

class ObtenerCondicionesUseCase(private val repo: MonitoreoRepository) {
    suspend operator fun invoke(loteId: String) = repo.obtenerCondiciones(loteId)
}

class ObtenerAlertasUseCase(private val repo: MonitoreoRepository) {
    suspend operator fun invoke() = repo.obtenerAlertas()
}

class ObtenerRecomendacionesUseCase(private val repo: MonitoreoRepository) {
    suspend operator fun invoke(loteId: String) = repo.obtenerRecomendaciones(loteId)
}

class ObtenerBitacoraUseCase(private val repo: MonitoreoRepository) {
    suspend operator fun invoke(loteId: String) = repo.obtenerBitacora(loteId)
}

class ObtenerActividadRecienteUseCase(private val repo: MonitoreoRepository) {
    suspend operator fun invoke(limite: Int = 5) = repo.obtenerActividadReciente(limite)
}
