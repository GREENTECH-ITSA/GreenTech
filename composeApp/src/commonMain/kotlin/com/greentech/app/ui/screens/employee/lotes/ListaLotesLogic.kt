package com.greentech.app.ui.screens.employee.lotes

import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.ImmutableList
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.Immutable
import com.greentech.app.ui.core.IndiceLotesLocal
import com.greentech.app.navigation.AppDestination
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.core.UiEffect
import com.greentech.app.ui.core.aEstadoCarga
import com.greentech.shared.domain.model.Etapa
import com.greentech.shared.domain.model.Lote
import com.greentech.shared.domain.usecase.ObtenerLotesUseCase

/**
 * Estado de la lista de lotes. [filtrados] y [especies] llegan ya calculados (el Logic los calcula en
 * Dispatchers.Default al cambiar los datos o un filtro): la pantalla no filtra ni ordena al recomponer.
 * Los valores por defecto los calculan a partir de los demás campos (útil en previews).
 */
@Immutable
data class ListaLotesUiState(
    val lotes: EstadoCarga<List<Lote>> = EstadoCarga.Cargando,
    val busqueda: String = "",
    val especie: String? = null,
    val etapa: Etapa? = null,
    val soloLectura: Boolean = false,
    val filtrados: ImmutableList<Lote> = filtrarLotes(lotes.datosOrNull.orEmpty(), busqueda, especie, etapa),
    val especies: ImmutableList<String?> = especiesDe(lotes.datosOrNull.orEmpty()),
) {
    val etapas: ImmutableList<Etapa?> get() = ETAPAS

    val hayFiltros: Boolean get() = filtrosActivos > 0

    /** Búsqueda, especie y etapa cuentan como un filtro cada uno. */
    val filtrosActivos: Int get() = listOf(busqueda.isNotBlank(), especie != null, etapa != null).count { it }

    private companion object {
        val ETAPAS: ImmutableList<Etapa?> = (listOf<Etapa?>(null) + Etapa.entries).toImmutableList()
    }
}

internal fun filtrarLotes(lotes: List<Lote>, busqueda: String, especie: String?, etapa: Etapa?): ImmutableList<Lote> {
    val texto = busqueda.trim()
    return lotes.filter { l ->
        (texto.isEmpty() || l.especie.nombre.contains(texto, true) || l.codigo.contains(texto, true)) &&
            (especie == null || l.especie.nombre == especie) &&
            (etapa == null || l.etapa == etapa)
    }.toImmutableList()
}

internal fun especiesDe(lotes: List<Lote>): ImmutableList<String?> =
    (listOf<String?>(null) + lotes.map { it.especie.nombre }.distinct().sorted()).toImmutableList()

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
    private val indice: IndiceLotesLocal,
    soloLectura: Boolean = false,
) : BaseLogic<ListaLotesUiState, ListaLotesEvent>(ListaLotesUiState(soloLectura = soloLectura)) {

    override fun onEvent(evento: ListaLotesEvent) {
        when (evento) {
            ListaLotesEvent.Entrar -> cargar()
            is ListaLotesEvent.Buscar -> {
                actualizar { copy(busqueda = evento.texto) }
                refiltrar()
            }
            is ListaLotesEvent.FiltrarEspecie -> {
                actualizar { copy(especie = evento.especie) }
                refiltrar()
            }
            is ListaLotesEvent.FiltrarEtapa -> {
                actualizar { copy(etapa = evento.etapa) }
                refiltrar()
            }
            ListaLotesEvent.QuitarFiltros -> {
                actualizar { copy(busqueda = "", especie = null, etapa = null) }
                refiltrar()
            }
            is ListaLotesEvent.AbrirLote -> emitir(
                UiEffect.Navegar(AppDestination.DetalleLote(evento.loteId, soloLectura = estado.soloLectura)),
            )
            ListaLotesEvent.NuevoLote -> if (!estado.soloLectura) emitir(UiEffect.Navegar(AppDestination.RegistrarLote))
            ListaLotesEvent.Ayuda -> emitir(UiEffect.Navegar(AppDestination.Ayuda))
        }
    }

    private var filtrado: Job? = null

    private fun cargar() = lanzar {
        if (estado.lotes !is EstadoCarga.Listo) actualizar { copy(lotes = EstadoCarga.Cargando) }
        val r = obtenerLotes().aEstadoCarga()
        val lista = r.datosOrNull.orEmpty()
        // Lista, filtrado y especies llegan juntos: no hay un frame con "sin coincidencias".
        val (filtrados, especies) = withContext(Dispatchers.Default) {
            val f = estado
            filtrarLotes(lista, f.busqueda, f.especie, f.etapa) to especiesDe(lista)
        }
        actualizar { copy(lotes = r, filtrados = filtrados, especies = especies) }
        indice.recordar(lista)
    }

    /** Recalcula el filtrado fuera del hilo principal; si llega otro cambio, el anterior se cancela. */
    private fun refiltrar() {
        filtrado?.cancel()
        filtrado = viewModelScope.launch {
            val f = estado
            val filtrados = withContext(Dispatchers.Default) {
                filtrarLotes(f.lotes.datosOrNull.orEmpty(), f.busqueda, f.especie, f.etapa)
            }
            actualizar { copy(filtrados = filtrados) }
        }
    }
}
