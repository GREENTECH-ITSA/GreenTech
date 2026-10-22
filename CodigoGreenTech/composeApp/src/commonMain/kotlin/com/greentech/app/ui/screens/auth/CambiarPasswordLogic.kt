package com.greentech.app.ui.screens.auth

import androidx.compose.runtime.Immutable
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.UiEffect
import com.greentech.app.ui.strings.Textos
import com.greentech.shared.domain.result.AppError
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.domain.result.Mensajes
import com.greentech.shared.domain.usecase.CambiarPasswordUseCase
import com.greentech.shared.domain.validation.PasswordValidator
import com.greentech.shared.domain.validation.RequisitosPassword

@Immutable
data class CambiarPasswordUiState(
    val actual: String = "",
    val nueva: String = "",
    val confirmacion: String = "",
    val errorActual: String? = null,
    val cargando: Boolean = false,
) {
    val requisitos: RequisitosPassword get() = PasswordValidator.requisitos(nueva)

    val errorNueva: String?
        get() = if (nueva.length >= PasswordValidator.LONGITUD_MINIMA_NUEVA && !requisitos.cumpleTodos) {
            AppError.PasswordNoCumple.mensaje
        } else {
            null
        }

    val errorConfirmacion: String?
        get() = if (confirmacion.isNotEmpty() && confirmacion != nueva) AppError.PasswordsNoCoinciden.mensaje else null

    val faltantes: List<String>
        get() = buildList {
            if (actual.isEmpty()) add(Textos.Password.ACTUAL)
            if (!requisitos.cumpleTodos) add(Textos.Password.NUEVA)
            if (confirmacion.isEmpty() || confirmacion != nueva) add(Textos.Password.CONFIRMAR)
        }

    val puedeGuardar: Boolean get() = !cargando && faltantes.isEmpty()

    val hayCambios: Boolean get() = actual.isNotEmpty() || nueva.isNotEmpty() || confirmacion.isNotEmpty()
}

sealed interface CambiarPasswordEvent {
    data class ActualCambio(val valor: String) : CambiarPasswordEvent
    data class NuevaCambio(val valor: String) : CambiarPasswordEvent
    data class ConfirmacionCambio(val valor: String) : CambiarPasswordEvent
    data object Guardar : CambiarPasswordEvent
    data object Volver : CambiarPasswordEvent
    data object Ayuda : CambiarPasswordEvent
}

class CambiarPasswordLogic(
    private val cambiarPassword: CambiarPasswordUseCase,
) : BaseLogic<CambiarPasswordUiState, CambiarPasswordEvent>(CambiarPasswordUiState()) {

    override fun onEvent(evento: CambiarPasswordEvent) {
        when (evento) {
            is CambiarPasswordEvent.ActualCambio -> actualizar { copy(actual = evento.valor, errorActual = null) }
            is CambiarPasswordEvent.NuevaCambio -> actualizar { copy(nueva = evento.valor) }
            is CambiarPasswordEvent.ConfirmacionCambio -> actualizar { copy(confirmacion = evento.valor) }
            CambiarPasswordEvent.Volver -> emitir(UiEffect.Volver)
            CambiarPasswordEvent.Ayuda -> emitir(UiEffect.Navegar(com.greentech.app.navigation.AppDestination.Ayuda))
            CambiarPasswordEvent.Guardar -> guardar()
        }
    }

    private fun guardar() {
        if (!estado.puedeGuardar) return
        actualizar { copy(cargando = true) }
        lanzar {
            when (val r = cambiarPassword(estado.actual, estado.nueva, estado.confirmacion)) {
                is AppResult.Exito -> {
                    actualizar { CambiarPasswordUiState() }
                    notificarExito(Mensajes.PASSWORD_ACTUALIZADA)
                    emitir(UiEffect.Volver)
                }
                is AppResult.Fallo -> {
                    actualizar {
                        copy(
                            cargando = false,
                            errorActual = if (r.error == AppError.PasswordActualIncorrecta) r.error.mensaje else errorActual,
                        )
                    }
                    notificarError(r.error)
                }
            }
        }
    }
}
