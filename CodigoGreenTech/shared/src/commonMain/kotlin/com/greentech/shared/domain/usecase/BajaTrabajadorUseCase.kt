package com.greentech.shared.domain.usecase

import com.greentech.shared.domain.model.Usuario
import com.greentech.shared.domain.repository.AuthRepository
import com.greentech.shared.domain.repository.UsuarioRepository
import com.greentech.shared.domain.result.AppError
import com.greentech.shared.domain.result.AppResult

class BajaTrabajadorUseCase(
    private val repo: UsuarioRepository,
    private val auth: AuthRepository,
) {
    suspend operator fun invoke(trabajador: Usuario): AppResult<Usuario> {
        val sesion = auth.sesion.value ?: return AppResult.Fallo(AppError.SesionExpirada)
        return when {
            !trabajador.activo -> AppResult.Fallo(AppError.UsuarioInactivo)
            trabajador.id == sesion.id -> AppResult.Fallo(AppError.BajaPropia)
            else -> repo.bajaTrabajador(trabajador.id, sesion.nombre)
        }
    }
}
