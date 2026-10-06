package com.greentech.shared.domain.model

import kotlinx.datetime.LocalDateTime

/** Tipo de problema con su catálogo de opciones frecuentes en viveros. */
enum class TipoAnomalia(val etiqueta: String, val catalogo: List<String>) {
    PLAGA("Plaga", listOf("Pulgón", "Mosca blanca", "Araña roja", "Trips", "Cochinilla")),
    ENFERMEDAD("Enfermedad", listOf("Oídio", "Pudrición de raíz", "Botrytis", "Roya", "Mildiu")),
    ANOMALIA("Anomalía", listOf("Hojas amarillas", "Marchitez", "Quemadura solar", "Falta de nutrientes", "Crecimiento lento")),
}

enum class Severidad(val etiqueta: String) {
    BAJA("Baja"),
    MEDIA("Media"),
    ALTA("Alta"),
}

data class Anomalia(
    val id: String,
    val loteId: String,
    val loteCodigo: String,
    val especie: String,
    val tipo: TipoAnomalia,
    val nombre: String,
    val severidad: Severidad,
    val descripcion: String,
    val autor: String,
    val fecha: LocalDateTime,
    val abierta: Boolean,
)
