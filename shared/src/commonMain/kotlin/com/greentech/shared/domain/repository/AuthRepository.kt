package com.greentech.shared.domain.repository

import com.greentech.shared.domain.model.Usuario
import com.greentech.shared.domain.result.AppResult
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    /** Usuario con sesión iniciada, o null si no hay sesión. */
    val sesion: StateFlow<Usuario?>

    suspend fun login(correo: String, password: String): AppResult<Usuario>

    suspend fun cambiarPassword(actual: String, nueva: String): AppResult<Unit>

    suspend fun logout()
}
