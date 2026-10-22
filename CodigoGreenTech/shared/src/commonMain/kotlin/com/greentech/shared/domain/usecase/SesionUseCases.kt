package com.greentech.shared.domain.usecase

import com.greentech.shared.domain.model.Usuario
import com.greentech.shared.domain.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow

/** Expone el usuario con sesión iniciada. */
class ObtenerSesionUseCase(private val repo: AuthRepository) {
    operator fun invoke(): StateFlow<Usuario?> = repo.sesion
    fun actual(): Usuario? = repo.sesion.value
}

class CerrarSesionUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke() = repo.logout()
}
