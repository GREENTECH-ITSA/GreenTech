package com.greentech.app.ui.core

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.KSerializer
import androidx.lifecycle.SavedStateHandle
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

    /**
     * Borrador de formulario en [handle] (SavedStateHandle): lo escrito sobrevive a rotación, segundo plano,
     * "No conservar actividades" y muerte del proceso. Solo se guarda lo que escribe el usuario ([tomar]);
     * al recrear el Logic se vuelve a aplicar con [aplicar]. Devuelve el borrador restaurado (o null) para
     * que el Logic lo vuelva a aplicar si una carga posterior pone valores por defecto.
     *
     * TODO(Supabase): al guardar, el registro irá a la cola local persistente `pendiente_sync`
     * (shared/data/sync/SyncManager) en vez de enviarse directo; este borrador cubre solo lo que aún no se guarda.
     */
    protected fun <B> conservarBorrador(
        handle: SavedStateHandle?,
        serializer: KSerializer<B>,
        tomar: S.() -> B,
        aplicar: S.(B) -> S,
    ): B? {
        if (handle == null) return null
        val restaurado = handle.get<String>(CLAVE_BORRADOR)
            ?.let { runCatching { jsonBorrador.decodeFromString(serializer, it) }.getOrNull() }
        restaurado?.let { borrador -> actualizar { aplicar(borrador) } }
        viewModelScope.launch {
            state.map { it.tomar() }.distinctUntilChanged().collect {
                handle[CLAVE_BORRADOR] = jsonBorrador.encodeToString(serializer, it)
            }
        }
        return restaurado
    }

    protected fun notificarExito(mensaje: String) = emitir(UiEffect.Notificar(TipoNotificacion.EXITO, mensaje))
    protected fun notificarError(error: AppError) = emitir(UiEffect.Notificar(TipoNotificacion.ERROR, error.mensaje))
    protected fun notificarAdvertencia(mensaje: String) = emitir(UiEffect.Notificar(TipoNotificacion.ADVERTENCIA, mensaje))
    protected fun notificarInfo(mensaje: String) = emitir(UiEffect.Notificar(TipoNotificacion.INFO, mensaje))

    private companion object {
        const val CLAVE_BORRADOR = "borrador"
        val jsonBorrador = Json { ignoreUnknownKeys = true }
    }
}
