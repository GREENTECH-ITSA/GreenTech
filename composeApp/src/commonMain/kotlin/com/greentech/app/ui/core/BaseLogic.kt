package com.greentech.app.ui.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greentech.app.ui.notification.TipoNotificacion
import com.greentech.shared.domain.result.AppError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base de todos los XLogic: estado observable ([state]), eventos de la UI ([onEvent])
 * y un canal de efectos de un solo uso ([efectos]) para navegar o mostrar notificaciones.
 */
abstract class BaseLogic<S, E>(estadoInicial: S) : ViewModel() {

    private val _state = MutableStateFlow(estadoInicial)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _efectos = Channel<UiEffect>(Channel.BUFFERED)
    val efectos: Flow<UiEffect> = _efectos.receiveAsFlow()

    protected val estado: S get() = _state.value

    abstract fun onEvent(evento: E)

    protected fun actualizar(cambio: S.() -> S) = _state.update(cambio)

    protected fun emitir(efecto: UiEffect) {
        viewModelScope.launch { _efectos.send(efecto) }
    }

    protected fun lanzar(bloque: suspend () -> Unit) {
        viewModelScope.launch { bloque() }
    }

    protected fun notificarExito(mensaje: String) = emitir(UiEffect.Notificar(TipoNotificacion.EXITO, mensaje))
    protected fun notificarError(error: AppError) = emitir(UiEffect.Notificar(TipoNotificacion.ERROR, error.mensaje))
    protected fun notificarAdvertencia(mensaje: String) = emitir(UiEffect.Notificar(TipoNotificacion.ADVERTENCIA, mensaje))
    protected fun notificarInfo(mensaje: String) = emitir(UiEffect.Notificar(TipoNotificacion.INFO, mensaje))
}
