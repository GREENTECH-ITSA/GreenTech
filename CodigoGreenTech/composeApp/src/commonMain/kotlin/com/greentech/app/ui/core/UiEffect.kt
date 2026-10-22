package com.greentech.app.ui.core

import com.greentech.app.navigation.AppDestination
import com.greentech.app.ui.notification.TipoNotificacion
import com.greentech.shared.domain.model.Rol

/** Efectos de un solo uso que un Logic pide a la UI. */
sealed interface UiEffect {
    data class Notificar(val tipo: TipoNotificacion, val mensaje: String, val titulo: String? = null) : UiEffect

    /** Navega a [destino]. Con [reemplazar] la pantalla actual sale de la pila. */
    data class Navegar(val destino: AppDestination, val reemplazar: Boolean = false) : UiEffect

    data object Volver : UiEffect

    /** Entra al panel del rol y limpia la pila (atrás no regresa al login). */
    data class EntrarAlPanel(val rol: Rol) : UiEffect

    data object IrAlLogin : UiEffect
}
