package com.greentech.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Pending
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.greentech.app.ui.preview.DatosPreview
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.strings.conValores
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.app.ui.theme.tabular
import com.greentech.app.util.formatoMiles
import com.greentech.shared.domain.model.Lote

/**
 * Variantes de [LoteCard].
 * - NORMAL: listas de lotes.
 * - COMPACTA: una sola fila (listas densas, selección de lote, traslados).
 * - DESTACADA: una fila sobre primaryContainer con el corte de hoja: el lote con el que se está
 *   trabajando (encabezado de los formularios).
 */
enum class VarianteLoteCard { NORMAL, COMPACTA, DESTACADA }

/**
 * Tarjeta de lote v2. Jerarquía: especie → código (tnum) → salud → etapa → barra "X de Y plantas" →
 * última actividad. Las ranuras [tareasPendientes] y [plantasEnTransito] (Parte 2) solo se dibujan
 * cuando traen un valor mayor que cero.
 *
 * @param onClick null cuando la tarjeta es solo contexto (formularios).
 * @param ultimaActividad texto ya formateado, por ejemplo "Riego · Hoy, 10:32".
 */
@Composable
fun LoteCard(
    lote: Lote,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    variante: VarianteLoteCard = VarianteLoteCard.NORMAL,
    ultimaActividad: String? = null,
    tareasPendientes: Int? = null,
    plantasEnTransito: Int? = null,
) {
    // Textos formateados una sola vez por lote (no en cada recomposición ni durante el scroll).
    val disponibles = remember(lote.cantidadDisponible, lote.cantidadInicial) {
        Textos.Lotes.DISPONIBLES_DE.conValores(lote.cantidadDisponible.formatoMiles(), lote.cantidadInicial.formatoMiles())
    }
    val resumen = remember(lote, disponibles, ultimaActividad, tareasPendientes, plantasEnTransito) { listOfNotNull(
        lote.especie.nombre, lote.codigo, "${Textos.Disenio.SALUD}: ${lote.estadoSalud.etiqueta}", lote.etapa.etiqueta,
        disponibles, lote.zona.nombre,
        ultimaActividad?.let { "${Textos.LoteCard.ULTIMA_ACTIVIDAD}: $it" },
        tareasPendientes?.takeIf { it > 0 }?.let(::textoTareas),
        plantasEnTransito?.takeIf { it > 0 }?.let { Textos.LoteCard.PLANTAS_EN_TRANSITO.conValores(it) },
    ).joinToString(", ") }
    val destacada = variante == VarianteLoteCard.DESTACADA
    val esquema = MaterialTheme.colorScheme
    ContenedorLote(
        onClick = onClick,
        forma = if (destacada) GtShapes.hojaCorte else GtShapes.tarjeta,
        fondo = if (destacada) esquema.primaryContainer else esquema.surfaceContainerLow,
        modifier = modifier.semantics(mergeDescendants = true) {
            contentDescription = resumen
            if (onClick != null) role = Role.Button
        },
    ) {
        val tinta = if (destacada) esquema.onPrimaryContainer else esquema.onSurface
        val tintaSecundaria = if (destacada) esquema.onPrimaryContainer else esquema.onSurfaceVariant
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(Dimens.iconoLote)
                    .clip(if (destacada) GtShapes.hojaCorteIcono else GtShapes.campo)
                    .background(if (destacada) esquema.surface else esquema.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(IconosDominio.etapa(lote.etapa), null, tint = if (destacada) esquema.primary else esquema.onPrimaryContainer)
            }
            Column(Modifier.weight(1f).padding(horizontal = Dimens.md)) {
                Text(lote.especie.nombre, style = GtTexto.tituloTarjeta, color = tinta, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    if (destacada) "${lote.codigo} · ${lote.zona.nombre}" else lote.codigo,
                    style = GtTexto.ayuda.tabular(),
                    color = tintaSecundaria,
                )
            }
            when (variante) {
                VarianteLoteCard.COMPACTA -> PlantHealthIndicator(lote.estadoSalud, variante = VarianteSalud.PUNTO)
                // Sobre primaryContainer el chip lleva su propio fondo: el contraste no depende del contenedor.
                VarianteLoteCard.DESTACADA -> PlantHealthIndicator(lote.estadoSalud)
                VarianteLoteCard.NORMAL -> Unit
            }
            if (onClick != null) {
                Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null, tint = tintaSecundaria)
            }
        }
        if (variante != VarianteLoteCard.NORMAL) return@ContenedorLote

        Spacer(Modifier.height(Dimens.md))
        // Salud primero (chip tonal); la etapa, ya representada por el ícono, va como texto.
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Dimens.md),
            verticalArrangement = Arrangement.spacedBy(Dimens.sm),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            PlantHealthIndicator(lote.estadoSalud)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(IconosDominio.etapa(lote.etapa), null, tint = tintaSecundaria, modifier = Modifier.size(Dimens.iconoChico))
                Spacer(Modifier.width(Dimens.xs))
                Text(lote.etapa.etiqueta, style = GtTexto.etiqueta, color = tintaSecundaria)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Place, null, tint = tintaSecundaria, modifier = Modifier.size(Dimens.iconoChico))
                Spacer(Modifier.width(Dimens.xs))
                Text(lote.zona.nombre, style = GtTexto.etiqueta, color = tintaSecundaria)
            }
        }
        Spacer(Modifier.height(Dimens.md))
        Text(disponibles, style = GtTexto.ayuda.tabular(), color = tinta)
        Spacer(Modifier.height(Dimens.sm))
        BarraDisponibilidad(lote.proporcionDisponible)
        if (ultimaActividad != null) {
            Spacer(Modifier.height(Dimens.md))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(IconosModulo.bitacora, null, tint = tintaSecundaria, modifier = Modifier.size(Dimens.iconoChico))
                Spacer(Modifier.width(Dimens.iconoTexto))
                Text(
                    ultimaActividad, style = GtTexto.ayuda, color = tintaSecundaria,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
            }
        }
        IndicadoresLote(tareasPendientes, plantasEnTransito)
    }
}

