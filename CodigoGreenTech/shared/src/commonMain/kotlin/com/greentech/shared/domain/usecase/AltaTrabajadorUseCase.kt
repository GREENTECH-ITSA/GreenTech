package com.greentech.shared.domain.usecase

import com.greentech.shared.domain.model.Rol
import com.greentech.shared.domain.model.Usuario
import com.greentech.shared.domain.repository.UsuarioRepository
import com.greentech.shared.domain.result.AppError
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.domain.validation.EmailValidator
import com.greentech.shared.domain.validation.NombreValidator
import com.greentech.shared.domain.validation.PasswordValidator

class AltaTrabajadorUseCase(private val repo: UsuarioRepository) {
    suspend operator fun invoke(
        nombre: String,
        correo: String,
        passwordProvisional: String,
        rol: Rol?,
        autor: String,
    ): AppResult<Usuario> {
        if (NombreValidator.validar(nombre) != null || EmailValidator.validar(correo) != null || rol == null) {
            return AppResult.Fallo(AppError.CamposObligatorios)
        }
        if (!PasswordValidator.requisitos(passwordProvisional).cumpleTodos) {
            return AppResult.Fallo(AppError.PasswordNoCumple)
        }
        return repo.altaTrabajador(nombre.trim(), correo.trim().lowercase(), passwordProvisional, rol, autor)
    }

    /** Genera una contraseña provisional de 10 caracteres que cumple los requisitos. */
    fun generarPassword(): String {
        val letras = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ"
        val numeros = "23456789"
        val todos = letras + numeros
        val base = List(8) { todos.random() } + letras.random() + numeros.random()
        return base.shuffled().joinToString("")
    }
}
