package com.greentech.shared.domain.model

import kotlinx.datetime.LocalDate

data class Usuario(
    val id: String,
    val nombre: String,
    val correo: String,
    val rol: Rol,
    val activo: Boolean,
    val fechaAlta: LocalDate,
) {
    /** Iniciales para el avatar: "Juan Pérez" → "JP". */
    val iniciales: String
        get() = nombre.trim().split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }

    val primerNombre: String get() = nombre.trim().substringBefore(" ")
}
