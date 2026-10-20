package com.greentech.app.ui.screens.admin.personal

import androidx.compose.runtime.Immutable
import com.greentech.app.navigation.AppDestination
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.core.UiEffect
import com.greentech.app.ui.core.aEstadoCarga
import com.greentech.shared.domain.model.Usuario
import com.greentech.shared.domain.result.AppError
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.domain.result.Mensajes
import com.greentech.shared.domain.usecase.BajaTrabajadorUseCase
import com.greentech.shared.domain.usecase.ObtenerSesionUseCase
import com.greentech.shared.domain.usecase.ObtenerTrabajadoresUseCase

enum class PestanaPersonal { ACTIVOS, INACTIVOS }

@Immutable
data class TrabajadoresUiState(
    val trabajadores: EstadoCarga<List<Usuario>> = EstadoCarga.Cargando,
    val busqueda: String = "",
    val pestana: PestanaPersonal = PestanaPersonal.ACTIVOS,
    val porDarDeBaja: Usuario? = null,
    val procesando: Boolean = false,
    val idSesion: String? = null,
) {
    private val todos: List<Usuario> get() = trabajadores.datosOrNull.orEmpty()
    val totalActivos: Int get() = todos.count { it.activo }
    val totalInactivos: Int get() = todos.count { !it.activo }

    // Se calcula una sola vez por estado (los estados son inmutables), no en cada recomposición.
    val visibles: List<Usuario> by lazy(LazyThreadSafetyMode.NONE) {
        todos.filter { u ->
            u.activo == (pestana == PestanaPersonal.ACTIVOS) &&
                (busqueda.isBlank() || u.nombre.contains(busqueda.trim(), true) || u.correo.contains(busqueda.trim(), true))
        }
    }
}

sealed interface TrabajadoresEvent {
    data object Entrar : TrabajadoresEvent
    data class Buscar(val texto: String) : TrabajadoresEvent
    data class CambiarPestana(val pestana: PestanaPersonal) : TrabajadoresEvent
    data class PedirBaja(val usuario: Usuario) : TrabajadoresEvent
    data object CancelarBaja : TrabajadoresEvent
    data object ConfirmarBaja : TrabajadoresEvent
    data object Agregar : TrabajadoresEvent
    data object Ayuda : TrabajadoresEvent
}

class TrabajadoresLogic(
    private val obtenerTrabajadores: ObtenerTrabajadoresUseCase,
    private val bajaTrabajador: BajaTrabajadorUseCase,
    sesion: ObtenerSesionUseCase,
) : BaseLogic<TrabajadoresUiState, TrabajadoresEvent>(TrabajadoresUiState(idSesion = sesion.actual()?.id)) {

    override fun onEvent(evento: TrabajadoresEvent) {
        when (evento) {
            TrabajadoresEvent.Entrar -> cargar()
            is TrabajadoresEvent.Buscar -> actualizar { copy(busqueda = evento.texto) }
            is TrabajadoresEvent.CambiarPestana -> actualizar { copy(pestana = evento.pestana) }
            is TrabajadoresEvent.PedirBaja ->
                // Si ya está inactivo no se pide confirmación: el caso de uso responde con el aviso.
                if (evento.usuario.activo) actualizar { copy(porDarDeBaja = evento.usuario) } else darDeBaja(evento.usuario)
            TrabajadoresEvent.CancelarBaja -> actualizar { copy(porDarDeBaja = null) }
            TrabajadoresEvent.ConfirmarBaja -> estado.porDarDeBaja?.let { darDeBaja(it) }
            TrabajadoresEvent.Agregar -> emitir(UiEffect.Navegar(AppDestination.AltaTrabajador))
            TrabajadoresEvent.Ayuda -> emitir(UiEffect.Navegar(AppDestination.Ayuda))
        }
    }

    private fun cargar() = lanzar {
        if (estado.trabajadores !is EstadoCarga.Listo) actualizar { copy(trabajadores = EstadoCarga.Cargando) }
        val r = obtenerTrabajadores().aEstadoCarga()
        actualizar { copy(trabajadores = r) }
    }

    private fun darDeBaja(usuario: Usuario) = lanzar {
        actualizar { copy(procesando = true) }
        when (val r = bajaTrabajador(usuario)) {
            is AppResult.Exito -> {
                actualizar {
                    copy(
                        procesando = false,
                        porDarDeBaja = null,
                        trabajadores = EstadoCarga.Listo(trabajadores.datosOrNull.orEmpty().map { if (it.id == r.datos.id) r.datos else it }),
                    )
                }
                notificarExito(Mensajes.TRABAJADOR_BAJA)
            }
            is AppResult.Fallo -> {
                actualizar { copy(procesando = false, porDarDeBaja = null) }
                if (r.error == AppError.UsuarioInactivo) notificarAdvertencia(r.error.mensaje) else notificarError(r.error)
            }
        }
    }
}
