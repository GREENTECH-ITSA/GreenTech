package com.greentech.shared.domain.model

import kotlinx.datetime.LocalDate

enum class TipoSalida(val etiqueta: String) {
    VENTA("Venta"),
    MERMA("Merma"),
}

enum class MotivoMerma(val etiqueta: String) {
    PLAGA("Plaga"),
    ENFERMEDAD("Enfermedad"),
    DANO_FISICO("Daño físico"),
    ESTRES_HIDRICO("Estrés hídrico"),
    OTRO("Otro"),
}

data class Salida(
    val id: String,
    val loteId: String,
    val loteCodigo: String,
    val especie: String,
    val tipo: TipoSalida,
    val cantidad: Int,
    val motivo: MotivoMerma?,
    val fecha: LocalDate,
    val responsable: String,
)
