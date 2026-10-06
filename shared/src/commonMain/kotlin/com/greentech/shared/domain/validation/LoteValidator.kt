package com.greentech.shared.domain.validation

object LoteValidator {
    fun validarEspecie(especieId: String?): String? =
        if (especieId.isNullOrBlank()) "Elige la especie" else null

    fun validarZona(zonaId: String?): String? =
        if (zonaId.isNullOrBlank()) "Elige la zona o invernadero" else null

    fun validarCantidad(cantidad: Int?): String? = when {
        cantidad == null -> "Escribe cuántas plantas tiene el lote."
        cantidad <= 0 -> "Ingresa una cantidad mayor a 0."
        else -> null
    }

    /** Código de lote con formato GT- seguido de 10 caracteres hexadecimales. */
    private val patronCodigo = Regex("^GT-[0-9A-F]{10}$")

    fun normalizarCodigo(codigo: String): String {
        val limpio = codigo.trim().uppercase().replace(" ", "")
        return if (limpio.startsWith("GT-")) limpio else "GT-$limpio"
    }

    fun codigoValido(codigo: String): Boolean = patronCodigo.matches(normalizarCodigo(codigo))
}