/** Ranura de la Parte 2: "N tareas pendientes" y "N plantas en tránsito" como insignias con ícono. */
@Composable
private fun ColumnScope.IndicadoresLote(tareas: Int?, transito: Int?) {
    val hayTareas = tareas != null && tareas > 0
    val hayTransito = transito != null && transito > 0
    if (!hayTareas && !hayTransito) return
    Spacer(Modifier.height(Dimens.md))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(Dimens.sm), verticalArrangement = Arrangement.spacedBy(Dimens.sm)) {
        if (hayTareas) StatusChip(textoTareas(tareas), GreenTechTheme.colors.tarea.pendiente, icono = Icons.Outlined.Pending)
        if (hayTransito) {
            StatusChip(
                Textos.LoteCard.PLANTAS_EN_TRANSITO.conValores(transito),
                GreenTechTheme.colors.traslado.enTransito,
                icono = Icons.Outlined.LocalShipping,
            )
        }
    }
}

private fun textoTareas(n: Int): String =
    if (n == 1) Textos.LoteCard.TAREA_PENDIENTE else Textos.LoteCard.TAREAS_PENDIENTES.conValores(n)

@Composable
private fun ContenedorLote(
    onClick: (() -> Unit)?,
    forma: Shape,
    fondo: Color,
    modifier: Modifier,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    val cuerpo: @Composable () -> Unit = {
        Column(Modifier.heightIn(min = Dimens.tactilMin).padding(Dimens.rellenoTarjeta), content = contenido)
    }
    if (onClick != null) {
        val interaccion = remember { MutableInteractionSource() }
        Surface(
            onClick = onClick,
            modifier = modifier.fillMaxWidth().efectoPresion(interaccion).anilloFoco(interaccion, forma),
            shape = forma,
            color = fondo,
            interactionSource = interaccion,
        ) { cuerpo() }
    } else {
        Surface(modifier = modifier.fillMaxWidth(), shape = forma, color = fondo) { cuerpo() }
    }
}

/** Barra de disponibilidad (plantas disponibles / iniciales), con extremos redondeados. */
@Composable
fun BarraDisponibilidad(proporcion: Float, modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxWidth().height(Dimens.barraProgreso).clip(GtShapes.pastilla)
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
    ) {
        Box(
            Modifier.fillMaxWidth(proporcion.coerceIn(0f, 1f)).height(Dimens.barraProgreso).clip(GtShapes.pastilla)
                .background(MaterialTheme.colorScheme.primary),
        )
    }
}

@Composable
private fun MuestraLoteCard() {
    Column(
        Modifier.background(MaterialTheme.colorScheme.background).padding(Dimens.margenLateral),
        verticalArrangement = Arrangement.spacedBy(Dimens.separacionTarjetas),
    ) {
        val lotes = DatosPreview.lotes
        LoteCard(lotes[0], onClick = {}, ultimaActividad = "Riego manual · Hoy, 10:32", tareasPendientes = 2, plantasEnTransito = 40)
        LoteCard(lotes[1], onClick = {}, variante = VarianteLoteCard.COMPACTA)
        LoteCard(lotes[2], onClick = null, variante = VarianteLoteCard.DESTACADA)
    }
}

@Preview
@Composable
private fun LoteCardPreviewClaro() {
    GreenTechTheme { MuestraLoteCard() }
}

@Preview
@Composable
private fun LoteCardPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { MuestraLoteCard() }
}
