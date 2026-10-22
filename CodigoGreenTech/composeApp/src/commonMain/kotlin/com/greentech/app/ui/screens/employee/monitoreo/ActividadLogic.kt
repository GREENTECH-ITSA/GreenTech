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
import com.greentech.shared.domain.model.Lote
import com.greentech.shared.domain.model.TipoActividad
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.domain.result.Mensajes
import com.greentech.shared.domain.usecase.ObtenerLoteUseCase
import com.greentech.shared.domain.usecase.ObtenerSesionUseCase
import com.greentech.shared.domain.usecase.RegistrarActividadUseCase
import com.greentech.shared.util.Fechas

@Immutable
data class ActividadUiState(
    val lote: EstadoCarga<Lote> = EstadoCarga.Cargando,
    val tipo: TipoActividad? = null,
    val observacion: String = "",
    val responsable: String = "",
    val fechaHora: String = "",
    val cargando: Boolean = false,
) {
    val hayCambios: Boolean get() = tipo != null || observacion.isNotBlank()
    val faltantes: List<String> get() = if (tipo == null) listOf(Textos.Actividad.FALTA_TIPO) else emptyList()
    val puedeGuardar: Boolean get() = !cargando && lote is EstadoCarga.Listo && faltantes.isEmpty()
}

sealed interface ActividadEvent {
    data class ElegirTipo(val tipo: TipoActividad) : ActividadEvent
    data class ObservacionCambio(val valor: String) : ActividadEvent
    data object Guardar : ActividadEvent
    data object Volver : ActividadEvent
    data object Ayuda : ActividadEvent
}

/** Lo escrito en el formulario (sobrevive a la muerte del proceso). */
@Serializable
private data class BorradorActividad(val tipo: TipoActividad?, val observacion: String)

class ActividadLogic(
    private val loteId: String,
    private val obtenerLote: ObtenerLoteUseCase,
    private val registrar: RegistrarActividadUseCase,
    sesion: ObtenerSesionUseCase,
    private val recientes: RegistrosRecientes,
    handle: SavedStateHandle,
) : BaseLogic<ActividadUiState, ActividadEvent>(
    // Responsable y fecha se llenan solos: el empleado no escribe nada de esto.
    ActividadUiState(responsable = sesion.actual()?.nombre.orEmpty(), fechaHora = "${Textos.Comun.HOY}, ${Fechas.hora(Fechas.ahora())}"),
) {
    init {
        conservarBorrador(handle, BorradorActividad.serializer(), { BorradorActividad(tipo, observacion) }) {
            copy(tipo = it.tipo, observacion = it.observacion)
        }
        lanzar {
            val r = obtenerLote(loteId).aEstadoCarga()
            actualizar { copy(lote = r) }
        }
    }

    override fun onEvent(evento: ActividadEvent) {
        when (evento) {
            is ActividadEvent.ElegirTipo -> actualizar { copy(tipo = evento.tipo) }
            is ActividadEvent.ObservacionCambio -> actualizar { copy(observacion = evento.valor) }
            ActividadEvent.Volver -> emitir(UiEffect.Volver)
            ActividadEvent.Ayuda -> emitir(UiEffect.Navegar(AppDestination.Ayuda))
            ActividadEvent.Guardar -> guardar()
        }
    }

    private fun guardar() {
        if (!estado.puedeGuardar) return
        actualizar { copy(cargando = true) }
        lanzar {
            when (val r = registrar(loteId, estado.tipo, estado.observacion, estado.responsable)) {
                is AppResult.Exito -> {
                    recientes.marcar(loteId, AccionLote.ACTIVIDAD)
                    notificarExito(Mensajes.ACTIVIDAD_REGISTRADA)
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
