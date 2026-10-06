package com.greentech.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Motion

/**
 * Barra superior: transparente arriba y vidrio cuando el contenido se desplaza.
 * Botón de volver opcional y el de Ayuda siempre visible (íconos outlined de 56 dp).
 *
 * Patrón por tipo de pantalla:
 * - Pestaña principal: [principal] = true (título grande, sin volver).
 * - Detalle: flecha atrás + [subtitulo] de contexto (ej. "Agave · GT-8F2A91C0D4").
 * - Formulario: [cerrar] = true (X en lugar de flecha).
 *
 * El título se anuncia al lector de pantalla al entrar (paneTitle).
 */
@Composable
fun GtTopBar(
    titulo: String,
    onAyuda: (() -> Unit)?,
    modifier: Modifier = Modifier,
    onVolver: (() -> Unit)? = null,
    subtitulo: String? = null,
    principal: Boolean = false,
    cerrar: Boolean = false,
    acciones: @Composable RowScope.() -> Unit = {},
) {
    BarraConVidrio(modifier.semantics { paneTitle = titulo }) {
        TopAppBar(
            title = {
                Column {
                    Text(
                        titulo,
                        style = if (principal) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.semantics { heading() },
                    )
                    if (subtitulo != null) {
                        Text(
                            subtitulo,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            },
            navigationIcon = {
                if (onVolver != null) {
                    IconButton(onClick = onVolver, modifier = Modifier.size(56.dp)) {
                        Icon(
                            if (cerrar) Icons.Outlined.Close else Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = if (cerrar) Textos.Comun.CERRAR else Textos.Comun.VOLVER,
                        )
                    }
                }
            },
            actions = {
                acciones()
                if (onAyuda != null) BotonAyuda(onAyuda)
            },
            colors = coloresBarra(),
        )
    }
}

/** Barra de los paneles: emblema + logotipo GreenTech y Ayuda. */
@Composable
fun GtPanelTopBar(onAyuda: () -> Unit, modifier: Modifier = Modifier, acciones: @Composable RowScope.() -> Unit = {}) {
    BarraConVidrio(modifier) {
        TopAppBar(
            title = { MarcaGreenTech(tamanoEmblema = 32.dp, tamanoTexto = 20.sp) },
            actions = {
                acciones()
                BotonAyuda(onAyuda)
            },
            colors = coloresBarra(),
        )
    }
}

@Composable
private fun coloresBarra() = TopAppBarDefaults.topAppBarColors(
    containerColor = Color.Transparent,
    scrolledContainerColor = Color.Transparent,
    titleContentColor = MaterialTheme.colorScheme.onSurface,
    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
    actionIconContentColor = MaterialTheme.colorScheme.primary,
)

/** Fondo de la barra: nada arriba; vidrio (con fade de 200 ms) cuando el contenido pasa por debajo. */
@Composable
private fun BarraConVidrio(modifier: Modifier, barra: @Composable () -> Unit) {
    val desplazada = LocalBarraDesplazada.current
    val alfa by animateFloatAsState(if (desplazada) 1f else 0f, Motion.media(), label = "vidrioBarra")
    Box(modifier) {
        if (alfa > 0f) {
            GlassSurface(Modifier.matchParentSize().graphicsLayer { alpha = alfa }, conBorde = false) {}
        }
        barra()
    }
}

@Composable
private fun BotonAyuda(onAyuda: () -> Unit) {
    IconButton(onClick = onAyuda, modifier = Modifier.size(56.dp)) {
        Icon(
            Icons.AutoMirrored.Outlined.HelpOutline,
            contentDescription = Textos.Comun.AYUDA,
            modifier = Modifier.size(28.dp),
        )
    }
}
