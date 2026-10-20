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
import com.greentech.shared.domain.model.Lote
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.domain.result.Mensajes
import com.greentech.shared.domain.usecase.ObtenerLoteUseCase
import com.greentech.shared.domain.usecase.ObtenerSesionUseCase
import com.greentech.shared.domain.usecase.RegistrarObservacionUseCase

@Immutable
data class ObservacionUiState(
    val lote: EstadoCarga<Lote> = EstadoCarga.Cargando,
    val texto: String = "",
    val textoTocado: Boolean = false,
    val salud: EstadoSalud = EstadoSalud.SANA,
    val saludInicial: EstadoSalud = EstadoSalud.SANA,
    val conFoto: Boolean = false,
    /** URI local de la foto (cámara o galería). */
    val fotoUri: String? = null,
    val mostrarOpcionesFoto: Boolean = false,
    val cargando: Boolean = false,
) {
    val hayCambios: Boolean get() = texto.isNotBlank() || conFoto || salud != saludInicial
    val errorTexto: String? get() = if (textoTocado && texto.isBlank()) Textos.Observacion.TEXTO_ERROR else null
    val faltantes: List<String> get() = if (texto.isBlank()) listOf(Textos.Observacion.FALTA_TEXTO) else emptyList()
    val puedeGuardar: Boolean get() = !cargando && lote is EstadoCarga.Listo && faltantes.isEmpty()
}

sealed interface ObservacionEvent {
    data class TextoCambio(val valor: String) : ObservacionEvent
    data class ElegirSalud(val salud: EstadoSalud) : ObservacionEvent
    data object AgregarFoto : ObservacionEvent
    data object CerrarOpcionesFoto : ObservacionEvent
    data class FotoElegida(val uri: String) : ObservacionEvent
    data object QuitarFoto : ObservacionEvent
    data object Guardar : ObservacionEvent
    data object Volver : ObservacionEvent
    data object Ayuda : ObservacionEvent
}

/** Lo escrito en el formulario (sobrevive a la muerte del proceso). */
@Serializable
private data class BorradorObservacion(val texto: String, val textoTocado: Boolean, val salud: EstadoSalud, val fotoUri: String?)

class ObservacionLogic(
    private val loteId: String,
    private val obtenerLote: ObtenerLoteUseCase,
    private val registrar: RegistrarObservacionUseCase,
    private val sesion: ObtenerSesionUseCase,
    private val recientes: RegistrosRecientes,
    handle: SavedStateHandle,
) : BaseLogic<ObservacionUiState, ObservacionEvent>(ObservacionUiState()) {

    init {
        val borrador = conservarBorrador(handle, BorradorObservacion.serializer(), { BorradorObservacion(texto, textoTocado, salud, fotoUri) }) {
            copy(texto = it.texto, textoTocado = it.textoTocado, salud = it.salud, fotoUri = it.fotoUri, conFoto = it.fotoUri != null)
        }
        lanzar {
            val r = obtenerLote(loteId)
            val saludActual = (r as? AppResult.Exito)?.datos?.estadoSalud ?: estado.salud
            // La salud elegida antes de recrear la pantalla gana sobre la del lote.
            actualizar { copy(lote = r.aEstadoCarga(), salud = borrador?.salud ?: saludActual, saludInicial = saludActual) }
        }
    }

    override fun onEvent(evento: ObservacionEvent) {
        when (evento) {
            is ObservacionEvent.TextoCambio -> actualizar { copy(texto = evento.valor, textoTocado = true) }
            is ObservacionEvent.ElegirSalud -> actualizar { copy(salud = evento.salud) }
            ObservacionEvent.AgregarFoto -> actualizar { copy(mostrarOpcionesFoto = true) }
            ObservacionEvent.CerrarOpcionesFoto -> actualizar { copy(mostrarOpcionesFoto = false) }
            is ObservacionEvent.FotoElegida -> actualizar { copy(conFoto = true, fotoUri = evento.uri, mostrarOpcionesFoto = false) }
            ObservacionEvent.QuitarFoto -> actualizar { copy(conFoto = false, fotoUri = null) }
            ObservacionEvent.Volver -> emitir(UiEffect.Volver)
            ObservacionEvent.Ayuda -> emitir(UiEffect.Navegar(AppDestination.Ayuda))
            ObservacionEvent.Guardar -> guardar()
        }
    }

    private fun guardar() {
        actualizar { copy(textoTocado = true) }
        if (!estado.puedeGuardar) return
        actualizar { copy(cargando = true) }
        lanzar {
            val s = estado
            // TODO(Supabase Storage): subir s.fotoUri al bucket de observaciones y guardar la URL pública.
            when (val r = registrar(loteId, s.texto, s.salud, s.conFoto, sesion.actual()?.nombre.orEmpty())) {
                is AppResult.Exito -> {
                    recientes.marcar(loteId, AccionLote.OBSERVACION)
                    notificarExito(Mensajes.OBSERVACION_REGISTRADA)
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
