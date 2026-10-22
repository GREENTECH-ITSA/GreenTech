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
import com.greentech.shared.domain.model.Severidad
import com.greentech.shared.domain.model.TipoAnomalia
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.domain.result.Mensajes
import com.greentech.shared.domain.usecase.ObtenerLoteUseCase
import com.greentech.shared.domain.usecase.ObtenerSesionUseCase
import com.greentech.shared.domain.usecase.RegistrarAnomaliaUseCase

@Immutable
data class AnomaliaUiState(
    val lote: EstadoCarga<Lote> = EstadoCarga.Cargando,
    val tipo: TipoAnomalia? = null,
    val nombre: String? = null,
    val severidad: Severidad? = null,
    val descripcion: String = "",
    val intentoGuardar: Boolean = false,
    val cargando: Boolean = false,
) {
    val hayCambios: Boolean get() = tipo != null || severidad != null || descripcion.isNotBlank()
    val errorDescripcion: String? get() = if (intentoGuardar && descripcion.isBlank()) Textos.Anomalia.DESCRIPCION_ERROR else null

    val faltantes: List<String>
        get() = buildList {
            if (tipo == null) add(Textos.Anomalia.FALTA_TIPO)
            if (nombre == null) add(Textos.Anomalia.FALTA_OPCION)
            if (severidad == null) add(Textos.Anomalia.FALTA_SEVERIDAD)
            if (descripcion.isBlank()) add(Textos.Anomalia.FALTA_DESCRIPCION)
        }

    val puedeGuardar: Boolean get() = !cargando && lote is EstadoCarga.Listo && faltantes.isEmpty()
}

sealed interface AnomaliaEvent {
    data class ElegirTipo(val tipo: TipoAnomalia) : AnomaliaEvent
    data class ElegirNombre(val nombre: String) : AnomaliaEvent
    data class ElegirSeveridad(val severidad: Severidad) : AnomaliaEvent
    data class DescripcionCambio(val valor: String) : AnomaliaEvent
    data object Guardar : AnomaliaEvent
    data object Volver : AnomaliaEvent
    data object Ayuda : AnomaliaEvent
}

/** Lo escrito en el formulario (sobrevive a la muerte del proceso). */
@Serializable
private data class BorradorAnomalia(val tipo: TipoAnomalia?, val nombre: String?, val severidad: Severidad?, val descripcion: String)

class AnomaliaLogic(
    private val loteId: String,
    private val obtenerLote: ObtenerLoteUseCase,
    private val registrar: RegistrarAnomaliaUseCase,
    private val sesion: ObtenerSesionUseCase,
    private val recientes: RegistrosRecientes,
    handle: SavedStateHandle,
) : BaseLogic<AnomaliaUiState, AnomaliaEvent>(AnomaliaUiState()) {

    init {
        conservarBorrador(handle, BorradorAnomalia.serializer(), { BorradorAnomalia(tipo, nombre, severidad, descripcion) }) {
            copy(tipo = it.tipo, nombre = it.nombre, severidad = it.severidad, descripcion = it.descripcion)
        }
        lanzar { val r = obtenerLote(loteId).aEstadoCarga(); actualizar { copy(lote = r) } }
    }

    override fun onEvent(evento: AnomaliaEvent) {
        when (evento) {
            // Al cambiar de tipo se limpia la opción elegida del catálogo anterior.
            is AnomaliaEvent.ElegirTipo -> actualizar { copy(tipo = evento.tipo, nombre = if (tipo == evento.tipo) nombre else null) }
            is AnomaliaEvent.ElegirNombre -> actualizar { copy(nombre = evento.nombre) }
            is AnomaliaEvent.ElegirSeveridad -> actualizar { copy(severidad = evento.severidad) }
            is AnomaliaEvent.DescripcionCambio -> actualizar { copy(descripcion = evento.valor) }
            AnomaliaEvent.Volver -> emitir(UiEffect.Volver)
            AnomaliaEvent.Ayuda -> emitir(UiEffect.Navegar(AppDestination.Ayuda))
            AnomaliaEvent.Guardar -> guardar()
        }
    }

    private fun guardar() {
        actualizar { copy(intentoGuardar = true) }
        if (!estado.puedeGuardar) return
        actualizar { copy(cargando = true) }
        lanzar {
            val s = estado
            when (val r = registrar(loteId, s.tipo, s.nombre, s.severidad, s.descripcion, sesion.actual()?.nombre.orEmpty())) {
                is AppResult.Exito -> {
                    recientes.marcar(loteId, AccionLote.ANOMALIA)
                    notificarExito(Mensajes.ANOMALIA_REGISTRADA)
                    emitir(UiEffect.Navegar(AppDestination.Recomendaciones(loteId), reemplazar = true))
                }
                is AppResult.Fallo -> {
                    actualizar { copy(cargando = false) }
                    notificarError(r.error)
                }
            }
        }
    }
}
