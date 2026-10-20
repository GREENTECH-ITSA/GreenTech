package com.greentech.app.ui.screens.common

import androidx.compose.runtime.Immutable
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.PreferenciasUso
import com.greentech.shared.domain.model.Rol
import com.greentech.shared.domain.usecase.ObtenerSesionUseCase

@Immutable
data class IntroduccionUiState(
    val rol: Rol = Rol.EMPLEADO,
    val visible: Boolean = false,
)

sealed interface IntroduccionEvent {
    /** Desde Ayuda o Perfil → "Ver introducción". */
    data object Abrir : IntroduccionEvent

    /** "Omitir" o el último "Empezar": se recuerda que este usuario ya la vio. */
    data object Terminar : IntroduccionEvent
}

/** Bienvenida de primer uso: se muestra una sola vez por usuario y se puede volver a abrir. */
class IntroduccionLogic(
    sesion: ObtenerSesionUseCase,
    private val preferencias: PreferenciasUso,
) : BaseLogic<IntroduccionUiState, IntroduccionEvent>(IntroduccionUiState()) {

    private val usuarioId = sesion.actual()?.id

    init {
        val usuario = sesion.actual()
        actualizar {
            copy(
                rol = usuario?.rol ?: Rol.EMPLEADO,
                visible = usuario != null && !preferencias.introVista(usuario.id),
            )
        }
    }

    override fun onEvent(evento: IntroduccionEvent) {
        when (evento) {
            IntroduccionEvent.Abrir -> actualizar { copy(visible = true) }
            IntroduccionEvent.Terminar -> {
                usuarioId?.let(preferencias::marcarIntroVista)
                actualizar { copy(visible = false) }
            }
        }
    }
}
