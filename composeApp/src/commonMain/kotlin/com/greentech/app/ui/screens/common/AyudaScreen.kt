package com.greentech.app.ui.screens.common

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.BentoSize
import com.greentech.app.ui.components.BentoTile
import com.greentech.app.ui.components.EmblemaGreenTech
import com.greentech.app.ui.components.EmptyState
import com.greentech.app.ui.components.GtBuscador
import com.greentech.app.ui.components.GtButton
import com.greentech.app.ui.components.GtButtonVariant
import com.greentech.app.ui.components.GtScaffold
import com.greentech.app.ui.components.GtTopBar
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.LocalIntroduccion
import com.greentech.app.ui.components.SectionHeader
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.strings.conValores
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.Motion
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.app.ui.theme.tabular
import com.greentech.shared.domain.model.Rol

/**
 * Centro de ayuda: encabezado con el emblema, buscador, "Primeros pasos" como mosaico L,
 * módulos en acordeones con su ícono y las preguntas frecuentes al final.
 */
@Composable
fun AyudaScreen(estado: AyudaUiState, onEvent: (AyudaEvent) -> Unit) {
    val intro = LocalIntroduccion.current
    GtScaffold(topBar = { GtTopBar(Textos.Ayuda.TITULO, onAyuda = null, onVolver = { onEvent(AyudaEvent.Volver) }) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = Dimens.margenLateral, end = Dimens.margenLateral,
                top = padding.calculateTopPadding() + Dimens.sm,
                bottom = padding.calculateBottomPadding() + Dimens.xl,
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.md),
        ) {
            item(key = "encabezado") { Encabezado() }
            if (estado.mostrarPrimerosPasos) {
                item(key = "intro") {
                    GtButton(
                        Textos.Ayuda.VER_INTRO, { intro.abrir() },
                        variante = GtButtonVariant.Tonal, icono = Icons.Outlined.AutoStories,
                    )
                }
            }
            item(key = "buscar") {
                GtBuscador(estado.busqueda, { onEvent(AyudaEvent.Buscar(it)) }, placeholder = Textos.Ayuda.BUSCAR)
            }
            if (estado.mostrarPrimerosPasos) {
                item(key = "primeros") { PrimerosPasos(estado.contenido.primerosPasos) }
            }
            if (estado.sinResultados) {
                item(key = "vacio") {
                    EmptyState(
                        Icons.Outlined.SearchOff,
                        Textos.Ayuda.SIN_RESULTADOS.conValores(estado.busqueda.trim()),
                        Textos.Ayuda.SIN_RESULTADOS_DESC,
                        textoAccion = Textos.Comun.LIMPIAR_BUSQUEDA,
                        iconoAccion = Icons.Outlined.Close,
                        onAccion = { onEvent(AyudaEvent.Buscar("")) },
                    )
                }
            }
            if (estado.modulos.isNotEmpty()) {
                item(key = "tituloModulos") { SectionHeader(Textos.Ayuda.MODULOS) }
                items(estado.modulos, key = { "m-${it.id}" }) { m ->
                    TarjetaModulo(
                        modulo = m,
                        abierto = m.id in estado.expandidos || estado.busqueda.isNotBlank(),
                        onAlternar = { onEvent(AyudaEvent.Alternar(m.id)) },
                        onIr = { m.destino?.let { onEvent(AyudaEvent.IrAModulo(it)) } },
                        modifier = Modifier.animateItem(),
                    )
                }
            }
            if (estado.preguntas.isNotEmpty()) {
                item(key = "tituloFaq") { SectionHeader(Textos.Ayuda.FAQ) }
                items(estado.preguntas, key = { "q-${it.id}" }) { p ->
                    Acordeon(
                        titulo = p.pregunta,
                        icono = IconosModulo.ayuda,
                        abierto = p.id in estado.expandidos || estado.busqueda.isNotBlank(),
                        onAlternar = { onEvent(AyudaEvent.Alternar(p.id)) },
                        modifier = Modifier.animateItem(),
                    ) {
                        Text(p.respuesta, style = GtTexto.cuerpo)
                    }
                }
            }
        }
    }
}

