package com.greentech.app.ui.screens.employee.monitoreo

import androidx.compose.runtime.Immutable
import com.greentech.app.navigation.AppDestination
import com.greentech.app.ui.core.BaseLogic
import com.greentech.shared.domain.model.Lote
import com.greentech.shared.domain.result.datosOrNull
import com.greentech.shared.domain.usecase.ObtenerLoteUseCase
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.core.UiEffect
import com.greentech.shared.domain.model.CondicionesLote
import com.greentech.shared.domain.result.AppError
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.domain.usecase.ObtenerCondicionesUseCase

/** null en `condiciones` significa "sin lecturas": se muestra el estado vacío. */
@Immutable
data class CondicionesUiState(
    val condiciones: EstadoCarga<CondicionesLote?> = EstadoCarga.Cargando,
    /** Para el subtítulo de contexto, aunque todavía no haya lecturas. */
    val lote: Lote? = null,
)

sealed interface CondicionesEvent {
    data object Reintentar : CondicionesEvent
    data object Volver : CondicionesEvent
    data object Ayuda : CondicionesEvent
}

class CondicionesLogic(
    private val loteId: String,
    private val obtenerCondiciones: ObtenerCondicionesUseCase,
    private val obtenerLote: ObtenerLoteUseCase,
) : BaseLogic<CondicionesUiState, CondicionesEvent>(CondicionesUiState()) {

    init { cargar() }

    override fun onEvent(evento: CondicionesEvent) {
        when (evento) {
            CondicionesEvent.Reintentar -> cargar()
            CondicionesEvent.Volver -> emitir(UiEffect.Volver)
            CondicionesEvent.Ayuda -> emitir(UiEffect.Navegar(AppDestination.Ayuda))
        }
    }

    private fun cargar() = lanzar {
        actualizar { copy(condiciones = EstadoCarga.Cargando) }
        val r = when (val res = obtenerCondiciones(loteId)) {
            is AppResult.Exito -> EstadoCarga.Listo(res.datos.takeIf { it.lecturas.isNotEmpty() })
            is AppResult.Fallo ->
                if (res.error == AppError.SinInformacion) EstadoCarga.Listo(null) else EstadoCarga.Error(res.error.mensaje)
        }
        val lote = obtenerLote(loteId).datosOrNull()
        actualizar { copy(condiciones = r, lote = lote) }
    }
}
