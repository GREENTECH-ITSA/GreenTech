package com.greentech.shared.domain.validation

object EmailValidator {
    private val patron = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+[.][A-Za-z]{2,}$")

    fun esValido(correo: String): Boolean = patron.matches(correo.trim())

    /** Devuelve el mensaje de error o null si el correo es válido. */
    fun validar(correo: String): String? = when {
        correo.isBlank() -> "Escribe tu correo"
        !esValido(correo) -> "Escribe un correo válido, por ejemplo nombre@gmail.com"
        else -> null
    }
}
