package com.greentech.app.ui.screens.auth

import com.greentech.app.platform.Medicion
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.UiEffect
import com.greentech.shared.domain.result.AppError
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.domain.usecase.LoginUseCase
import com.greentech.shared.domain.validation.EmailValidator
import com.greentech.shared.domain.validation.PasswordValidator

data class LoginUiState(
    val correo: String = "",
    val password: String = "",
    val intentoEnviar: Boolean = false,
    /** Correo o contraseña incorrectos: se marca en el campo de contraseña. */
    val errorCredenciales: String? = null,
    /** Otros motivos (cuenta desactivada, sin conexión…): se muestra arriba del botón. */
    val errorCuenta: String? = null,
    val cargando: Boolean = false,
) {
    val errorCorreo: String?
        get() = if (correo.isNotEmpty() || intentoEnviar) EmailValidator.validar(correo) else null

    val errorPassword: String?
        get() = errorCredenciales ?: if (password.isNotEmpty() || intentoEnviar) PasswordValidator.validarLogin(password) else null

    val puedeEnviar: Boolean
        get() = !cargando && EmailValidator.validar(correo) == null && PasswordValidator.validarLogin(password) == null
}

sealed interface LoginEvent {
    data class CorreoCambio(val valor: String) : LoginEvent
    data class PasswordCambio(val valor: String) : LoginEvent
    data object Enviar : LoginEvent
}

class LoginLogic(private val login: LoginUseCase) : BaseLogic<LoginUiState, LoginEvent>(LoginUiState()) {
    override fun onEvent(evento: LoginEvent) {
        when (evento) {
            is LoginEvent.CorreoCambio ->
                actualizar { copy(correo = evento.valor.trim(), errorCredenciales = null, errorCuenta = null) }
            is LoginEvent.PasswordCambio ->
                actualizar { copy(password = evento.valor, errorCredenciales = null, errorCuenta = null) }
            LoginEvent.Enviar -> enviar()
        }
    }

    private fun enviar() {
        actualizar { copy(intentoEnviar = true) }
        if (!estado.puedeEnviar) return
        actualizar { copy(cargando = true) }
        Medicion.iniciar(Medicion.LOGIN_A_INICIO)
        lanzar {
            when (val r = login(estado.correo, estado.password)) {
                is AppResult.Exito -> {
                    actualizar { copy(cargando = false) }
                    emitir(UiEffect.EntrarAlPanel(r.datos.rol))
                }
                is AppResult.Fallo -> {
                    // Se conserva el correo y se limpia solo la contraseña.
                    val credenciales = r.error == AppError.CredencialesInvalidas
                    actualizar {
                        copy(
                            cargando = false,
                            password = "",
                            intentoEnviar = false,
                            errorCredenciales = if (credenciales) r.error.mensaje else null,
                            errorCuenta = if (credenciales) null else r.error.mensaje,
                        )
                    }
                    notificarError(r.error)
                }
            }
        }
    }
}
