package com.greentech.app.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.Motion

/**
 * Pestaña de la barra inferior: ícono outlined normal y relleno (filled) solo cuando está seleccionada.
 */
data class GtBottomItem(
    val etiqueta: String,
    val icono: ImageVector,
    val iconoSeleccionado: ImageVector = icono,
    /** Acción principal del rol (Escanear): su ícono va sobre una pill primaryContainer. */
    val destacado: Boolean = false,
)

/**
 * Cuenta las veces que se toca la pestaña que ya está activa. Las pantallas principales lo
 * escuchan con [AlReseleccionarPestana] para volver al inicio de su lista.
 */
val LocalReseleccionPestana = compositionLocalOf { 0 }

/** Ejecuta [accion] (p. ej. desplazarse al inicio) cada vez que se vuelve a tocar la pestaña activa. */
@Composable
fun AlReseleccionarPestana(accion: suspend () -> Unit) {
    val conteo = LocalReseleccionPestana.current
    val inicial = remember { conteo }
    LaunchedEffect(conteo) { if (conteo != inicial) accion() }
}

/** Alto de la cápsula + margen inferior (para que el contenido deje espacio). */
val EspacioBarraInferior = 64.dp + 12.dp + 8.dp

/**
 * Barra de navegación inferior flotante de vidrio: cápsula a 12 dp del borde inferior y 16 dp de
 * los laterales. Un indicador pill se desliza (resorte) al ítem activo, el ícono cambia de
 * outlined a filled con crossfade de 150 ms y hay una vibración ligera al cambiar de pestaña.
 */
@Composable
fun GtBottomBar(
    items: List<GtBottomItem>,
    seleccionado: Int,
    onSeleccion: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptica = LocalHapticFeedback.current
    Box(
        modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(start = Dimens.lg, end = Dimens.lg, bottom = Dimens.md),
        contentAlignment = Alignment.BottomCenter,
    ) {
        GlassSurface(
            Modifier.fillMaxWidth().widthIn(max = 560.dp).height(64.dp),
            shape = GtShapes.pastilla,
            elevada = true,
        ) {
            BoxWithConstraints(Modifier.fillMaxSize().padding(horizontal = Dimens.sm)) {
                val anchoItem = maxWidth / items.size.coerceAtLeast(1)
                val desplazamiento by animateDpAsState(
                    anchoItem * seleccionado.coerceAtLeast(0),
                    Motion.resorte(),
                    label = "indicadorBarra",
                )
                if (seleccionado >= 0) {
                    Box(
                        Modifier
                            .offset(x = desplazamiento)
                            .width(anchoItem)
                            .fillMaxHeight()
                            .padding(vertical = Dimens.sm, horizontal = Dimens.xxs)
                            .clip(GtShapes.pastilla)
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                    )
                }
                Row(Modifier.fillMaxSize().selectableGroup(), verticalAlignment = Alignment.CenterVertically) {
                    items.forEachIndexed { indice, item ->
                        ItemBarra(
                            item = item,
                            seleccionado = indice == seleccionado,
                            onClick = {
                                if (indice != seleccionado) haptica.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                onSeleccion(indice)
                            },
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemBarra(item: GtBottomItem, seleccionado: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val interaccion = remember { MutableInteractionSource() }
    // Sobre vidrio el texto va en onSurface (contraste garantizado); el activo, sobre su pill tonal.
    val color by animateColorAsState(
        if (seleccionado) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
        Motion.media(),
        label = "colorItem",
    )
    Column(
        modifier = modifier
            .clip(GtShapes.pastilla)
            .selectable(
                selected = seleccionado,
                onClick = onClick,
                role = Role.Tab,
                interactionSource = interaccion,
                indication = ripple(bounded = true),
            )
            .efectoPresion(interaccion),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Crossfade(seleccionado, animationSpec = Motion.tween(150), label = "iconoBarra") { activo ->
            if (item.destacado && !activo) {
                Box(
                    Modifier.size(width = Dimens.xxxl, height = Dimens.xxl).clip(GtShapes.pastilla)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(item.icono, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(Dimens.icono))
                }
            } else {
                // Misma caja de 32 dp que el ícono destacado: todas las etiquetas quedan alineadas.
                Box(Modifier.height(Dimens.xxl), contentAlignment = Alignment.Center) {
                    Icon(
                        if (activo) item.iconoSeleccionado else item.icono,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(Dimens.icono),
                    )
                }
            }
        }
        // La barra tiene alto fijo: la etiqueta crece hasta 130 % y, si no cabe a lo ancho, se reduce.
        val densidad = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(densidad.density, densidad.fontScale.coerceAtMost(1.3f))) {
            TextoAjustable(
                item.etiqueta,
                GtTexto.etiqueta,
                color = color,
                modifier = Modifier.padding(top = Dimens.xxs, start = Dimens.xxs, end = Dimens.xxs),
            )
        }
    }
}
