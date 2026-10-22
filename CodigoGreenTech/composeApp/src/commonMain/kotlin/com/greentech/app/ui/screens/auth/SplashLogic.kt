package com.greentech.app.ui.screens.auth

import androidx.compose.runtime.Immutable
import com.greentech.app.navigation.AppDestination
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.UiEffect
import com.greentech.shared.domain.usecase.ObtenerSesionUseCase
import kotlinx.coroutines.delay

@Immutable
data object SplashUiState

sealed interface SplashEvent

/** Muestra el logo un momento y decide: panel (si hay sesión) o login. */
class SplashLogic(private val sesion: ObtenerSesionUseCase) : BaseLogic<SplashUiState, SplashEvent>(SplashUiState) {
    init {
        lanzar {
            delay(DURACION_MS)
            val usuario = sesion.actual()
            emitir(
                if (usuario != null) UiEffect.EntrarAlPanel(usuario.rol)
                else UiEffect.Navegar(AppDestination.Login, reemplazar = true),
            )
        }
    }

    override fun onEvent(evento: SplashEvent) = Unit

    private companion object {
        const val DURACION_MS = 1_300L
    }
}
