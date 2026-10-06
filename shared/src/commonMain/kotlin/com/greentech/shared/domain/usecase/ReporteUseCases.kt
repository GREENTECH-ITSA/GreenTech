package com.greentech.shared.domain.usecase

import com.greentech.shared.domain.model.Periodo
import com.greentech.shared.domain.repository.ReporteRepository
import com.greentech.shared.domain.repository.UsuarioRepository

class ObtenerTrabajadoresUseCase(private val repo: UsuarioRepository) {
    suspend operator fun invoke() = repo.obtenerTrabajadores()
}

class ObtenerDashboardUseCase(private val repo: ReporteRepository) {
    suspend operator fun invoke(periodo: Periodo) = repo.obtenerDashboard(periodo)
}

class ObtenerAuditoriaUseCase(private val repo: ReporteRepository) {
    suspend operator fun invoke() = repo.obtenerAuditoria()
}
