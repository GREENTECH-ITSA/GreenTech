package com.greentech.app.ui.screens.employee.monitoreo

import com.greentech.app.navigation.AppDestination
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.core.UiEffect
import com.greentech.app.ui.core.aEstadoCarga
import com.greentech.app.ui.strings.Textos
import com.greentech.shared.domain.model.EventoBitacora
import com.greentech.shared.domain.model.Lote
import com.greentech.shared.domain.model.TipoEvento
import com.greentech.shared.domain.usecase.ObtenerBitacoraUseCase
import com.greentech.shared.domain.usecase.ObtenerLoteUseCase
import com.greentech.shared.util.Fechas
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.minus

enum class PeriodoBitacora(val etiqueta: String, val dias: Int?) {
    COMPLETO(Textos.Bitacora.TODO_EL_HISTORIAL, null),
    SIETE(Textos.Bitacora.SIETE_DIAS, 7),
    TREINTA(Textos.Bitacora.TREINTA_DIAS, 30),
}

data class BitacoraUiState(
    val lote: Lote? = null,
    val eventos: EstadoCarga<List<EventoBitacora>> = EstadoCarga.Cargando,
    val periodo: PeriodoBitacora = PeriodoBitacora.COMPLETO,
    val tipo: TipoEvento? = null,
    val soloLectura: Boolean = false,
) {
    val filtrosActivos: Int get() = listOf(periodo != PeriodoBitacora.COMPLETO, tipo != null).count { it }

    val filtrados: List<EventoBitacora>
        get() {
            val desde = periodo.dias?.let { LocalDateTime(Fechas.hoy().minus(it, DateTimeUnit.DAY), LocalTime(0, 0)) }
            return eventos.datosOrNull.orEmpty().filter { e ->
                (desde == null || e.fecha >= desde) && (tipo == null || e.tipo == tipo)
            }
        }
}

sealed interface BitacoraEvent {
    data object Reintentar : BitacoraEvent
    data class ElegirPeriodo(val periodo: PeriodoBitacora) : BitacoraEvent
    data class ElegirTipo(val tipo: TipoEvento?) : BitacoraEvent
    data object LimpiarFiltros : BitacoraEvent
    data object Volver : BitacoraEvent
    data object Ayuda : BitacoraEvent
}

class BitacoraLogic(
    private val loteId: String,
    soloLectura: Boolean,
    private val obtenerLote: ObtenerLoteUseCase,
    private val obtenerBitacora: ObtenerBitacoraUseCase,
) : BaseLogic<BitacoraUiState, BitacoraEvent>(BitacoraUiState(soloLectura = soloLectura)) {

    init { cargar() }

    override fun onEvent(evento: BitacoraEvent) {
        when (evento) {
            BitacoraEvent.Reintentar -> cargar()
            is BitacoraEvent.ElegirPeriodo -> actualizar { copy(periodo = evento.periodo) }
            is BitacoraEvent.ElegirTipo -> actualizar { copy(tipo = evento.tipo) }
            BitacoraEvent.LimpiarFiltros -> actualizar { copy(periodo = PeriodoBitacora.COMPLETO, tipo = null) }
            BitacoraEvent.Volver -> emitir(UiEffect.Volver)
            BitacoraEvent.Ayuda -> emitir(UiEffect.Navegar(AppDestination.Ayuda))
        }
    }

    private fun cargar() = lanzar {
        actualizar { copy(eventos = EstadoCarga.Cargando) }
        coroutineScope {
            val lote = async { obtenerLote(loteId) }
            val eventos = async { obtenerBitacora(loteId) }
            val nuevoLote = lote.await().aEstadoCarga().datosOrNull
            val nuevosEventos = eventos.await().aEstadoCarga()
            actualizar { copy(lote = nuevoLote, eventos = nuevosEventos) }
        }
    }
}
