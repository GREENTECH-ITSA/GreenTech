package com.greentech.app.ui.screens.common

import androidx.lifecycle.viewModelScope
import com.greentech.app.navigation.AppDestination
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.UiEffect
import com.greentech.shared.domain.model.Usuario
import com.greentech.shared.domain.usecase.CerrarSesionUseCase
import com.greentech.shared.domain.usecase.ObtenerSesionUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class PerfilUiState(
    val usuario: Usuario? = null,
    val confirmarSalida: Boolean = false,
    val saliendo: Boolean = false,
)

sealed interface PerfilEvent {
    data object CambiarPassword : PerfilEvent
    data object Ayuda : PerfilEvent
    data object PedirCerrarSesion : PerfilEvent
    data object CancelarCerrarSesion : PerfilEvent
    data object ConfirmarCerrarSesion : PerfilEvent
}

class PerfilLogic(
    sesion: ObtenerSesionUseCase,
    private val cerrarSesion: CerrarSesionUseCase,
) : BaseLogic<PerfilUiState, PerfilEvent>(PerfilUiState(usuario = sesion.actual())) {

    init {
        sesion().onEach { u -> if (u != null) actualizar { copy(usuario = u) } }.launchIn(viewModelScope)
    }

    override fun onEvent(evento: PerfilEvent) {
        when (evento) {
            PerfilEvent.CambiarPassword -> emitir(UiEffect.Navegar(AppDestination.CambiarPassword))
            PerfilEvent.Ayuda -> emitir(UiEffect.Navegar(AppDestination.Ayuda))
            PerfilEvent.PedirCerrarSesion -> actualizar { copy(confirmarSalida = true) }
            PerfilEvent.CancelarCerrarSesion -> actualizar { copy(confirmarSalida = false) }
            PerfilEvent.ConfirmarCerrarSesion -> lanzar {
                actualizar { copy(saliendo = true) }
                cerrarSesion()
                emitir(UiEffect.IrAlLogin)
            }
        }
    }
}
