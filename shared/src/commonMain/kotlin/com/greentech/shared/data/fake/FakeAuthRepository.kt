package com.greentech.shared.data.fake

import com.greentech.shared.domain.model.Usuario
import com.greentech.shared.domain.repository.AuthRepository
import com.greentech.shared.domain.result.AppError
import com.greentech.shared.domain.result.AppResult
import kotlinx.coroutines.flow.StateFlow

class FakeAuthRepository(private val store: FakeStore) : AuthRepository {

    override val sesion: StateFlow<Usuario?> = store.sesion

    override suspend fun login(correo: String, password: String): AppResult<Usuario> {
        simularLatencia()
        val usuario = store.usuarios.value.firstOrNull { it.correo.equals(correo, ignoreCase = true) }
        if (usuario == null || store.passwords[usuario.correo] != password) {
            return AppResult.Fallo(AppError.CredencialesInvalidas)
        }
        if (!usuario.activo) return AppResult.Fallo(AppError.CuentaDesactivada)
        store.sesion.value = usuario
        return AppResult.Exito(usuario)
    }

    override suspend fun cambiarPassword(actual: String, nueva: String): AppResult<Unit> {
        simularLatencia()
        val usuario = store.sesion.value ?: return AppResult.Fallo(AppError.SesionExpirada)
        if (store.passwords[usuario.correo] != actual) return AppResult.Fallo(AppError.PasswordActualIncorrecta)
        store.passwords[usuario.correo] = nueva
        return AppResult.Exito(Unit)
    }

    override suspend fun logout() {
        store.sesion.value = null
    }
}
