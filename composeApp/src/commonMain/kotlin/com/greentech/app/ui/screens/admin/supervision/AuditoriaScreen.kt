package com.greentech.app.ui.screens.admin.supervision

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterAltOff
import androidx.compose.material.icons.outlined.SearchOff
import com.greentech.app.ui.components.FiltrosActivos
import com.greentech.app.ui.components.textoConteo
import com.greentech.app.util.FechasUi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.EmptyState
import com.greentech.app.ui.components.EstadoContenido
import com.greentech.app.ui.components.GtCard
import com.greentech.app.ui.components.GtChipFiltro
import com.greentech.app.ui.components.GtScaffold
import com.greentech.app.ui.components.GtTopBar
import com.greentech.app.ui.components.IconosDominio
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.entradaEscalonada
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.preview.DatosPreview
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.ParColores
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.shared.domain.model.Auditoria
import com.greentech.shared.domain.model.TipoAuditoria
import com.greentech.shared.util.Fechas

/** Auditoría: lista densa pero legible, ícono por tipo de acción y filtros en chips. */
@Composable
fun AuditoriaScreen(estado: AuditoriaUiState, onEvent: (AuditoriaEvent) -> Unit) {
    GtScaffold(
        topBar = {
            GtTopBar(
                Textos.Auditoria.TITULO,
                subtitulo = Textos.Comun.SOLO_LECTURA,
                onVolver = { onEvent(AuditoriaEvent.Volver) },
                onAyuda = { onEvent(AuditoriaEvent.Ayuda) },
            )
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(top = padding.calculateTopPadding()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                Textos.Auditoria.SUBTITULO,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            GtChipFiltro(
                opciones = listOf<TipoAuditoria?>(null) + TipoAuditoria.entries,
                seleccionado = estado.tipo,
                onSeleccion = { onEvent(AuditoriaEvent.ElegirTipo(it)) },
                etiqueta = { it?.etiqueta ?: Textos.Comun.TODOS },
                margen = 20.dp,
                icono = { it?.let(IconosDominio::auditoria) ?: IconosModulo.auditoria },
            )
            EstadoContenido(
                estado.registros,
                onReintentar = { onEvent(AuditoriaEvent.Reintentar) },
                esVacio = { it.isEmpty() },
                vacio = { EmptyState(IconosModulo.auditoria, Textos.Auditoria.VACIO, Textos.Auditoria.VACIO_DESC) },
            ) {
                val lista = estado.filtrados
                val limpiar = { onEvent(AuditoriaEvent.ElegirTipo(null)) }
                if (lista.isEmpty()) {
                    EmptyState(
                        Icons.Outlined.SearchOff, Textos.Auditoria.SIN_COINCIDENCIAS, Textos.Lotes.SIN_COINCIDENCIAS_MENSAJE,
                        textoAccion = Textos.Comun.LIMPIAR_FILTROS, iconoAccion = Icons.Outlined.FilterAltOff, onAccion = limpiar,
                    )
                } else {
                    LazyColumn(
                        Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = padding.calculateBottomPadding() + 24.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        item(key = "conteo") {
                            FiltrosActivos(
                                resultados = textoConteo(lista.size, Textos.Bitacora.REGISTRO, Textos.Bitacora.REGISTROS),
                                filtrosActivos = if (estado.tipo != null) 1 else 0,
                                onLimpiar = limpiar,
                            )
                        }
                        itemsIndexed(lista, key = { _, a -> a.id }) { i, a ->
                            TarjetaAuditoria(a, Modifier.animateItem().entradaEscalonada(i))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TipoAuditoria.colores(): ParColores = when (this) {
    TipoAuditoria.VENTA -> GreenTechTheme.colors.exito
    TipoAuditoria.BAJA_PLANTAS -> GreenTechTheme.colors.advertencia
    TipoAuditoria.ALTA_USUARIO -> GreenTechTheme.colors.secundario
    TipoAuditoria.BAJA_USUARIO -> GreenTechTheme.colors.neutro
}

@Composable
private fun TarjetaAuditoria(a: Auditoria, modifier: Modifier = Modifier) {
    val colores = a.tipo.colores()
    GtCard(modifier.semantics(mergeDescendants = true) {}, relleno = 12.dp) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                Modifier.size(40.dp).clip(GtShapes.campo).background(colores.fondo),
                contentAlignment = Alignment.Center,
            ) { Icon(IconosDominio.auditoria(a.tipo), contentDescription = null, tint = colores.contenido, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(a.tipo.etiqueta, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                    Text(FechasUi.relativa(a.fecha), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(a.detalle, style = MaterialTheme.typography.bodyMedium)
                Text(a.usuario, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview
@Composable
private fun AuditoriaPreviewClaro() {
    GreenTechTheme { AuditoriaScreen(AuditoriaUiState(EstadoCarga.Listo(DatosPreview.auditoria))) {} }
}

@Preview
@Composable
private fun AuditoriaPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { AuditoriaScreen(AuditoriaUiState(EstadoCarga.Listo(DatosPreview.auditoria))) {} }
}
