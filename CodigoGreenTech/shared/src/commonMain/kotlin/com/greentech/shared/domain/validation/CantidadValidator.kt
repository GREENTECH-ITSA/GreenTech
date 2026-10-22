package com.greentech.shared.domain.validation

import com.greentech.shared.domain.result.AppError

object CantidadValidator {
    /** Valida que la cantidad sea entera, mayor a 0 y no supere las plantas disponibles. */
    fun validar(cantidad: Int?, disponibles: Int): String? = when {
        cantidad == null || cantidad <= 0 -> "Ingresa una cantidad mayor a 0."
        cantidad > disponibles -> AppError.CantidadExcedida(disponibles).mensaje
        else -> null
    }
}
