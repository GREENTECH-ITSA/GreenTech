package com.greentech.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier

/** Fotos de observaciones: tomar una con la cámara o elegirla de la galería. */
@Stable
interface SelectorFoto {
    /** false en equipos sin cámara: solo se ofrece la galería. */
    val puedeTomarFoto: Boolean

    /** Abre la cámara. Requiere el permiso de cámara concedido. */
    fun tomarFoto()

    /** Selector de fotos del sistema (no requiere permisos). */
    fun elegirDeGaleria()
}

/** [alElegir] recibe la URI local de la foto. */
@Composable
expect fun rememberSelectorFoto(alElegir: (String) -> Unit): SelectorFoto

/** Miniatura de una foto local (recortada para llenar [modifier]). */
@Composable
expect fun MiniaturaFoto(uri: String, descripcion: String?, modifier: Modifier = Modifier)