@Composable
private fun Encabezado() {
    Row(Modifier.fillMaxWidth().padding(vertical = Dimens.xs), verticalAlignment = Alignment.CenterVertically) {
        EmblemaGreenTech(tamano = 64.dp)
        Spacer(Modifier.width(Dimens.md))
        Column {
            Text(Textos.Ayuda.TITULO, style = GtTexto.tituloPantalla, modifier = Modifier.semantics { heading() })
            Text(Textos.Ayuda.SUBTITULO, style = GtTexto.ayuda, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** "Primeros pasos" como mosaico L en primaryContainer. */
@Composable
private fun PrimerosPasos(textos: List<String>) {
    val sobre = MaterialTheme.colorScheme.onPrimaryContainer
    BentoTile(
        BentoSize.L,
        etiqueta = Textos.Ayuda.PRIMEROS_PASOS,
        icono = IconosModulo.lote,
        colorFondo = MaterialTheme.colorScheme.primaryContainer,
        colorContenido = sobre,
        colorEtiqueta = sobre,
        colorIcono = sobre,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.md)) {
            textos.forEachIndexed { i, t -> PasoNumerado(i + 1, t, enContenedor = true) }
        }
    }
}

@Composable
private fun PasoNumerado(numero: Int, texto: String, enContenedor: Boolean = false) {
    val esquema = MaterialTheme.colorScheme
    Row(verticalAlignment = Alignment.Top) {
        Box(
            Modifier.size(Dimens.iconoMediano).clip(GtShapes.circulo).background(if (enContenedor) esquema.surface else esquema.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "$numero",
                style = GtTexto.etiqueta.tabular(),
                color = if (enContenedor) esquema.onSurface else esquema.onPrimaryContainer,
            )
        }
        Spacer(Modifier.width(Dimens.md))
        Text(
            texto,
            style = GtTexto.cuerpo,
            color = if (enContenedor) esquema.onPrimaryContainer else esquema.onSurface,
            modifier = Modifier.weight(1f).padding(top = Dimens.xxs),
        )
    }
}

@Composable
private fun TarjetaModulo(modulo: ModuloAyuda, abierto: Boolean, onAlternar: () -> Unit, onIr: () -> Unit, modifier: Modifier) {
    Acordeon(modulo.titulo, modulo.icono, abierto, onAlternar, modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.md)) {
            Bloque(Textos.Ayuda.QUE_HACE, modulo.queHace)
            Bloque(Textos.Ayuda.CUANDO, modulo.cuando)
            Text(Textos.Ayuda.PASOS, style = GtTexto.etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant)
            modulo.pasos.forEachIndexed { i, p -> PasoNumerado(i + 1, p) }
            if (modulo.destino != null) {
                GtButton(Textos.Ayuda.IR, onIr, variante = GtButtonVariant.Tonal, icono = Icons.AutoMirrored.Outlined.ArrowForward)
            }
        }
    }
}

@Composable
private fun Bloque(titulo: String, texto: String) {
    Column {
        Text(titulo, style = GtTexto.etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(texto, style = GtTexto.cuerpo)
    }
}

/** Acordeón: se expande con animateContentSize y la flecha gira 180°. */
@Composable
private fun Acordeon(
    titulo: String,
    icono: ImageVector,
    abierto: Boolean,
    onAlternar: () -> Unit,
    modifier: Modifier = Modifier,
    contenido: @Composable () -> Unit,
) {
    val giro by animateFloatAsState(if (abierto) 180f else 0f, Motion.resorte(), label = "flecha")
    val esquema = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = GtShapes.tarjeta,
        color = esquema.surfaceContainerLow,
    ) {
        Column(Modifier.animateContentSize(Motion.resorte())) {
            Surface(
                onClick = onAlternar,
                color = esquema.surfaceContainerLow,
                modifier = Modifier.fillMaxWidth().semantics {
                    stateDescription = if (abierto) Textos.Ayuda.CONTRAER else Textos.Ayuda.EXPANDIR
                },
            ) {
                Row(
                    Modifier.heightIn(min = Dimens.filaLista).padding(horizontal = Dimens.lg, vertical = Dimens.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier.size(Dimens.circuloIcono).clip(GtShapes.campo).background(esquema.secondaryContainer),
                        contentAlignment = Alignment.Center,
                    ) { Icon(icono, null, tint = esquema.onSecondaryContainer) }
                    Spacer(Modifier.width(Dimens.md))
                    Text(titulo, style = GtTexto.tituloTarjeta, modifier = Modifier.weight(1f))
                    Icon(Icons.Outlined.ExpandMore, null, tint = esquema.primary, modifier = Modifier.rotate(giro))
                }
            }
            if (abierto) {
                Box(Modifier.padding(start = Dimens.lg, end = Dimens.lg, bottom = Dimens.lg)) { contenido() }
            }
        }
    }
}

private val estadoPreview = AyudaUiState(contenido = AyudaContent.para(Rol.EMPLEADO), expandidos = setOf("registrar"))

@Preview
@Composable
private fun AyudaPreviewClaro() {
    GreenTechTheme { AyudaScreen(estadoPreview) {} }
}

@Preview
@Composable
private fun AyudaPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { AyudaScreen(AyudaUiState(contenido = AyudaContent.para(Rol.ADMINISTRADOR))) {} }
}
