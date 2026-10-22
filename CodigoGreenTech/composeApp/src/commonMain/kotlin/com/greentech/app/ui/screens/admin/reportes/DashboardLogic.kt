package com.greentech.app.ui.screens.admin.reportes

import androidx.compose.runtime.Immutable
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.core.aEstadoCarga
import com.greentech.shared.domain.model.Dashboard
import com.greentech.shared.domain.model.Periodo
import com.greentech.shared.domain.usecase.ObtenerDashboardUseCase

@Immutable
data class DashboardUiState(
    val periodo: Periodo = Periodo.MES,
    val dashboard: EstadoCarga<Dashboard> = EstadoCarga.Cargando,
)

sealed interface DashboardEvent {
    data object Entrar : DashboardEvent
    data class CambiarPeriodo(val periodo: Periodo) : DashboardEvent
}

class DashboardLogic(
    private val obtenerDashboard: ObtenerDashboardUseCase,
) : BaseLogic<DashboardUiState, DashboardEvent>(DashboardUiState()) {

    override fun onEvent(evento: DashboardEvent) {
        when (evento) {
            DashboardEvent.Entrar -> cargar(silencioso = estado.dashboard is EstadoCarga.Listo)
            is DashboardEvent.CambiarPeriodo -> if (evento.periodo != estado.periodo) {
                actualizar { copy(periodo = evento.periodo) }
                cargar(silencioso = false)
            }
        }
    }

    private fun cargar(silencioso: Boolean) = lanzar {
        if (!silencioso) actualizar { copy(dashboard = EstadoCarga.Cargando) }
        val r = obtenerDashboard(estado.periodo).aEstadoCarga()
        actualizar { copy(dashboard = r) }
    }
}
