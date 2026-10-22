package com.greentech.app.ui.screens.employee.monitoreo

import kotlinx.serialization.Serializable
import androidx.lifecycle.SavedStateHandle
import androidx.compose.runtime.Immutable
import com.greentech.app.navigation.AppDestination
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.core.UiEffect
import com.greentech.app.ui.core.aEstadoCarga
import com.greentech.app.ui.strings.Textos
import com.greentech.shared.domain.model.EstadoSalud
import com.greentech.shared.domain.model.Etapa
import com.greentech.shared.domain.model.Lote
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.domain.result.Mensajes
import com.greentech.shared.domain.usecase.ActualizarEtapaUseCase
import com.greentech.shared.domain.usecase.ObtenerLoteUseCase
import com.greentech.shared.domain.usecase.ObtenerSesionUseCase

@Immutable
data class EtapaUiState(
    val lote: EstadoCarga<Lote> = EstadoCarga.Cargando,
    val etapa: Etapa? = null,
    val salud: EstadoSalud = EstadoSalud.SANA,
    val saludInicial: EstadoSalud = EstadoSalud.SANA,
    val nota: String = "",
    val cargando: Boolean = false,
) {
    val hayCambios: Boolean get() = etapa != null || nota.isNotBlank() || salud != saludInicial
    val etapaActual: Etapa? get() = lote.datosOrNull?.etapa
    val faltantes: List<String> get() = if (etapa == null) listOf(Textos.Etapa.FALTA_ETAPA) else emptyList()
    val puedeGuardar: Boolean get() = !cargando && lote is EstadoCarga.Listo && faltantes.isEmpty()
}

sealed interface EtapaEvent {
    data class ElegirEtapa(val etapa: Etapa) : EtapaEvent
    data class ElegirSalud(val salud: EstadoSalud) : EtapaEvent
    data class NotaCambio(val valor: String) : EtapaEvent
    data object Guardar : EtapaEvent
    data object Volver : EtapaEvent
    data object Ayuda : EtapaEvent
}

/** Lo escrito en el formulario (sobrevive a la muerte del proceso). */
@Serializable
private data class BorradorEtapa(val etapa: Etapa?, val salud: EstadoSalud, val nota: String)

class EtapaLogic(
    private val loteId: String,
    private val obtenerLote: ObtenerLoteUseCase,
    private val actualizarEtapa: ActualizarEtapaUseCase,
    private val sesion: ObtenerSesionUseCase,
    private val recientes: RegistrosRecientes,
    handle: SavedStateHandle,
) : BaseLogic<EtapaUiState, EtapaEvent>(EtapaUiState()) {

    init {
        val borrador = conservarBorrador(handle, BorradorEtapa.serializer(), { BorradorEtapa(etapa, salud, nota) }) {
            copy(etapa = it.etapa, salud = it.salud, nota = it.nota)
        }
        lanzar {
            val r = obtenerLote(loteId)
            val saludActual = (r as? AppResult.Exito)?.datos?.estadoSalud ?: estado.salud
            // La salud elegida antes de recrear la pantalla gana sobre la del lote.
            actualizar { copy(lote = r.aEstadoCarga(), salud = borrador?.salud ?: saludActual, saludInicial = saludActual) }
        }
    }

    override fun onEvent(evento: EtapaEvent) {
        when (evento) {
            is EtapaEvent.ElegirEtapa -> actualizar { copy(etapa = evento.etapa) }
            is EtapaEvent.ElegirSalud -> actualizar { copy(salud = evento.salud) }
            is EtapaEvent.NotaCambio -> actualizar { copy(nota = evento.valor) }
            EtapaEvent.Volver -> emitir(UiEffect.Volver)
            EtapaEvent.Ayuda -> emitir(UiEffect.Navegar(AppDestination.Ayuda))
            EtapaEvent.Guardar -> guardar()
        }
    }

    private fun guardar() {
        if (!estado.puedeGuardar) return
        actualizar { copy(cargando = true) }
        lanzar {
            val s = estado
            when (val r = actualizarEtapa(loteId, s.etapa, s.salud, s.nota, sesion.actual()?.nombre.orEmpty())) {
                is AppResult.Exito -> {
                    recientes.marcar(loteId, AccionLote.ETAPA)
                    notificarExito(Mensajes.ETAPA_ACTUALIZADA)
                    emitir(UiEffect.Volver)
                }
                is AppResult.Fallo -> {
                    actualizar { copy(cargando = false) }
                    notificarError(r.error)
                }
            }
        }
    }
}
