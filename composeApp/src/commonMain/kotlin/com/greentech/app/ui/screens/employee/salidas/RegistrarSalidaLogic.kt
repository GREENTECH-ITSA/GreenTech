package com.greentech.app.ui.screens.employee.salidas

import com.greentech.app.navigation.AppDestination
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.core.UiEffect
import com.greentech.app.ui.core.aEstadoCarga
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.screens.employee.monitoreo.AccionLote
import com.greentech.app.ui.screens.employee.monitoreo.RegistrosRecientes
import com.greentech.shared.domain.model.Lote
import com.greentech.shared.domain.model.MotivoMerma
import com.greentech.shared.domain.model.TipoSalida
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.domain.result.Mensajes
import com.greentech.shared.domain.usecase.ObtenerLoteUseCase
import com.greentech.shared.domain.usecase.ObtenerSesionUseCase
import com.greentech.shared.domain.usecase.RegistrarSalidaUseCase
import com.greentech.shared.domain.validation.CantidadValidator
import com.greentech.shared.util.Fechas
import kotlinx.datetime.LocalDate

data class RegistrarSalidaUiState(
    val lote: EstadoCarga<Lote> = EstadoCarga.Cargando,
    val tipo: TipoSalida? = null,
    val cantidad: Int? = null,
    val motivo: MotivoMerma? = null,
    val fecha: LocalDate = Fechas.hoy(),
    val confirmar: Boolean = false,
    val cargando: Boolean = false,
) {
    val disponibles: Int get() = lote.datosOrNull?.cantidadDisponible ?: 0

    val errorCantidad: String? get() = if (cantidad != null) CantidadValidator.validar(cantidad, disponibles) else null

    val faltantes: List<String>
        get() = buildList {
            if (tipo == null) add(Textos.Salida.FALTA_TIPO)
            if (CantidadValidator.validar(cantidad, disponibles) != null) add(Textos.Salida.FALTA_CANTIDAD)
            if (tipo == TipoSalida.MERMA && motivo == null) add(Textos.Salida.FALTA_MOTIVO)
        }

    val puedeGuardar: Boolean get() = !cargando && lote is EstadoCarga.Listo && faltantes.isEmpty()

    val hayCambios: Boolean get() = tipo != null || cantidad != null || motivo != null || fecha != Fechas.hoy()

    /** Plantas que quedarán en el lote después de la salida. */
    val quedaran: Int get() = (disponibles - (cantidad ?: 0)).coerceAtLeast(0)
}

sealed interface RegistrarSalidaEvent {
    data class ElegirTipo(val tipo: TipoSalida) : RegistrarSalidaEvent
    data class CambiarCantidad(val cantidad: Int?) : RegistrarSalidaEvent
    data class ElegirMotivo(val motivo: MotivoMerma) : RegistrarSalidaEvent
    data class CambiarFecha(val fecha: LocalDate) : RegistrarSalidaEvent
    data object PedirConfirmacion : RegistrarSalidaEvent
    data object CancelarConfirmacion : RegistrarSalidaEvent
    data object Confirmar : RegistrarSalidaEvent
    data object Volver : RegistrarSalidaEvent
    data object Ayuda : RegistrarSalidaEvent
}

class RegistrarSalidaLogic(
    private val loteId: String,
    private val obtenerLote: ObtenerLoteUseCase,
    private val registrarSalida: RegistrarSalidaUseCase,
    private val sesion: ObtenerSesionUseCase,
    private val recientes: RegistrosRecientes,
) : BaseLogic<RegistrarSalidaUiState, RegistrarSalidaEvent>(RegistrarSalidaUiState()) {

    init {
        lanzar {
            val r = obtenerLote(loteId).aEstadoCarga()
            actualizar { copy(lote = r) }
        }
    }

    override fun onEvent(evento: RegistrarSalidaEvent) {
        when (evento) {
            is RegistrarSalidaEvent.ElegirTipo -> actualizar { copy(tipo = evento.tipo) }
            is RegistrarSalidaEvent.CambiarCantidad -> actualizar { copy(cantidad = evento.cantidad) }
            is RegistrarSalidaEvent.ElegirMotivo -> actualizar { copy(motivo = evento.motivo) }
            is RegistrarSalidaEvent.CambiarFecha -> actualizar { copy(fecha = evento.fecha) }
            RegistrarSalidaEvent.PedirConfirmacion -> if (estado.puedeGuardar) actualizar { copy(confirmar = true) }
            RegistrarSalidaEvent.CancelarConfirmacion -> actualizar { copy(confirmar = false) }
            RegistrarSalidaEvent.Confirmar -> guardar()
            RegistrarSalidaEvent.Volver -> emitir(UiEffect.Volver)
            RegistrarSalidaEvent.Ayuda -> emitir(UiEffect.Navegar(AppDestination.Ayuda))
        }
    }

    private fun guardar() {
        if (!estado.puedeGuardar) return
        actualizar { copy(cargando = true) }
        lanzar {
            val s = estado
            when (val r = registrarSalida(loteId, s.tipo, s.cantidad, s.motivo, s.fecha, sesion.actual()?.nombre.orEmpty())) {
                is AppResult.Exito -> {
                    actualizar { copy(confirmar = false) }
                    // Al volver, el detalle anima "Disponibles" al nuevo valor y lo marca como actualizado.
                    recientes.marcar(loteId, AccionLote.SALIDA)
                    notificarExito(if (r.datos.tipo == TipoSalida.VENTA) Mensajes.VENTA_REGISTRADA else Mensajes.MERMA_REGISTRADA)
                    emitir(UiEffect.Volver)
                }
                is AppResult.Fallo -> {
                    actualizar { copy(cargando = false, confirmar = false) }
                    notificarError(r.error)
                }
            }
        }
    }
}
