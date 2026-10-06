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

data class EscanerQrUiState(
    val linterna: Boolean = false,
    val mostrarManual: Boolean = false,
    val codigo: String = "",
    val errorCodigo: String? = null,
    val buscando: Boolean = false,
    val camaraReal: Boolean = false,
) {
    val puedeBuscar: Boolean get() = codigo.isNotBlank() && !buscando
}

sealed interface EscanerQrEvent {
    data object AlternarLinterna : EscanerQrEvent
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

    init {
        camara.iniciar { codigo -> buscar(codigo) }
    }

    override fun onEvent(evento: EscanerQrEvent) {
        when (evento) {
            EscanerQrEvent.AlternarLinterna -> {
                val nueva = !estado.linterna
                camara.alternarLinterna(nueva)
                actualizar { copy(linterna = nueva) }
            }
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

    override fun onCleared() {
        camara.detener()
        super.onCleared()
    }
}
