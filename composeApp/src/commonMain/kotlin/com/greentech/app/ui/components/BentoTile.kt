package com.greentech.app.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.theme.GtShapes

/**
 * Tamaños de mosaico bento.
 * - S: 1 columna × ~120 dp. Un dato + ícono.
 * - M: 2 columnas × ~120 dp. Dato + sparkline o acción con texto.
 * - L: 2 columnas × ~200 dp. Gráfica, gauge o resumen principal.
 */
enum class BentoSize(val columnas: Int, val altoMinimo: Dp) {
    S(1, 120.dp),
    M(2, 120.dp),
    L(2, 200.dp),
}

object BentoDefaults {
    val separacion = 12.dp
    val margen = 20.dp
    val relleno = 16.dp
}

/** 2 columnas en teléfono y 4 en tablet (ancho de ventana ≥ 600 dp). */
@Composable
fun columnasBento(): Int {
    val ancho = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() }
    return if (ancho >= 600.dp) 4 else 2
}

/**
 * Mosaico bento: etiqueta pequeña arriba, contenido protagonista y contexto debajo.
 * Si [onClick] no es nulo, todo el mosaico es tocable (ripple + micro-escala) y muestra una flecha.
 * Sin sombra: la jerarquía viene del tono de superficie (surfaceContainerLow por defecto).
 */
@Composable
fun BentoTile(
    size: BentoSize,
    modifier: Modifier = Modifier,
    etiqueta: String? = null,
    icono: ImageVector? = null,
    contexto: String? = null,
    onClick: (() -> Unit)? = null,
    colorFondo: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    colorContenido: Color = MaterialTheme.colorScheme.onSurface,
    colorEtiqueta: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    colorIcono: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    descripcion: String? = null,
    relleno: PaddingValues = PaddingValues(BentoDefaults.relleno),
    fondo: @Composable (BoxScope.() -> Unit)? = null,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    val semantica = Modifier.semantics(mergeDescendants = descripcion == null) {
        if (descripcion != null) contentDescription = descripcion
        if (onClick != null) role = Role.Button
    }
    val cuerpo: @Composable () -> Unit = {
        Box {
            fondo?.invoke(this)
            Column(Modifier.fillMaxWidth().heightIn(min = size.altoMinimo).padding(relleno)) {
                if (etiqueta != null || icono != null || onClick != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (etiqueta != null) {
                            Text(
                                etiqueta,
                                style = MaterialTheme.typography.labelMedium,
                                color = colorEtiqueta,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f),
                            )
                        } else {
                            Spacer(Modifier.weight(1f))
                        }
                        if (icono != null) {
                            Icon(icono, null, tint = colorIcono, modifier = Modifier.size(28.dp))
                        } else if (onClick != null) {
                            Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null, tint = colorIcono)
                        }
                    }
                    Spacer(Modifier.size(8.dp))
                }
                CompositionLocalProvider(LocalContentColor provides colorContenido) {
                    Column(content = contenido)
                }
                if (contexto != null) {
                    Spacer(Modifier.size(4.dp))
                    Text(contexto, style = MaterialTheme.typography.bodySmall, color = colorEtiqueta)
                }
            }
        }
    }
    if (onClick != null) {
        val interaccion = remember { MutableInteractionSource() }
        Surface(
            onClick = onClick,
            modifier = modifier.then(semantica).efectoPresion(interaccion),
            shape = GtShapes.mosaico,
            color = colorFondo,
            contentColor = colorContenido,
            interactionSource = interaccion,
        ) { cuerpo() }
    } else {
        Surface(
            modifier = modifier.then(semantica),
            shape = GtShapes.mosaico,
            color = colorFondo,
            contentColor = colorContenido,
        ) { cuerpo() }
    }
}

/**
 * Cuadrícula bento: LazyVerticalGrid de 2/4 columnas, 12 dp de separación y 20 dp de margen lateral.
 * Los mosaicos se agregan con [bento] para que ocupen el ancho según su [BentoSize].
 */
@Composable
fun BentoGrid(
    modifier: Modifier = Modifier,
    estado: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    contenido: LazyGridScope.() -> Unit,
) {
    val columnas = columnasBento()
    LazyVerticalGrid(
        columns = GridCells.Fixed(columnas),
        modifier = modifier,
        state = estado,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(BentoDefaults.separacion),
        verticalArrangement = Arrangement.spacedBy(BentoDefaults.separacion),
        content = contenido,
    )
}

/** Agrega un mosaico a la cuadrícula con el ancho de su tamaño (S = 1 columna; M y L = 2). */
fun LazyGridScope.bento(
    size: BentoSize,
    key: Any,
    contenido: @Composable LazyGridItemScope.() -> Unit,
) = item(key = key, span = { GridItemSpan(minOf(size.columnas, maxLineSpan)) }, content = contenido)

/** Elemento a lo ancho de toda la cuadrícula (títulos de sección, listas de acciones). */
fun LazyGridScope.anchoCompleto(key: Any, contenido: @Composable LazyGridItemScope.() -> Unit) =
    item(key = key, span = { GridItemSpan(maxLineSpan) }, content = contenido)
