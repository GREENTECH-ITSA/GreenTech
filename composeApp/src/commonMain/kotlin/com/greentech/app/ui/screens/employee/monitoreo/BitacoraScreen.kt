package com.greentech.app.ui.screens.employee.monitoreo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterAltOff
import androidx.compose.material.icons.outlined.SearchOff
import com.greentech.app.ui.components.FiltrosActivos
import com.greentech.app.ui.components.textoConteo
import com.greentech.app.util.FechasUi
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.EmptyState
import com.greentech.app.ui.components.EstadoContenido
import com.greentech.app.ui.components.GtChipFiltro
import com.greentech.app.ui.components.GtScaffold
import com.greentech.app.ui.components.GtSegmentado
import com.greentech.app.ui.components.GtTopBar
import com.greentech.app.ui.components.IconosDominio
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.TimelineItem
import com.greentech.app.ui.components.entradaEscalonada
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.preview.DatosPreview
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.shared.domain.model.EventoBitacora
import com.greentech.shared.domain.model.TipoEvento
import com.greentech.shared.util.Fechas
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.number

/**
 * Bitácora como línea de tiempo vertical: nodos de color por tipo, tarjetas compactas y
 * separadores de fecha pegajosos ("Hoy", "Ayer", "12 sep").
 */
@Composable
fun BitacoraScreen(estado: BitacoraUiState, onEvent: (BitacoraEvent) -> Unit) {
    val subtitulo = listOfNotNull(
        estado.lote?.let { "${it.especie.nombre} · ${it.codigo}" },
        if (estado.soloLectura) Textos.Comun.SOLO_LECTURA else null,
    ).joinToString(" · ").ifEmpty { null }
    GtScaffold(
        topBar = {
            GtTopBar(Textos.Bitacora.TITULO, subtitulo = subtitulo, onVolver = { onEvent(BitacoraEvent.Volver) }, onAyuda = { onEvent(BitacoraEvent.Ayuda) })
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(top = padding.calculateTopPadding()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            GtSegmentado(
                opciones = PeriodoBitacora.entries,
                seleccionado = estado.periodo,
                onSeleccion = { onEvent(BitacoraEvent.ElegirPeriodo(it)) },
                etiqueta = { it.etiqueta },
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            GtChipFiltro(
                opciones = listOf<TipoEvento?>(null) + TipoEvento.entries,
                seleccionado = estado.tipo,
                onSeleccion = { onEvent(BitacoraEvent.ElegirTipo(it)) },
                etiqueta = { it?.etiqueta ?: "${Textos.Bitacora.TIPO}: ${Textos.Comun.TODOS.lowercase()}" },
                icono = { it?.let(IconosDominio::evento) ?: IconosModulo.bitacora },
            )
            EstadoContenido(
                estado.eventos,
                onReintentar = { onEvent(BitacoraEvent.Reintentar) },
                esVacio = { it.isEmpty() },
                vacio = { EmptyState(IconosModulo.bitacora, Textos.Bitacora.VACIO, Textos.Bitacora.VACIO_DESC) },
            ) {
                val lista = estado.filtrados
                val limpiar = { onEvent(BitacoraEvent.LimpiarFiltros) }
                if (lista.isEmpty()) {
                    EmptyState(
                        Icons.Outlined.SearchOff, Textos.Bitacora.SIN_COINCIDENCIAS, Textos.Lotes.SIN_COINCIDENCIAS_MENSAJE,
                        textoAccion = Textos.Comun.LIMPIAR_FILTROS, iconoAccion = Icons.Outlined.FilterAltOff, onAccion = limpiar,
                    )
                } else {
                    LineaDeTiempo(
                        lista,
                        PaddingValues(start = 20.dp, end = 20.dp, bottom = padding.calculateBottomPadding() + 24.dp),
                    ) {
                        FiltrosActivos(
                            resultados = textoConteo(lista.size, Textos.Bitacora.REGISTRO, Textos.Bitacora.REGISTROS),
                            filtrosActivos = estado.filtrosActivos,
                            onLimpiar = limpiar,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LineaDeTiempo(lista: List<EventoBitacora>, contentPadding: PaddingValues, encabezado: @Composable () -> Unit) {
    val grupos = remember(lista) { lista.sortedByDescending { it.fecha }.groupBy { it.fecha.date } }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = contentPadding) {
        item(key = "conteo") { encabezado() }
        var indiceGlobal = 0
        grupos.forEach { (fecha, eventos) ->
            stickyHeader(key = "fecha-$fecha") { SeparadorFecha(fecha) }
            val base = indiceGlobal
            itemsIndexed(eventos, key = { _, e -> e.id }) { i, e ->
                TimelineItem(
                    evento = e,
                    esUltimo = i == eventos.lastIndex,
                    esPrimero = i == 0,
                    soloHora = true,
                    modifier = Modifier.animateItem().entradaEscalonada(base + i),
                )
            }
            indiceGlobal += eventos.size
        }
    }
}

/** Separador pegajoso con la fecha del grupo; fondo del tema para que el texto siempre se lea. */
@Composable
private fun SeparadorFecha(fecha: LocalDate) {
    Text(
        etiquetaFecha(fecha),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 10.dp)
            .semantics { heading() },
    )
}

private fun etiquetaFecha(fecha: LocalDate): String = FechasUi.dia(fecha)

private val estadoPreview = BitacoraUiState(
    lote = DatosPreview.lote,
    eventos = EstadoCarga.Listo(DatosPreview.eventos.filter { it.loteId == DatosPreview.lote.id }),
)

@Preview
@Composable
private fun BitacoraPreviewClaro() {
    GreenTechTheme { BitacoraScreen(estadoPreview) {} }
}

@Preview
@Composable
private fun BitacoraPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { BitacoraScreen(estadoPreview.copy(soloLectura = true)) {} }
}
