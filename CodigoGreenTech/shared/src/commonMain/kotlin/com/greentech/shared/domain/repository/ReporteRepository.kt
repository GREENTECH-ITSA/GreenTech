package com.greentech.shared.domain.repository

import com.greentech.shared.domain.model.Auditoria
import com.greentech.shared.domain.model.Dashboard
import com.greentech.shared.domain.model.Periodo
import com.greentech.shared.domain.result.AppResult

interface ReporteRepository {
    suspend fun obtenerDashboard(periodo: Periodo): AppResult<Dashboard>

    suspend fun obtenerAuditoria(): AppResult<List<Auditoria>>
}
