package com.greentech.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GtTexto

/**
 * Clase de ancho de la ventana (umbrales de WindowSizeClass de Material: 600 y 840 dp).
 * - COMPACTO: teléfonos en vertical (desde 360 dp) → barra inferior flotante.
 * - MEDIANO: plegables abiertos, teléfonos en horizontal, tablets pequeñas → Navigation Rail.
 * - EXPANDIDO: tablets y plegables grandes en horizontal → Navigation Rail y lista-detalle de lotes.
 */
enum class ClaseAncho { COMPACTO, MEDIANO, EXPANDIDO }

@Composable
fun claseAncho(): ClaseAncho {
    val ancho = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() }
    return when {
        ancho >= 840.dp -> ClaseAncho.EXPANDIDO
        ancho >= 600.dp -> ClaseAncho.MEDIANO
        else -> ClaseAncho.COMPACTO
    }
}

/** Ancho máximo del contenido de una pantalla: en tablets no se estira de lado a lado. */
val AnchoMaximoPantalla: Dp = 840.dp

/** Centra el contenido con [AnchoMaximoPantalla] como máximo (en teléfonos ocupa todo el ancho). */
@Composable
fun ContenidoCentrado(modifier: Modifier = Modifier, contenido: @Composable BoxScope.() -> Unit) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Box(Modifier.widthIn(max = AnchoMaximoPantalla).fillMaxSize(), content = contenido)
    }
}

/**
 * Navigation Rail de los paneles en ventanas medianas y expandidas: los mismos destinos, íconos y
 * etiquetas que [GtBottomBar], con áreas táctiles de 56 dp o más.
 */
@Composable
fun GtNavigationRail(
    items: List<GtBottomItem>,
    seleccionado: Int,
    onSeleccion: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptica = LocalHapticFeedback.current
    val esquema = MaterialTheme.colorScheme
    NavigationRail(
        modifier = modifier.fillMaxHeight(),
        containerColor = esquema.surfaceContainerLow,
        windowInsets = WindowInsets.safeDrawing,
    ) {
        Spacer(Modifier.height(Dimens.lg))
        items.forEachIndexed { indice, item ->
            val activo = indice == seleccionado
            NavigationRailItem(
                selected = activo,
                onClick = {
                    if (!activo) haptica.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    onSeleccion(indice)
                },
                icon = { Icon(if (activo) item.iconoSeleccionado else item.icono, contentDescription = null) },
                label = { Text(item.etiqueta, style = GtTexto.etiqueta) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = esquema.onSecondaryContainer,
                    selectedTextColor = esquema.onSurface,
                    indicatorColor = esquema.secondaryContainer,
                    unselectedIconColor = esquema.onSurfaceVariant,
                    unselectedTextColor = esquema.onSurfaceVariant,
                ),
                modifier = Modifier.padding(vertical = Dimens.xs),
            )
        }
    }
}
