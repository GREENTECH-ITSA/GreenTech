package com.greentech.app.ui.screens.employee.monitoreo

import com.greentech.app.navigation.AppDestination
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.core.UiEffect
import com.greentech.app.ui.core.aEstadoCarga
import com.greentech.shared.domain.model.CondicionesLote
import com.greentech.shared.domain.model.Lote
import com.greentech.shared.domain.result.datosOrNull
import com.greentech.shared.domain.usecase.ObtenerCondicionesUseCase
import com.greentech.shared.domain.usecase.ObtenerLoteUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.coroutineScope

/** Acciones de registro disponibles desde el detalle del lote. */
enum class AccionLote { ETAPA, OBSERVACION, ACTIVIDAD, CONDICIONES, ANOMALIA, RECOMENDACIONES, BITACORA, SALIDA }

data class DetalleLoteUiState(
    val lote: EstadoCarga<Lote> = EstadoCarga.Cargando,
    val condiciones: CondicionesLote? = null,
    val soloLectura: Boolean = false,
    /** Lo que se acaba de registrar: se resalta un momento al volver al detalle. */
    val resaltado: AccionLote? = null,
)

sealed interface DetalleLoteEvent {
    data object Entrar : DetalleLoteEvent
    data class Accion(val accion: AccionLote) : DetalleLoteEvent
    data object VerQr : DetalleLoteEvent
    data object Volver : DetalleLoteEvent
    data object Ayuda : DetalleLoteEvent
}

class DetalleLoteLogic(
    private val loteId: String,
    soloLectura: Boolean,
    private val obtenerLote: ObtenerLoteUseCase,
    private val obtenerCondiciones: ObtenerCondicionesUseCase,
    private val recientes: RegistrosRecientes,
) : BaseLogic<DetalleLoteUiState, DetalleLoteEvent>(DetalleLoteUiState(soloLectura = soloLectura)) {

    override fun onEvent(evento: DetalleLoteEvent) {
        when (evento) {
            DetalleLoteEvent.Entrar -> {
                cargar()
                recientes.tomar(loteId)?.let(::resaltar)
            }
            is DetalleLoteEvent.Accion -> emitir(UiEffect.Navegar(destino(evento.accion)))
            DetalleLoteEvent.VerQr -> emitir(UiEffect.Navegar(AppDestination.QrLote(loteId)))
            DetalleLoteEvent.Volver -> emitir(UiEffect.Volver)
            DetalleLoteEvent.Ayuda -> emitir(UiEffect.Navegar(AppDestination.Ayuda))
        }
    }

    private fun destino(accion: AccionLote): AppDestination {
        // En modo solo lectura (administrador) únicamente se permiten consultas.
        if (estado.soloLectura && accion !in setOf(AccionLote.BITACORA, AccionLote.CONDICIONES)) {
            return AppDestination.Bitacora(loteId, soloLectura = true)
        }
        return when (accion) {
            AccionLote.ETAPA -> AppDestination.CambiarEtapa(loteId)
            AccionLote.OBSERVACION -> AppDestination.NuevaObservacion(loteId)
            AccionLote.ACTIVIDAD -> AppDestination.RegistrarActividad(loteId)
            AccionLote.CONDICIONES -> AppDestination.Condiciones(loteId)
            AccionLote.ANOMALIA -> AppDestination.ReportarAnomalia(loteId)
            AccionLote.RECOMENDACIONES -> AppDestination.Recomendaciones(loteId)
            AccionLote.BITACORA -> AppDestination.Bitacora(loteId, estado.soloLectura)
            AccionLote.SALIDA -> AppDestination.RegistrarSalida(loteId)
        }
    }

    private fun resaltar(accion: AccionLote) = lanzar {
        actualizar { copy(resaltado = accion) }
        delay(DURACION_RESALTADO_MS)
        actualizar { copy(resaltado = null) }
    }

    private fun cargar() = lanzar {
        if (estado.lote !is EstadoCarga.Listo) actualizar { copy(lote = EstadoCarga.Cargando) }
        coroutineScope {
            val lote = async { obtenerLote(loteId) }
            val cond = async { obtenerCondiciones(loteId) }
            val nuevoLote = lote.await().aEstadoCarga()
            val nuevasCondiciones = cond.await().datosOrNull()
            actualizar { copy(lote = nuevoLote, condiciones = nuevasCondiciones) }
        }
    }

    private companion object {
        const val DURACION_RESALTADO_MS = 3_500L
    }
}
