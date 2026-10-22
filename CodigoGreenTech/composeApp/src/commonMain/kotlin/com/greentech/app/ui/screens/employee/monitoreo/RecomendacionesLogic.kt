package com.greentech.app.ui.screens.employee.monitoreo

import androidx.compose.runtime.Immutable
import com.greentech.app.navigation.AppDestination
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.core.UiEffect
import com.greentech.app.ui.core.aEstadoCarga
import com.greentech.shared.domain.model.Lote
import com.greentech.shared.domain.result.datosOrNull
import com.greentech.shared.domain.usecase.ObtenerLoteUseCase
import com.greentech.shared.domain.model.Recomendacion
import com.greentech.shared.domain.usecase.ObtenerRecomendacionesUseCase

@Immutable
data class RecomendacionesUiState(
    val recomendaciones: EstadoCarga<List<Recomendacion>> = EstadoCarga.Cargando,
    val lote: Lote? = null,
)

sealed interface RecomendacionesEvent {
    data object Reintentar : RecomendacionesEvent
    data object Volver : RecomendacionesEvent
    data object Ayuda : RecomendacionesEvent
}

class RecomendacionesLogic(
    private val loteId: String,
    private val obtener: ObtenerRecomendacionesUseCase,
    private val obtenerLote: ObtenerLoteUseCase,
) : BaseLogic<RecomendacionesUiState, RecomendacionesEvent>(RecomendacionesUiState()) {

    init { cargar() }

    override fun onEvent(evento: RecomendacionesEvent) {
        when (evento) {
            RecomendacionesEvent.Reintentar -> cargar()
            RecomendacionesEvent.Volver -> emitir(UiEffect.Volver)
            RecomendacionesEvent.Ayuda -> emitir(UiEffect.Navegar(AppDestination.Ayuda))
        }
    }

    private fun cargar() = lanzar {
        actualizar { copy(recomendaciones = EstadoCarga.Cargando) }
        val r = obtener(loteId).aEstadoCarga()
        val lote = obtenerLote(loteId).datosOrNull()
        actualizar { copy(recomendaciones = r, lote = lote) }
    }
}
