package com.greentech.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.greentech.app.ui.strings.Textos

/** Estado de error con mensaje claro y botón Reintentar. */
@Composable
fun ErrorState(mensaje: String, onReintentar: () -> Unit, modifier: Modifier = Modifier) {
    EmptyState(
        icono = Icons.Outlined.CloudOff,
        titulo = Textos.Comun.ERROR_GENERICO,
        mensaje = mensaje,
        modifier = modifier,
        textoAccion = Textos.Comun.REINTENTAR,
        iconoAccion = Icons.Outlined.Refresh,
        onAccion = onReintentar,
    )
}
