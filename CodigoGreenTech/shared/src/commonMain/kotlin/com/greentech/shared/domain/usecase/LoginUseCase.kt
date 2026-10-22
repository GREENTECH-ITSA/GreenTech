package com.greentech.shared.domain.usecase

import com.greentech.shared.domain.model.Usuario
import com.greentech.shared.domain.repository.AuthRepository
import com.greentech.shared.domain.result.AppError
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.domain.validation.EmailValidator
import com.greentech.shared.domain.validation.PasswordValidator

class LoginUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(correo: String, password: String): AppResult<Usuario> {
        if (EmailValidator.validar(correo) != null || PasswordValidator.validarLogin(password) != null) {
            return AppResult.Fallo(AppError.CredencialesInvalidas)
        }
        return repo.login(correo.trim().lowercase(), password)
    }
}
