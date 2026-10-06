package com.greentech.app.ui.screens.employee.lotes

import com.greentech.app.navigation.AppDestination
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.PreferenciasUso
import com.greentech.app.ui.core.UiEffect
import com.greentech.app.ui.strings.Textos
import com.greentech.shared.domain.model.Especie
import com.greentech.shared.domain.model.Zona
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.domain.result.Mensajes
import com.greentech.shared.domain.result.datosOrNull
import com.greentech.shared.domain.usecase.ObtenerCatalogosLoteUseCase
import com.greentech.shared.domain.usecase.ObtenerSesionUseCase
import com.greentech.shared.domain.usecase.RegistrarLoteUseCase
import com.greentech.shared.domain.validation.LoteValidator
import com.greentech.shared.util.Fechas
import kotlinx.datetime.LocalDate

data class RegistrarLoteUiState(
    val especies: List<Especie> = emptyList(),
    val zonas: List<Zona> = emptyList(),
    val especie: Especie? = null,
    val zona: Zona? = null,
    /** Zona sugerida (la del último lote registrado). */
    val zonaSugerida: Zona? = null,
    val cantidad: Int? = null,
    val fecha: LocalDate = Fechas.hoy(),
    val responsable: String = "",
    val intentoGuardar: Boolean = false,
    val cantidadTocada: Boolean = false,
    val cargando: Boolean = false,
) {
    val errorEspecie: String? get() = if (intentoGuardar) LoteValidator.validarEspecie(especie?.id) else null
    val errorZona: String? get() = if (intentoGuardar) LoteValidator.validarZona(zona?.id) else null
    val errorCantidad: String? get() = if (cantidadTocada || intentoGuardar) LoteValidator.validarCantidad(cantidad) else null

    val faltantes: List<String>
        get() = buildList {
            if (especie == null) add(Textos.RegistrarLote.FALTA_ESPECIE)
            if (zona == null) add(Textos.RegistrarLote.FALTA_ZONA)
            if (LoteValidator.validarCantidad(cantidad) != null) add(Textos.RegistrarLote.FALTA_CANTIDAD)
        }

    val puedeGuardar: Boolean get() = !cargando && faltantes.isEmpty()

    val hayCambios: Boolean
        get() = especie != null || cantidad != null || zona != zonaSugerida || fecha != Fechas.hoy()
}

sealed interface RegistrarLoteEvent {
    data class ElegirEspecie(val especie: Especie) : RegistrarLoteEvent
    data class ElegirZona(val zona: Zona) : RegistrarLoteEvent
    data class CambiarCantidad(val cantidad: Int?) : RegistrarLoteEvent
    data class CambiarFecha(val fecha: LocalDate) : RegistrarLoteEvent
    data object Guardar : RegistrarLoteEvent
    data object Volver : RegistrarLoteEvent
    data object Ayuda : RegistrarLoteEvent
}

class RegistrarLoteLogic(
    sesion: ObtenerSesionUseCase,
    private val catalogos: ObtenerCatalogosLoteUseCase,
    private val registrarLote: RegistrarLoteUseCase,
    private val preferencias: PreferenciasUso,
) : BaseLogic<RegistrarLoteUiState, RegistrarLoteEvent>(
    RegistrarLoteUiState(responsable = sesion.actual()?.nombre.orEmpty()),
) {
    init {
        lanzar {
            val especies = catalogos.especies().datosOrNull().orEmpty()
            val zonas = catalogos.zonas().datosOrNull().orEmpty()
            // Valor por defecto útil: la zona del último lote que se registró en este equipo.
            val sugerida = zonas.firstOrNull { it.id == preferencias.ultimaZonaId }
            actualizar { copy(especies = especies, zonas = zonas, zonaSugerida = sugerida, zona = zona ?: sugerida) }
        }
    }

    override fun onEvent(evento: RegistrarLoteEvent) {
        when (evento) {
            is RegistrarLoteEvent.ElegirEspecie -> actualizar { copy(especie = evento.especie) }
            is RegistrarLoteEvent.ElegirZona -> actualizar { copy(zona = evento.zona) }
            is RegistrarLoteEvent.CambiarCantidad -> actualizar { copy(cantidad = evento.cantidad, cantidadTocada = true) }
            is RegistrarLoteEvent.CambiarFecha -> actualizar { copy(fecha = evento.fecha) }
            RegistrarLoteEvent.Volver -> emitir(UiEffect.Volver)
            RegistrarLoteEvent.Ayuda -> emitir(UiEffect.Navegar(AppDestination.Ayuda))
            RegistrarLoteEvent.Guardar -> guardar()
        }
    }

    private fun guardar() {
        actualizar { copy(intentoGuardar = true) }
        if (!estado.puedeGuardar) return
        actualizar { copy(cargando = true) }
        lanzar {
            val s = estado
            when (val r = registrarLote(s.especie?.id, s.zona?.id, s.cantidad, s.fecha, s.responsable)) {
                is AppResult.Exito -> {
                    preferencias.ultimaZonaId = s.zona?.id
                    notificarExito(Mensajes.LOTE_REGISTRADO)
                    // Siguiente paso: el QR del lote nuevo con "Guardar imagen", "Ver lote" y "Registrar otro lote".
                    emitir(UiEffect.Navegar(AppDestination.QrLote(r.datos.id, recienCreado = true), reemplazar = true))
                }
                is AppResult.Fallo -> {
                    actualizar { copy(cargando = false) }
                    notificarError(r.error)
                }
            }
        }
    }
}
