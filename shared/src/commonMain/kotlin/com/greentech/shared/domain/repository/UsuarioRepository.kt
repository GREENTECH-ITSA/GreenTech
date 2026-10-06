package com.greentech.shared.domain.repository

import com.greentech.shared.domain.model.Rol
import com.greentech.shared.domain.model.Usuario
import com.greentech.shared.domain.result.AppResult

interface UsuarioRepository {
    suspend fun obtenerTrabajadores(): AppResult<List<Usuario>>

    suspend fun altaTrabajador(
        nombre: String,
        correo: String,
        passwordProvisional: String,
        rol: Rol,
        autor: String,
    ): AppResult<Usuario>

    suspend fun bajaTrabajador(id: String, autor: String): AppResult<Usuario>
}
