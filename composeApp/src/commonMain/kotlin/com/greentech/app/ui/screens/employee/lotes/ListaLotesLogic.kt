package com.greentech.app.ui.screens.employee.lotes

import com.greentech.app.navigation.AppDestination
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.core.UiEffect
import com.greentech.app.ui.core.aEstadoCarga
import com.greentech.shared.domain.model.Etapa
import com.greentech.shared.domain.model.Lote
import com.greentech.shared.domain.usecase.ObtenerLotesUseCase

data class ListaLotesUiState(
    val lotes: EstadoCarga<List<Lote>> = EstadoCarga.Cargando,
    val busqueda: String = "",
    val especie: String? = null,
    val etapa: Etapa? = null,
    val soloLectura: Boolean = false,
) {
    val especies: List<String?>
        get() = listOf<String?>(null) + (lotes.datosOrNull.orEmpty().map { it.especie.nombre }.distinct().sorted())

    val etapas: List<Etapa?> get() = listOf<Etapa?>(null) + Etapa.entries

    val hayFiltros: Boolean get() = filtrosActivos > 0

    /** Búsqueda, especie y etapa cuentan como un filtro cada uno. */
    val filtrosActivos: Int get() = listOf(busqueda.isNotBlank(), especie != null, etapa != null).count { it }

    val filtrados: List<Lote>
        get() = lotes.datosOrNull.orEmpty().filter { l ->
            (busqueda.isBlank() || l.especie.nombre.contains(busqueda.trim(), true) || l.codigo.contains(busqueda.trim(), true)) &&
                (especie == null || l.especie.nombre == especie) &&
                (etapa == null || l.etapa == etapa)
        }
}

sealed interface ListaLotesEvent {
    data object Entrar : ListaLotesEvent
    data class Buscar(val texto: String) : ListaLotesEvent
    data class FiltrarEspecie(val especie: String?) : ListaLotesEvent
    data class FiltrarEtapa(val etapa: Etapa?) : ListaLotesEvent
    data object QuitarFiltros : ListaLotesEvent
    data class AbrirLote(val loteId: String) : ListaLotesEvent
    data object NuevoLote : ListaLotesEvent
    data object Ayuda : ListaLotesEvent
}

/** Lista de lotes del empleado. El administrador la reutiliza en modo solo lectura (LotesLogic). */
open class ListaLotesLogic(
    private val obtenerLotes: ObtenerLotesUseCase,
    soloLectura: Boolean = false,
) : BaseLogic<ListaLotesUiState, ListaLotesEvent>(ListaLotesUiState(soloLectura = soloLectura)) {

    override fun onEvent(evento: ListaLotesEvent) {
        when (evento) {
            ListaLotesEvent.Entrar -> cargar()
            is ListaLotesEvent.Buscar -> actualizar { copy(busqueda = evento.texto) }
            is ListaLotesEvent.FiltrarEspecie -> actualizar { copy(especie = evento.especie) }
            is ListaLotesEvent.FiltrarEtapa -> actualizar { copy(etapa = evento.etapa) }
            ListaLotesEvent.QuitarFiltros -> actualizar { copy(busqueda = "", especie = null, etapa = null) }
            is ListaLotesEvent.AbrirLote -> emitir(
                UiEffect.Navegar(AppDestination.DetalleLote(evento.loteId, soloLectura = estado.soloLectura)),
            )
            ListaLotesEvent.NuevoLote -> if (!estado.soloLectura) emitir(UiEffect.Navegar(AppDestination.RegistrarLote))
            ListaLotesEvent.Ayuda -> emitir(UiEffect.Navegar(AppDestination.Ayuda))
        }
    }

    private fun cargar() = lanzar {
        if (estado.lotes !is EstadoCarga.Listo) actualizar { copy(lotes = EstadoCarga.Cargando) }
        val r = obtenerLotes().aEstadoCarga()
        actualizar { copy(lotes = r) }
    }
}
