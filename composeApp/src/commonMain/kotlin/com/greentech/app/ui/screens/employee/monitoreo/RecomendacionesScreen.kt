package com.greentech.app.ui.screens.employee.monitoreo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.EmptyState
import com.greentech.app.ui.components.EstadoContenido
import com.greentech.app.ui.components.GtCard
import com.greentech.app.ui.components.GtCargandoCentro
import com.greentech.app.ui.components.GtScaffold
import com.greentech.app.ui.components.GtTopBar
import com.greentech.app.ui.components.IconosDominio
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.StatusChip
import com.greentech.app.ui.components.contexto
import com.greentech.app.ui.components.entradaEscalonada
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.app.ui.theme.tabular
import com.greentech.shared.domain.model.CategoriaRecomendacion
import com.greentech.shared.domain.model.Recomendacion

/** Recomendaciones: tarjetas con el ícono Lightbulb en contenedor tonal y pasos numerados. */
@Composable
fun RecomendacionesScreen(estado: RecomendacionesUiState, onEvent: (RecomendacionesEvent) -> Unit) {
    GtScaffold(
        topBar = {
            GtTopBar(
                Textos.Recomendaciones.TITULO,
                subtitulo = estado.lote?.contexto(),
                onVolver = { onEvent(RecomendacionesEvent.Volver) },
                onAyuda = { onEvent(RecomendacionesEvent.Ayuda) },
            )
        },
    ) { padding ->
        EstadoContenido(
            estado = estado.recomendaciones,
            onReintentar = { onEvent(RecomendacionesEvent.Reintentar) },
            modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding()),
            esVacio = { it.isEmpty() },
            vacio = {
                EmptyState(
                    Icons.Outlined.TaskAlt, Textos.Recomendaciones.VACIO, Textos.Recomendaciones.VACIO_DESC,
                    textoAccion = Textos.Recomendaciones.VOLVER_LOTE,
                    iconoAccion = Icons.AutoMirrored.Outlined.ArrowBack,
                    onAccion = { onEvent(RecomendacionesEvent.Volver) },
                )
            },
            cargando = { GtCargandoCentro() },
        ) { lista ->
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = Dimens.margenLateral, end = Dimens.margenLateral, top = Dimens.sm, bottom = padding.calculateBottomPadding() + Dimens.xl),
                verticalArrangement = Arrangement.spacedBy(Dimens.md),
            ) {
                item(key = "intro") {
                    Text(
                        Textos.Recomendaciones.SUBTITULO,
                        style = GtTexto.cuerpo,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                itemsIndexed(lista, key = { _, r -> r.id }) { i, r -> TarjetaRecomendacion(r, Modifier.entradaEscalonada(i)) }
            }
        }
    }
}

@Composable
private fun TarjetaRecomendacion(r: Recomendacion, modifier: Modifier = Modifier) {
    val esquema = MaterialTheme.colorScheme
    GtCard(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(Dimens.iconoLote).clip(GtShapes.campo).background(esquema.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) { Icon(IconosModulo.recomendaciones, null, tint = esquema.onSecondaryContainer) }
            Spacer(Modifier.width(Dimens.md))
            Text(r.titulo, style = GtTexto.tituloTarjeta, modifier = Modifier.weight(1f).semantics { heading() })
        }
        Spacer(Modifier.height(Dimens.md))
        val origenAnomalia = r.categoria == CategoriaRecomendacion.PLAGA || r.categoria == CategoriaRecomendacion.ENFERMEDAD
        StatusChip(
            r.origen,
            if (origenAnomalia) GreenTechTheme.colors.error else GreenTechTheme.colors.neutro,
            icono = IconosDominio.recomendacion(r.categoria),
        )
        Spacer(Modifier.height(Dimens.lg))
        Text(Textos.Recomendaciones.PASOS, style = GtTexto.etiqueta, color = esquema.onSurfaceVariant)
        Spacer(Modifier.height(Dimens.sm))
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.sm)) {
            r.pasos.forEachIndexed { i, paso ->
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        Modifier.size(Dimens.iconoMediano).clip(GtShapes.circulo).background(esquema.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("${i + 1}", style = GtTexto.etiqueta.tabular(), color = esquema.onPrimaryContainer)
                    }
                    Spacer(Modifier.width(Dimens.md))
                    Text(paso, style = GtTexto.ayuda, modifier = Modifier.weight(1f).padding(top = Dimens.xs))
                }
            }
        }
    }
}

private val estadoPreview = RecomendacionesUiState(
    EstadoCarga.Listo(
        listOf(
            Recomendacion("1", "Controlar el pulgón", CategoriaRecomendacion.PLAGA,
                listOf("Separa las plantas afectadas.", "Aplica jabón potásico."), "Por anomalía: Pulgón"),
            Recomendacion("2", "Aumentar el riego", CategoriaRecomendacion.RIEGO,
                listOf("Haz un riego ligero hoy."), "Por condición: humedad baja"),
        ),
    ),
)

@Preview
@Composable
private fun RecomendacionesPreviewClaro() {
    GreenTechTheme { RecomendacionesScreen(estadoPreview) {} }
}

@Preview
@Composable
private fun RecomendacionesPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { RecomendacionesScreen(estadoPreview) {} }
}
