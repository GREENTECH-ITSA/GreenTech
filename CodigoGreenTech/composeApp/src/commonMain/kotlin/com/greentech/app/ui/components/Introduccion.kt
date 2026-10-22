package com.greentech.app.ui.components

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Abre la bienvenida de primer uso desde Ayuda o Perfil ("Ver introducción").
 * Cada panel la provee; fuera de un panel (previews) no hace nada.
 */
class ControlIntroduccion(private val alAbrir: () -> Unit = {}) {
    fun abrir() = alAbrir()
}

val LocalIntroduccion = staticCompositionLocalOf { ControlIntroduccion() }
