package com.greentech.app.ui.screens.employee.monitoreo

import com.greentech.app.navigation.AppDestination
import com.greentech.app.platform.CameraScanner
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.UiEffect
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.domain.result.AppError
import com.greentech.shared.domain.validation.LoteValidator
import com.greentech.app.ui.strings.Textos
import com.greentech.shared.domain.usecase.BuscarLotePorCodigoUseCase
import com.greentech.app.ui.notification.TipoNotificacion
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.TimeSource

data class EscanerQrUiState(
    val linterna: Boolean = false,
    val mostrarManual: Boolean = false,
    val codigo: String = "",
    val errorCodigo: String? = null,
    val buscando: Boolean = false,
    val camaraReal: Boolean = false,
    val linternaDisponible: Boolean = false,
    val zoom: Float = 1f,
    /** Se leyó un QR: el marco cambia a primary con el check mientras se busca el lote. */
    val detectado: Boolean = false,
) {
    val puedeBuscar: Boolean get() = codigo.isNotBlank() && !buscando
}

sealed interface EscanerQrEvent {
    data object AlternarLinterna : EscanerQrEvent
    data object AlternarZoom : EscanerQrEvent
    data object AbrirManual : EscanerQrEvent
    data object CerrarManual : EscanerQrEvent
    data class CodigoCambio(val valor: String) : EscanerQrEvent
    data object BuscarCodigo : EscanerQrEvent
    data object Ayuda : EscanerQrEvent
}

class EscanerQrLogic(
    private val camara: CameraScanner,
    private val buscarLote: BuscarLotePorCodigoUseCase,
) : BaseLogic<EscanerQrUiState, EscanerQrEvent>(EscanerQrUiState(camaraReal = camara.disponible)) {

    /** Último QR desconocido: no se vuelve a avisar mientras siga frente a la cámara. */
    private var ultimoDesconocido: Pair<String, TimeSource.Monotonic.ValueTimeMark>? = null

    init {
        camara.iniciar { codigo -> leido(codigo) }
        camara.linternaEncendida.onEach { actualizar { copy(linterna = it) } }.launchIn(viewModelScope)
        camara.linternaDisponible.onEach { actualizar { copy(linternaDisponible = it) } }.launchIn(viewModelScope)
        camara.zoom.onEach { actualizar { copy(zoom = it) } }.launchIn(viewModelScope)
    }

    override fun onEvent(evento: EscanerQrEvent) {
        when (evento) {
            EscanerQrEvent.AlternarLinterna -> {
                val nueva = !estado.linterna
                camara.alternarLinterna(nueva)
                actualizar { copy(linterna = nueva) }
            }
            EscanerQrEvent.AlternarZoom -> camara.cambiarZoom(if (estado.zoom < 1.5f) 2f else 1f)
            EscanerQrEvent.AbrirManual -> actualizar { copy(mostrarManual = true, errorCodigo = null) }
            EscanerQrEvent.CerrarManual -> actualizar { copy(mostrarManual = false, errorCodigo = null) }
            is EscanerQrEvent.CodigoCambio -> actualizar { copy(codigo = evento.valor.uppercase().trim(), errorCodigo = null) }
            EscanerQrEvent.BuscarCodigo -> when {
                !estado.puedeBuscar -> Unit
                // Se revisa el formato antes de buscar para dar un error claro sin esperar al servidor.
                !LoteValidator.codigoValido(estado.codigo) -> actualizar { copy(errorCodigo = Textos.Escaner.FORMATO_INVALIDO) }
                else -> buscar(LoteValidator.normalizarCodigo(estado.codigo))
            }
            EscanerQrEvent.Ayuda -> emitir(UiEffect.Navegar(AppDestination.Ayuda))
        }
    }

    private fun buscar(codigo: String) = lanzar {
        actualizar { copy(buscando = true, errorCodigo = null) }
        when (val r = buscarLote(codigo)) {
            is AppResult.Exito -> {
                actualizar { copy(buscando = false, mostrarManual = false, codigo = "") }
                emitir(UiEffect.Navegar(AppDestination.DetalleLote(r.datos.id)))
            }
            is AppResult.Fallo -> {
                val mensaje = if (r.error == AppError.LoteNoEncontrado) Textos.Escaner.NO_ENCONTRADO.replace("%s", codigo) else r.error.mensaje
                actualizar { copy(buscando = false, errorCodigo = mensaje) }
                notificarError(r.error)
            }
        }
    }

    /** Código leído por la cámara: si el lote existe abre su detalle; si no, avisa y sigue escaneando. */
    private fun leido(codigo: String) {
        if (estado.detectado || estado.buscando || estado.mostrarManual) return
        val normalizado = LoteValidator.normalizarCodigo(codigo)
        ultimoDesconocido?.let { (previo, marca) ->
            if (previo == normalizado && marca.elapsedNow().inWholeMilliseconds < REAVISO_MS) return
        }
        actualizar { copy(detectado = true) }
        lanzar {
            val valido = LoteValidator.codigoValido(normalizado)
            val r = if (valido) buscarLote(normalizado) else AppResult.Fallo(AppError.LoteNoEncontrado)
            when (r) {
                is AppResult.Exito -> {
                    delay(250) // deja ver el check antes de cambiar de pantalla
                    emitir(UiEffect.Navegar(AppDestination.DetalleLote(r.datos.id)))
                    delay(500)
                    actualizar { copy(detectado = false) }
                }
                is AppResult.Fallo -> {
                    if (r.error == AppError.LoteNoEncontrado) {
                        ultimoDesconocido = normalizado to TimeSource.Monotonic.markNow()
                        emitir(
                            UiEffect.Notificar(
                                TipoNotificacion.ERROR,
                                Textos.Escaner.NO_ENCONTRADO.replace("%s", if (valido) normalizado else codigo.trim().take(40)),
                                titulo = Textos.Escaner.LOTE_NO_ENCONTRADO,
                            ),
                        )
                    } else {
                        notificarError(r.error)
                    }
                    actualizar { copy(detectado = false) }
                }
            }
        }
    }

    override fun onCleared() {
        camara.detener()
        super.onCleared()
    }

    private companion object {
        const val REAVISO_MS = 5_000L
    }
}
