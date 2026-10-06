package com.greentech.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.KeyEvent

actual val desenfoqueDisponible: Boolean = true

/** iOS no expone el estado de Bloq Mayús en los eventos de tecla de Compose. */
actual fun KeyEvent.bloqMayusActivo(): Boolean? = null

/** En iOS Compose dibuja de borde a borde; el estilo de la barra lo decide UIKit según el fondo. */
@Composable
actual fun AjustarBarrasSistema(oscuro: Boolean) = Unit
