package com.greentech.app.ui.screens.employee.home

import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import androidx.lifecycle.viewModelScope
import com.greentech.app.platform.MonitorConexion
import androidx.compose.runtime.Immutable
import com.greentech.app.ui.core.IndiceLotesLocal
import com.greentech.app.navigation.AppDestination
import com.greentech.app.ui.components.EstadoSincronizacion
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.core.UiEffect
import com.greentech.app.ui.core.aEstadoCarga
import com.greentech.app.ui.strings.Textos
import com.greentech.shared.domain.model.AlertaSensor
import com.greentech.shared.domain.model.CondicionesLote
import com.greentech.shared.domain.model.EventoBitacora
import com.greentech.shared.domain.model.Lote
import com.greentech.shared.domain.model.Periodo
import com.greentech.shared.domain.usecase.ObtenerCondicionesUseCase
import com.greentech.shared.domain.usecase.ObtenerDashboardUseCase
import com.greentech.shared.domain.usecase.ObtenerLotesUseCase
import com.greentech.shared.domain.usecase.ObtenerActividadRecienteUseCase
import com.greentech.shared.domain.usecase.ObtenerAlertasUseCase
import com.greentech.shared.domain.usecase.ObtenerSesionUseCase
import com.greentech.shared.domain.result.datosOrNull
import com.greentech.shared.util.Fechas
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

@Immutable
data class EmployeeHomeUiState(
    val nombre: String = "",
    val fecha: String = "",
    val alertas: EstadoCarga<List<AlertaSensor>> = EstadoCarga.Cargando,
    val recientes: EstadoCarga<List<EventoBitacora>> = EstadoCarga.Cargando,
    val sync: EstadoSincronizacion = EstadoSincronizacion(),
    // Datos para los mosaicos del inicio (GreenTech UI). Se cargan con casos de uso ya existentes.
    val lotes: EstadoCarga<List<Lote>> = EstadoCarga.Cargando,
    val anomaliasAbiertas: Int? = null,
    val condiciones: CondicionesLote? = null,
) {
    /** Lotes con plantas disponibles. */
    val lotesActivos: Int? get() = lotes.datosOrNull?.count { it.cantidadDisponible > 0 }
}

sealed interface EmployeeHomeEvent {
    data object Entrar : EmployeeHomeEvent
    data object NuevoLote : EmployeeHomeEvent
    data object Escanear : EmployeeHomeEvent
    data object VerLotes : EmployeeHomeEvent
    data class AbrirLote(val loteId: String) : EmployeeHomeEvent
    data object Sincronizar : EmployeeHomeEvent
    data object Ayuda : EmployeeHomeEvent
}

class EmployeeHomeLogic(
    sesion: ObtenerSesionUseCase,
    private val obtenerAlertas: ObtenerAlertasUseCase,
    private val obtenerRecientes: ObtenerActividadRecienteUseCase,
    private val obtenerLotes: ObtenerLotesUseCase,
    private val obtenerCondiciones: ObtenerCondicionesUseCase,
    private val obtenerDashboard: ObtenerDashboardUseCase,
    private val indice: IndiceLotesLocal,
    conexion: MonitorConexion,
) : BaseLogic<EmployeeHomeUiState, EmployeeHomeEvent>(
    EmployeeHomeUiState(
        nombre = sesion.actual()?.primerNombre.orEmpty(),
        fecha = Fechas.larga(Fechas.hoy()),
        // Simulado: dos registros pendientes hasta que exista el SyncManager real.
        sync = EstadoSincronizacion(enLinea = true, pendientes = 2, ultimaHora = Fechas.hora(Fechas.ahora())),
    ),
) {
    init {
        // El banner "Sin conexión" cambia en cuanto cambia la red (el aviso llega de otro hilo).
        conexion.enLinea.onEach { enLinea -> actualizar { copy(sync = sync.copy(enLinea = enLinea)) } }.launchIn(viewModelScope)
    }

    override fun onEvent(evento: EmployeeHomeEvent) {
        when (evento) {
            EmployeeHomeEvent.Entrar -> cargar()
            EmployeeHomeEvent.NuevoLote -> emitir(UiEffect.Navegar(AppDestination.RegistrarLote))
            EmployeeHomeEvent.Escanear -> emitir(UiEffect.Navegar(AppDestination.EscanerQr))
            EmployeeHomeEvent.VerLotes -> emitir(UiEffect.Navegar(AppDestination.ListaLotes))
            is EmployeeHomeEvent.AbrirLote -> emitir(UiEffect.Navegar(AppDestination.DetalleLote(evento.loteId)))
            EmployeeHomeEvent.Ayuda -> emitir(UiEffect.Navegar(AppDestination.Ayuda))
            EmployeeHomeEvent.Sincronizar -> sincronizar()
        }
    }

    private fun cargar() = lanzar {
        // Si ya hay datos, se recargan en silencio (sin volver al skeleton).
        if (estado.recientes !is EstadoCarga.Listo) actualizar { copy(alertas = EstadoCarga.Cargando, recientes = EstadoCarga.Cargando) }
        coroutineScope {
            val alertas = async { obtenerAlertas() }
            val recientes = async { obtenerRecientes(5) }
            val lotes = async { obtenerLotes() }
            val anomalias = async { obtenerDashboard(Periodo.MES).datosOrNull()?.anomaliasAbiertas?.size }
            val nuevasAlertas = alertas.await().aEstadoCarga()
            val nuevosRecientes = recientes.await().aEstadoCarga()
            val nuevosLotes = lotes.await().aEstadoCarga()
            // Deja listos los lotes para la consulta por QR local (sin esperar al repositorio al escanear).
            nuevosLotes.datosOrNull?.let(indice::recordar)
            // Condiciones de la zona del primer lote activo (mini gauges del inicio).
            val loteZona = nuevosLotes.datosOrNull?.firstOrNull { it.cantidadDisponible > 0 }
            val condiciones = loteZona?.let { obtenerCondiciones(it.id).datosOrNull() }
            val abiertas = anomalias.await()
            actualizar {
                copy(
                    alertas = nuevasAlertas,
                    recientes = nuevosRecientes,
                    lotes = nuevosLotes,
                    anomaliasAbiertas = abiertas,
                    condiciones = condiciones,
                )
            }
        }
    }

    private fun sincronizar() = lanzar {
        actualizar { copy(sync = sync.copy(sincronizando = true)) }
        delay(1_500)
        actualizar { copy(sync = sync.copy(sincronizando = false, pendientes = 0, ultimaHora = Fechas.hora(Fechas.ahora()))) }
        notificarInfo(Textos.Sync.COMPLETA)
    }
}
