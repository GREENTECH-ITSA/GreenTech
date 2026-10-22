package com.greentech.app.ui.screens.admin.personal

import kotlinx.serialization.Serializable
import androidx.lifecycle.SavedStateHandle
import androidx.compose.runtime.Immutable
import com.greentech.app.navigation.AppDestination
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.UiEffect
import com.greentech.app.ui.strings.Textos
import com.greentech.shared.domain.model.Rol
import com.greentech.shared.domain.result.AppError
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.domain.result.Mensajes
import com.greentech.shared.domain.usecase.AltaTrabajadorUseCase
import com.greentech.shared.domain.usecase.ObtenerSesionUseCase
import com.greentech.shared.domain.validation.EmailValidator
import com.greentech.shared.domain.validation.NombreValidator
import com.greentech.shared.domain.validation.PasswordValidator

@Immutable
data class AltaTrabajadorUiState(
    val nombre: String = "",
    val correo: String = "",
    val password: String = "",
    val rol: Rol? = Rol.EMPLEADO,
    val errorDuplicado: String? = null,
    val intentoGuardar: Boolean = false,
    /** Resumen visible antes de confirmar el alta. */
    val confirmar: Boolean = false,
    val cargando: Boolean = false,
) {
    // Los errores se calculan siempre que hay texto; el campo los muestra al salir de él (GtTextField).
    val errorNombre: String? get() = if (nombre.isNotEmpty() || intentoGuardar) NombreValidator.validar(nombre) else null
    val errorCorreo: String?
        get() = errorDuplicado ?: if (correo.isNotEmpty() || intentoGuardar) EmailValidator.validar(correo) else null
    val errorPassword: String?
        get() = if ((password.isNotEmpty() || intentoGuardar) && !PasswordValidator.requisitos(password).cumpleTodos) {
            AppError.PasswordNoCumple.mensaje
        } else {
            null
        }

    val faltantes: List<String>
        get() = buildList {
            if (NombreValidator.validar(nombre) != null) add(Textos.AltaTrabajador.FALTA_NOMBRE)
            if (EmailValidator.validar(correo) != null) add(Textos.AltaTrabajador.FALTA_CORREO)
            if (!PasswordValidator.requisitos(password).cumpleTodos) add(Textos.AltaTrabajador.FALTA_PASSWORD)
            if (rol == null) add(Textos.AltaTrabajador.FALTA_ROL)
        }

    val puedeGuardar: Boolean get() = !cargando && faltantes.isEmpty() && errorDuplicado == null

    val hayCambios: Boolean get() = nombre.isNotBlank() || correo.isNotBlank() || password.isNotEmpty() || rol != Rol.EMPLEADO
}

sealed interface AltaTrabajadorEvent {
    data class NombreCambio(val valor: String) : AltaTrabajadorEvent
    data class CorreoCambio(val valor: String) : AltaTrabajadorEvent
    data class PasswordCambio(val valor: String) : AltaTrabajadorEvent
    data object GenerarPassword : AltaTrabajadorEvent
    data class ElegirRol(val rol: Rol) : AltaTrabajadorEvent
    /** Revisa los datos y muestra el resumen. */
    data object Guardar : AltaTrabajadorEvent
    data object CancelarConfirmacion : AltaTrabajadorEvent
    data object Confirmar : AltaTrabajadorEvent
    data object Volver : AltaTrabajadorEvent
    data object Ayuda : AltaTrabajadorEvent
}

/** Lo escrito en el formulario (sobrevive a la muerte del proceso). */
@Serializable
private data class BorradorAlta(val nombre: String, val correo: String, val rol: Rol?)

class AltaTrabajadorLogic(
    private val alta: AltaTrabajadorUseCase,
    private val sesion: ObtenerSesionUseCase,
    handle: SavedStateHandle,
) : BaseLogic<AltaTrabajadorUiState, AltaTrabajadorEvent>(AltaTrabajadorUiState()) {

    init {
        // La contraseña provisional no se guarda en el estado del sistema (queda fuera del borrador).
        conservarBorrador(handle, BorradorAlta.serializer(), { BorradorAlta(nombre, correo, rol) }) {
            copy(nombre = it.nombre, correo = it.correo, rol = it.rol)
        }
    }

    override fun onEvent(evento: AltaTrabajadorEvent) {
        when (evento) {
            is AltaTrabajadorEvent.NombreCambio -> actualizar { copy(nombre = evento.valor) }
            is AltaTrabajadorEvent.CorreoCambio -> actualizar { copy(correo = evento.valor.trim(), errorDuplicado = null) }
            is AltaTrabajadorEvent.PasswordCambio -> actualizar { copy(password = evento.valor) }
            AltaTrabajadorEvent.GenerarPassword -> actualizar { copy(password = alta.generarPassword()) }
            is AltaTrabajadorEvent.ElegirRol -> actualizar { copy(rol = evento.rol) }
            AltaTrabajadorEvent.Guardar -> {
                actualizar { copy(intentoGuardar = true) }
                if (estado.puedeGuardar) actualizar { copy(confirmar = true) }
            }
            AltaTrabajadorEvent.CancelarConfirmacion -> if (!estado.cargando) actualizar { copy(confirmar = false) }
            AltaTrabajadorEvent.Confirmar -> guardar()
            AltaTrabajadorEvent.Volver -> emitir(UiEffect.Volver)
            AltaTrabajadorEvent.Ayuda -> emitir(UiEffect.Navegar(AppDestination.Ayuda))
        }
    }

    private fun guardar() {
        if (!estado.puedeGuardar) return
        actualizar { copy(cargando = true) }
        lanzar {
            val s = estado
            when (val r = alta(s.nombre, s.correo, s.password, s.rol, sesion.actual()?.nombre.orEmpty())) {
                is AppResult.Exito -> {
                    actualizar { copy(confirmar = false) }
                    notificarExito(Mensajes.TRABAJADOR_REGISTRADO)
                    emitir(UiEffect.Volver)
                }
                is AppResult.Fallo -> {
                    // El correo duplicado se marca en su campo para poder corregirlo ahí mismo.
                    actualizar {
                        copy(
                            cargando = false,
                            confirmar = false,
                            errorDuplicado = if (r.error == AppError.TrabajadorDuplicado) r.error.mensaje else null,
                        )
                    }
                    notificarError(r.error)
                }
            }
        }
    }
}
