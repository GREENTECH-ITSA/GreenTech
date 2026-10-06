package com.greentech.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.GtShapes

/** Explicación breve (2–3 líneas) de una sección o un indicador. */
data class InfoAyuda(val titulo: String, val texto: String)

/**
 * Ícono Info outlined (área táctil de 56 dp) junto al título de una sección compleja. Abre una
 * hoja corta con la explicación y, si hay [onVerAyuda], el enlace "Ver más en Ayuda".
 */
@Composable
fun BotonInfo(
    info: InfoAyuda,
    onVerAyuda: (() -> Unit)?,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    var abierto by rememberSaveable(info.titulo) { mutableStateOf(false) }
    IconButton(onClick = { abierto = true }, modifier = modifier.size(56.dp)) {
        Icon(Icons.Outlined.Info, contentDescription = "${Textos.Comun.MAS_INFO}: ${info.titulo}", tint = color)
    }
    if (abierto) {
        HojaInfo(
            info = info,
            onCerrar = { abierto = false },
            onVerAyuda = onVerAyuda?.let { ir ->
                {
                    abierto = false
                    ir()
                }
            },
        )
    }
}

@Composable
fun HojaInfo(info: InfoAyuda, onCerrar: () -> Unit, onVerAyuda: (() -> Unit)?) {
    GtBottomSheet(onCerrar = onCerrar) {
        Column(Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(48.dp).clip(GtShapes.circulo).background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) { Icon(Icons.Outlined.Info, null, tint = MaterialTheme.colorScheme.onPrimaryContainer) }
                Spacer(Modifier.width(12.dp))
                Text(info.titulo, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f).semantics { heading() })
            }
            Text(info.texto, style = MaterialTheme.typography.bodyLarge)
            GtButton(Textos.Comun.ENTENDIDO, onCerrar, variante = GtButtonVariant.Tonal)
            if (onVerAyuda != null) {
                GtButton(
                    Textos.Comun.VER_MAS_AYUDA, onVerAyuda,
                    variante = GtButtonVariant.Texto, icono = Icons.AutoMirrored.Outlined.ArrowForward,
                )
            }
        }
    }
}
