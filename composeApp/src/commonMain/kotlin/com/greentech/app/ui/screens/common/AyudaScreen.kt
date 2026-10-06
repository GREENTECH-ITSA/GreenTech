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
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Close
import com.greentech.app.ui.components.LocalIntroduccion
import com.greentech.app.ui.strings.conValores
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
import com.greentech.app.ui.components.SectionHeader
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes
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
                start = 20.dp, end = 20.dp,
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 24.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
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
                        Text(p.respuesta, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun Encabezado() {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        EmblemaGreenTech(tamano = 64.dp)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(Textos.Ayuda.TITULO, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.semantics { heading() })
            Text(Textos.Ayuda.SUBTITULO, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            textos.forEachIndexed { i, t -> PasoNumerado(i + 1, t, enContenedor = true) }
        }
    }
}

@Composable
private fun PasoNumerado(numero: Int, texto: String, enContenedor: Boolean = false) {
    val esquema = MaterialTheme.colorScheme
    Row(verticalAlignment = Alignment.Top) {
        Box(
            Modifier.size(28.dp).clip(GtShapes.circulo).background(if (enContenedor) esquema.surface else esquema.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "$numero",
                style = MaterialTheme.typography.labelLarge.tabular(),
                color = if (enContenedor) esquema.onSurface else esquema.onPrimaryContainer,
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            texto,
            style = MaterialTheme.typography.bodyLarge,
            color = if (enContenedor) esquema.onPrimaryContainer else esquema.onSurface,
            modifier = Modifier.weight(1f).padding(top = 2.dp),
        )
    }
}

@Composable
private fun TarjetaModulo(modulo: ModuloAyuda, abierto: Boolean, onAlternar: () -> Unit, onIr: () -> Unit, modifier: Modifier) {
    Acordeon(modulo.titulo, modulo.icono, abierto, onAlternar, modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Bloque(Textos.Ayuda.QUE_HACE, modulo.queHace)
            Bloque(Textos.Ayuda.CUANDO, modulo.cuando)
            Text(Textos.Ayuda.PASOS, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        Text(titulo, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(texto, style = MaterialTheme.typography.bodyLarge)
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
                    Modifier.heightIn(min = 64.dp).padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier.size(44.dp).clip(GtShapes.campo).background(esquema.secondaryContainer),
                        contentAlignment = Alignment.Center,
                    ) { Icon(icono, null, tint = esquema.onSecondaryContainer) }
                    Spacer(Modifier.width(12.dp))
                    Text(titulo, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    Icon(Icons.Outlined.ExpandMore, null, tint = esquema.primary, modifier = Modifier.rotate(giro))
                }
            }
            if (abierto) {
                Box(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) { contenido() }
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
