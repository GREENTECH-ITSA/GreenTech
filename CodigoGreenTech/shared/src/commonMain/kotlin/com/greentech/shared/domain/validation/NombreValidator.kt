package com.greentech.shared.domain.validation

object NombreValidator {
    /** Solo letras (con acentos y ñ) y espacios. */
    private val patron = Regex("^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]+$")

    fun validar(nombre: String): String? {
        val limpio = nombre.trim()
        return when {
            limpio.isEmpty() -> "Escribe el nombre completo"
            !patron.matches(limpio) -> "Usa solo letras y espacios, sin números ni símbolos"
            limpio.length < 5 || !limpio.contains(" ") -> "Escribe nombre y apellido"
            else -> null
        }
    }
}
