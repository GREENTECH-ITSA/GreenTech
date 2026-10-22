package com.greentech.shared.domain.model

import kotlinx.datetime.LocalDate

data class Lote(
    val id: String,
    /** Código impreso en el QR, por ejemplo GT-8F2A91C0D4. */
    val codigo: String,
    val especie: Especie,
    val zona: Zona,
    val cantidadInicial: Int,
    val cantidadDisponible: Int,
    val etapa: Etapa,
    val estadoSalud: EstadoSalud,
    val fechaSiembra: LocalDate,
    val responsable: String,
) {
    /** Proporción de plantas disponibles (0..1) para barras de progreso. */
    val proporcionDisponible: Float
        get() = if (cantidadInicial == 0) 0f else cantidadDisponible.toFloat() / cantidadInicial
}
