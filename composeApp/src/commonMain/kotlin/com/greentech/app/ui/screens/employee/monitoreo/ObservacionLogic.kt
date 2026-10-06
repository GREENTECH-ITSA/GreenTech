package com.greentech.app.ui.screens.employee.monitoreo

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

data class ObservacionUiState(
    val lote: EstadoCarga<Lote> = EstadoCarga.Cargando,
    val texto: String = "",
    val textoTocado: Boolean = false,
    val salud: EstadoSalud = EstadoSalud.SANA,
    val saludInicial: EstadoSalud = EstadoSalud.SANA,
    val conFoto: Boolean = false,
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
    data object QuitarFoto : ObservacionEvent
    data object Guardar : ObservacionEvent
    data object Volver : ObservacionEvent
    data object Ayuda : ObservacionEvent
}

class ObservacionLogic(
    private val loteId: String,
    private val obtenerLote: ObtenerLoteUseCase,
    private val registrar: RegistrarObservacionUseCase,
    private val sesion: ObtenerSesionUseCase,
    private val recientes: RegistrosRecientes,
) : BaseLogic<ObservacionUiState, ObservacionEvent>(ObservacionUiState()) {

    init {
        lanzar {
            val r = obtenerLote(loteId)
            val saludActual = (r as? AppResult.Exito)?.datos?.estadoSalud ?: estado.salud
            actualizar { copy(lote = r.aEstadoCarga(), salud = saludActual, saludInicial = saludActual) }
        }
    }

    override fun onEvent(evento: ObservacionEvent) {
        when (evento) {
            is ObservacionEvent.TextoCambio -> actualizar { copy(texto = evento.valor, textoTocado = true) }
            is ObservacionEvent.ElegirSalud -> actualizar { copy(salud = evento.salud) }
            ObservacionEvent.AgregarFoto -> {
                // TODO(Cámara): abrir la cámara y subir la foto a Supabase Storage.
                actualizar { copy(conFoto = true) }
            }
            ObservacionEvent.QuitarFoto -> actualizar { copy(conFoto = false) }
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
