package com.greentech.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.KeyEvent

/**
 * true si la plataforma puede desenfocar el fondo en tiempo real (Android 12+).
 * Si no, GlassSurface usa una superficie al 94 % sin desenfoque.
 */
expect val desenfoqueDisponible: Boolean

/** Bloq Mayús según una tecla física. null si la plataforma no lo informa (teclados en pantalla). */
expect fun KeyEvent.bloqMayusActivo(): Boolean?

/** Ajusta los íconos de las barras del sistema (edge-to-edge) al tema activo. */
@Composable
expect fun AjustarBarrasSistema(oscuro: Boolean)
