package com.greentech.app.ui.screens.common

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.greentech.app.ui.components.BentoSize
import com.greentech.app.ui.components.BentoTile
import com.greentech.app.ui.components.EmblemaGreenTech
import com.greentech.app.ui.components.GtButton
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.MarcaGreenTech
import com.greentech.app.ui.components.RegistrarModal
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.strings.conValores
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.Motion
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.shared.domain.model.Rol
import kotlinx.coroutines.launch

private data class PaginaIntro(val icono: ImageVector, val titulo: String, val texto: String)

private fun paginasIntro(rol: Rol): List<PaginaIntro> = when (rol) {
    Rol.EMPLEADO -> listOf(
        PaginaIntro(IconosModulo.lote, Textos.Intro.EMPLEADO_1_TITULO, Textos.Intro.EMPLEADO_1),
        PaginaIntro(IconosModulo.escanear, Textos.Intro.EMPLEADO_2_TITULO, Textos.Intro.EMPLEADO_2),
        PaginaIntro(IconosModulo.bitacora, Textos.Intro.EMPLEADO_3_TITULO, Textos.Intro.EMPLEADO_3),
    )
    Rol.ADMINISTRADOR -> listOf(
        PaginaIntro(IconosModulo.dashboard, Textos.Intro.ADMIN_1_TITULO, Textos.Intro.ADMIN_1),
        PaginaIntro(IconosModulo.personal, Textos.Intro.ADMIN_2_TITULO, Textos.Intro.ADMIN_2),
        PaginaIntro(IconosModulo.lote, Textos.Intro.ADMIN_3_TITULO, Textos.Intro.ADMIN_3),
    )
}

/**
 * Bienvenida de primer uso: 3 tarjetas deslizables adaptadas al rol, con "Siguiente" y "Omitir".
 * Ocupa toda la pantalla sobre el panel; "atrás" equivale a omitir.
 */
@Composable
fun IntroduccionScreen(estado: IntroduccionUiState, onEvent: (IntroduccionEvent) -> Unit) {
    if (!estado.visible) return
    RegistrarModal()
    Dialog(
        onDismissRequest = { onEvent(IntroduccionEvent.Terminar) },
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = false),
    ) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            ContenidoIntro(estado.rol, onTerminar = { onEvent(IntroduccionEvent.Terminar) })
        }
    }
}

@Composable
private fun ContenidoIntro(rol: Rol, onTerminar: () -> Unit) {
    val paginas = remember(rol) { paginasIntro(rol) }
    val pager = rememberPagerState { paginas.size }
    val alcance = rememberCoroutineScope()
    val ultima = pager.currentPage == paginas.lastIndex
    Column(
        Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(horizontal = Dimens.margenLateral, vertical = Dimens.md),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(Modifier.fillMaxWidth().widthIn(max = Dimens.anchoMaximo), verticalAlignment = Alignment.CenterVertically) {
            MarcaGreenTech(tamanoEmblema = 32.dp, tamanoTexto = 20.sp)
            Spacer(Modifier.weight(1f))
            if (!ultima) {
                TextButton(onClick = onTerminar, modifier = Modifier.heightIn(min = Dimens.tactilMin)) {
                    Text(Textos.Intro.OMITIR, style = GtTexto.boton)
                }
            }
        }
        HorizontalPager(
            state = pager,
            modifier = Modifier.weight(1f).fillMaxWidth().widthIn(max = Dimens.anchoMaximo),
            pageSpacing = Dimens.lg,
        ) { i ->
            TarjetaIntro(paginas[i], numero = i + 1, total = paginas.size)
        }
        Spacer(Modifier.height(Dimens.lg))
        IndicadorPasos(pager.currentPage, paginas.size)
        Spacer(Modifier.height(Dimens.lg))
        GtButton(
            texto = if (ultima) Textos.Intro.EMPEZAR else Textos.Intro.SIGUIENTE,
            onClick = {
                if (ultima) onTerminar() else alcance.launch { pager.animateScrollToPage(pager.currentPage + 1, animationSpec = Motion.lenta()) }
            },
            modifier = Modifier.fillMaxWidth().widthIn(max = Dimens.anchoMaximo),
            icono = if (ultima) Icons.Outlined.Check else Icons.AutoMirrored.Outlined.ArrowForward,
        )
    }
}

/** Tarjeta L tonal con el emblema como marca de agua (igual que el saludo de Inicio). */
@Composable
private fun TarjetaIntro(pagina: PaginaIntro, numero: Int, total: Int) {
    val sobre = MaterialTheme.colorScheme.onPrimaryContainer
    Box(Modifier.fillMaxSize().padding(vertical = Dimens.md), contentAlignment = Alignment.Center) {
        BentoTile(
            BentoSize.L,
            modifier = Modifier.fillMaxWidth(),
            colorFondo = MaterialTheme.colorScheme.primaryContainer,
            colorContenido = sobre,
            fondo = {
                EmblemaGreenTech(
                    tamano = 220.dp,
                    descripcion = null,
                    modifier = Modifier.align(Alignment.BottomEnd).offset(x = 48.dp, y = 56.dp).alpha(0.06f),
                )
            },
        ) {
            Column(Modifier.verticalScroll(rememberScrollState()).padding(Dimens.sm), verticalArrangement = Arrangement.spacedBy(Dimens.md)) {
                Box(
                    Modifier.size(88.dp).clip(GtShapes.circulo).background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center,
                ) { Icon(pagina.icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(44.dp)) }
                Text(Textos.Intro.PASO.conValores(numero, total), style = GtTexto.boton, color = sobre)
                Text(pagina.titulo, style = GtTexto.tituloPantalla, color = sobre, modifier = Modifier.semantics { heading() })
                Text(pagina.texto, style = GtTexto.cuerpo, color = sobre)
            }
        }
    }
}

/** Puntos de avance: el actual es más ancho (no depende solo del color) y se anuncia "Paso 1 de 3". */
@Composable
private fun IndicadorPasos(actual: Int, total: Int) {
    Row(
        Modifier.semantics(mergeDescendants = true) { contentDescription = Textos.Intro.PASO.conValores(actual + 1, total) },
        horizontalArrangement = Arrangement.spacedBy(Dimens.sm),
    ) {
        repeat(total) { i ->
            val ancho by animateDpAsState(if (i == actual) 28.dp else 8.dp, Motion.resorte(), label = "paso")
            Box(
                Modifier.width(ancho).height(8.dp).clip(GtShapes.pastilla)
                    .background(if (i == actual) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
            )
        }
    }
}

@Preview
@Composable
private fun IntroduccionPreviewClaro() {
    GreenTechTheme { Surface { ContenidoIntro(Rol.EMPLEADO) {} } }
}

@Preview
@Composable
private fun IntroduccionPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { Surface { ContenidoIntro(Rol.ADMINISTRADOR) {} } }
}
