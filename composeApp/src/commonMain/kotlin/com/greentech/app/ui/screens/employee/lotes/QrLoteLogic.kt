package com.greentech.app.ui.screens.employee.lotes

import com.greentech.app.navigation.AppDestination
import com.greentech.app.platform.QrSaver
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.core.UiEffect
import com.greentech.app.ui.core.aEstadoCarga
import com.greentech.app.ui.strings.Textos
import com.greentech.shared.domain.model.Lote
import com.greentech.shared.domain.result.AppError
import com.greentech.shared.domain.usecase.ObtenerLoteUseCase

data class QrLoteUiState(
    val lote: EstadoCarga<Lote> = EstadoCarga.Cargando,
    val recienCreado: Boolean = false,
    val guardando: Boolean = false,
)

sealed interface QrLoteEvent {
    data object Reintentar : QrLoteEvent
    data object GuardarImagen : QrLoteEvent
    data object Compartir : QrLoteEvent
    data object VerDetalle : QrLoteEvent
    data object RegistrarOtro : QrLoteEvent
    data object Volver : QrLoteEvent
    data object Ayuda : QrLoteEvent
}

class QrLoteLogic(
    private val loteId: String,
    recienCreado: Boolean,
    private val obtenerLote: ObtenerLoteUseCase,
    private val qrSaver: QrSaver,
) : BaseLogic<QrLoteUiState, QrLoteEvent>(QrLoteUiState(recienCreado = recienCreado)) {

    init { cargar() }

    override fun onEvent(evento: QrLoteEvent) {
        val codigo = estado.lote.datosOrNull?.codigo
        when (evento) {
            QrLoteEvent.Reintentar -> cargar()
            QrLoteEvent.GuardarImagen -> if (codigo != null && !estado.guardando) lanzar {
                actualizar { copy(guardando = true) }
                val ok = qrSaver.guardarImagen(codigo)
                actualizar { copy(guardando = false) }
                if (ok) notificarExito(Textos.Qr.GUARDADO) else notificarError(AppError.Desconocido)
            }
            QrLoteEvent.Compartir -> if (codigo != null) lanzar {
                if (qrSaver.compartir(codigo)) notificarInfo(Textos.Qr.COMPARTIDO) else notificarError(AppError.Desconocido)
            }
            QrLoteEvent.VerDetalle -> emitir(UiEffect.Navegar(AppDestination.DetalleLote(loteId), reemplazar = true))
            QrLoteEvent.RegistrarOtro -> emitir(UiEffect.Navegar(AppDestination.RegistrarLote, reemplazar = true))
            QrLoteEvent.Volver -> emitir(UiEffect.Volver)
            QrLoteEvent.Ayuda -> emitir(UiEffect.Navegar(AppDestination.Ayuda))
        }
    }

    private fun cargar() = lanzar {
        actualizar { copy(lote = EstadoCarga.Cargando) }
        val r = obtenerLote(loteId).aEstadoCarga()
        actualizar { copy(lote = r) }
    }
}
