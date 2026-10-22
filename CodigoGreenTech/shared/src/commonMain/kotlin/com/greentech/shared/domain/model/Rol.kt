package com.greentech.shared.domain.model

/** Rol del usuario dentro del vivero. Define qué panel y qué módulos puede usar. */
enum class Rol(val etiqueta: String) {
    EMPLEADO("Empleado"),
    ADMINISTRADOR("Administrador"),
}
