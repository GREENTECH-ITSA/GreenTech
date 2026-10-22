package com.greentech.shared.domain.validation

/** Estado de cada requisito de una contraseña nueva. Se marca en vivo en la UI. */
data class RequisitosPassword(
    val longitud: Boolean,
    val letra: Boolean,
    val numero: Boolean,
) {
    val cumpleTodos: Boolean get() = longitud && letra && numero
}

object PasswordValidator {
    const val LONGITUD_MINIMA_LOGIN = 6
    const val LONGITUD_MINIMA_NUEVA = 8

    /** Validación para iniciar sesión: no vacía y de 6 caracteres o más. */
    fun validarLogin(password: String): String? = when {
        password.isEmpty() -> "Escribe tu contraseña"
        password.length < LONGITUD_MINIMA_LOGIN -> "Debe tener al menos $LONGITUD_MINIMA_LOGIN caracteres"
        else -> null
    }

    /** Requisitos para una contraseña nueva: 8+ caracteres, una letra y un número. */
    fun requisitos(password: String) = RequisitosPassword(
        longitud = password.length >= LONGITUD_MINIMA_NUEVA,
        letra = password.any { it.isLetter() },
        numero = password.any { it.isDigit() },
    )
}
