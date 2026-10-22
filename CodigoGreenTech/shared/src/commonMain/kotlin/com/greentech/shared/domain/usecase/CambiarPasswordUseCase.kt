package com.greentech.shared.domain.usecase

import com.greentech.shared.domain.repository.AuthRepository
import com.greentech.shared.domain.result.AppError
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.domain.validation.PasswordValidator

class CambiarPasswordUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(actual: String, nueva: String, confirmacion: String): AppResult<Unit> = when {
        actual.isEmpty() -> AppResult.Fallo(AppError.PasswordActualIncorrecta)
        !PasswordValidator.requisitos(nueva).cumpleTodos -> AppResult.Fallo(AppError.PasswordNoCumple)
        nueva != confirmacion -> AppResult.Fallo(AppError.PasswordsNoCoinciden)
        else -> repo.cambiarPassword(actual, nueva)
    }
}
