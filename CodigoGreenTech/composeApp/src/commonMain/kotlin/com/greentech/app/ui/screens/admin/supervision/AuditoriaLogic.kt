package com.greentech.app.ui.screens.admin.supervision

import androidx.compose.runtime.Immutable
import com.greentech.app.navigation.AppDestination
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.core.UiEffect
import com.greentech.app.ui.core.aEstadoCarga
import com.greentech.shared.domain.model.Auditoria
import com.greentech.shared.domain.model.TipoAuditoria
import com.greentech.shared.domain.usecase.ObtenerAuditoriaUseCase

@Immutable
data class AuditoriaUiState(
    val registros: EstadoCarga<List<Auditoria>> = EstadoCarga.Cargando,
    val tipo: TipoAuditoria? = null,
) {
    // Se calcula una sola vez por estado (los estados son inmutables), no en cada recomposición.
    val filtrados: List<Auditoria> by lazy(LazyThreadSafetyMode.NONE) {
        registros.datosOrNull.orEmpty().filter { tipo == null || it.tipo == tipo }
    }
}

sealed interface AuditoriaEvent {
    data object Reintentar : AuditoriaEvent
    data class ElegirTipo(val tipo: TipoAuditoria?) : AuditoriaEvent
    data object Volver : AuditoriaEvent
    data object Ayuda : AuditoriaEvent
}

class AuditoriaLogic(
    private val obtenerAuditoria: ObtenerAuditoriaUseCase,
) : BaseLogic<AuditoriaUiState, AuditoriaEvent>(AuditoriaUiState()) {

    init { cargar() }

    override fun onEvent(evento: AuditoriaEvent) {
        when (evento) {
            AuditoriaEvent.Reintentar -> cargar()
            is AuditoriaEvent.ElegirTipo -> actualizar { copy(tipo = evento.tipo) }
            AuditoriaEvent.Volver -> emitir(UiEffect.Volver)
            AuditoriaEvent.Ayuda -> emitir(UiEffect.Navegar(AppDestination.Ayuda))
        }
    }

    private fun cargar() = lanzar {
        actualizar { copy(registros = EstadoCarga.Cargando) }
        val r = obtenerAuditoria().aEstadoCarga()
        actualizar { copy(registros = r) }
    }
}
