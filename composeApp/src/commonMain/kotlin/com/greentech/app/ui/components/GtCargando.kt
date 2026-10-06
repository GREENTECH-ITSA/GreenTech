package com.greentech.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Motion

/**
 * Indicador de carga expresivo de Material 3 (formas que se transforman).
 * Con "Reducir animaciones" se muestra la forma quieta, sin bucle.
 */
@Composable
fun GtCargando(modifier: Modifier = Modifier, tamano: Dp = 48.dp, color: Color = MaterialTheme.colorScheme.primary) {
    val mod = modifier.size(tamano).semantics { contentDescription = Textos.Comun.CARGANDO }
    if (Motion.reducir) {
        LoadingIndicator(progress = { 0.6f }, modifier = mod, color = color)
    } else {
        LoadingIndicator(modifier = mod, color = color)
    }
}

/** Carga a pantalla completa (centrada) para contenidos que no son listas. */
@Composable
fun GtCargandoCentro(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().padding(vertical = 48.dp), contentAlignment = Alignment.Center) { GtCargando() }
}

/**
 * Señales de un solo uso que la pantalla recibe de su Logic (vía Conectar): el instante del
 * último éxito y del último error notificados. Sirven para micro-interacciones como el check
 * del botón de guardar o la sacudida de los campos con error.
 */
@Stable
class Retroalimentacion {
    var ultimoExito by mutableLongStateOf(0L)
    var ultimoError by mutableLongStateOf(0L)
}

val LocalRetroalimentacion = staticCompositionLocalOf { Retroalimentacion() }
