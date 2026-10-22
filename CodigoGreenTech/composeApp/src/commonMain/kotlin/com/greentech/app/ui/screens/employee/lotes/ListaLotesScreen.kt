package com.greentech.app.ui.screens.employee.lotes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FilterAltOff
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.greentech.app.ui.components.AlReseleccionarPestana
import com.greentech.app.ui.components.BotonLeyendaSalud
import com.greentech.app.ui.components.EmptyState
import com.greentech.app.ui.components.EstadoContenido
import com.greentech.app.ui.components.FiltrosActivos
import com.greentech.app.ui.components.GtBuscador
import com.greentech.app.ui.components.GtChipFiltro
import com.greentech.app.ui.components.GtScaffold
import com.greentech.app.ui.components.GtTopBar
import com.greentech.app.ui.components.IconosDominio
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.LoteCard
import com.greentech.app.ui.components.entradaEscalonada
import com.greentech.app.ui.components.textoConteo
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.preview.DatosPreview
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.strings.conValores
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.ThemeMode

/**
 * Lista de lotes: buscador tipo SearchBar, chips de filtro con scroll horizontal, conteo con
 * "Limpiar filtros" y tarjetas limpias. Tocar otra vez la pestaña sube al inicio de la lista.
 */
@Composable
fun ListaLotesScreen(estado: ListaLotesUiState, onEvent: (ListaLotesEvent) -> Unit) {
    LaunchedEffect(Unit) { onEvent(ListaLotesEvent.Entrar) }
    val lista = rememberLazyListState()
    AlReseleccionarPestana { lista.animateScrollToItem(0) }
    GtScaffold(
        topBar = {
            GtTopBar(
                titulo = if (estado.soloLectura) Textos.Lotes.TITULO_SUPERVISION else Textos.Lotes.TITULO,
                subtitulo = if (estado.soloLectura) Textos.Comun.SOLO_LECTURA else null,
                onAyuda = { onEvent(ListaLotesEvent.Ayuda) },
                principal = true,
            )
        },
        botonFlotante = {
            if (!estado.soloLectura) {
                ExtendedFloatingActionButton(
                    onClick = { onEvent(ListaLotesEvent.NuevoLote) },
                    icon = { Icon(Icons.Outlined.Add, null) },
                    text = { Text(Textos.Lotes.NUEVO, style = GtTexto.boton) },
                    shape = GtShapes.campo,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.heightIn(min = Dimens.botonAlto),
                )
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
            FiltrosLotes(estado, onEvent)
            EstadoContenido(
                estado = estado.lotes,
                onReintentar = { onEvent(ListaLotesEvent.Entrar) },
                esVacio = { it.isEmpty() },
                vacio = {
                    EmptyState(
                        icono = IconosModulo.lote,
                        titulo = Textos.Lotes.VACIO_TITULO,
                        mensaje = if (estado.soloLectura) Textos.Lotes.VACIO_MENSAJE_SUPERVISION else Textos.Lotes.VACIO_MENSAJE,
                        textoAccion = if (estado.soloLectura) null else Textos.Lotes.REGISTRAR_PRIMERO,
                        iconoAccion = Icons.Outlined.Add,
                        onAccion = { onEvent(ListaLotesEvent.NuevoLote) },
                    )
                },
            ) {
                val filtrados = estado.filtrados
                if (filtrados.isEmpty()) {
                    EmptyState(
                        icono = Icons.Outlined.SearchOff,
                        titulo = if (estado.busqueda.isNotBlank()) {
                            Textos.Lotes.SIN_COINCIDENCIAS_BUSQUEDA.conValores(estado.busqueda.trim())
                        } else {
                            Textos.Lotes.SIN_COINCIDENCIAS_FILTROS
                        },
                        mensaje = Textos.Lotes.SIN_COINCIDENCIAS_MENSAJE,
                        textoAccion = Textos.Comun.LIMPIAR_FILTROS,
                        iconoAccion = Icons.Outlined.FilterAltOff,
                        onAccion = { onEvent(ListaLotesEvent.QuitarFiltros) },
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = lista,
                        contentPadding = PaddingValues(
                            start = Dimens.margenLateral, end = Dimens.margenLateral,
                            // Espacio para el FAB y la barra inferior flotante.
                            bottom = padding.calculateBottomPadding() + Dimens.espacioFab,
                        ),
                        verticalArrangement = Arrangement.spacedBy(Dimens.md),
                    ) {
                        item(key = "conteo", contentType = "conteo") {
                            FiltrosActivos(
                                resultados = textoConteo(filtrados.size, Textos.Lotes.RESULTADO, Textos.Lotes.RESULTADOS),
                                filtrosActivos = estado.filtrosActivos,
                                onLimpiar = { onEvent(ListaLotesEvent.QuitarFiltros) },
                                // La salud aparece en cada tarjeta: su leyenda queda a un toque.
                                accion = { BotonLeyendaSalud() },
                            )
                        }
                        itemsIndexed(filtrados, key = { _, lote -> lote.id }, contentType = { _, _ -> "filtrados" }) { i, lote ->
                            LoteCard(
                                lote = lote,
                                onClick = { onEvent(ListaLotesEvent.AbrirLote(lote.id)) },
                                modifier = Modifier.animateItem().entradaEscalonada(i),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FiltrosLotes(estado: ListaLotesUiState, onEvent: (ListaLotesEvent) -> Unit) {
    Column(Modifier.padding(bottom = Dimens.xs), verticalArrangement = Arrangement.spacedBy(Dimens.sm)) {
        GtBuscador(
            valor = estado.busqueda,
            onValorChange = { onEvent(ListaLotesEvent.Buscar(it)) },
            placeholder = Textos.Lotes.BUSCAR,
            modifier = Modifier.padding(horizontal = Dimens.margenLateral),
        )
        GtChipFiltro(
            opciones = estado.especies,
            seleccionado = estado.especie,
            onSeleccion = { onEvent(ListaLotesEvent.FiltrarEspecie(it)) },
            etiqueta = { it ?: "${Textos.Lotes.ESPECIE}: ${Textos.Comun.TODAS.lowercase()}" },
        )
        GtChipFiltro(
            opciones = estado.etapas,
            seleccionado = estado.etapa,
            onSeleccion = { onEvent(ListaLotesEvent.FiltrarEtapa(it)) },
            etiqueta = { it?.etiqueta ?: "${Textos.Lotes.ETAPA}: ${Textos.Comun.TODAS.lowercase()}" },
            icono = { it?.let(IconosDominio::etapa) ?: IconosModulo.etapa },
        )
    }
}

@Preview
@Composable
private fun ListaLotesPreviewClaro() {
    GreenTechTheme { ListaLotesScreen(ListaLotesUiState(lotes = EstadoCarga.Listo(DatosPreview.lotes))) {} }
}

@Preview
@Composable
private fun ListaLotesPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) {
        ListaLotesScreen(ListaLotesUiState(lotes = EstadoCarga.Listo(DatosPreview.lotes), busqueda = "rosa", especie = "Agave")) {}
    }
}
