package com.greentech.shared.domain.model

/** Especie cultivada con su rango ideal de humedad del sustrato (%) y temperatura (°C). */
data class Especie(
    val id: String,
    val nombre: String,
    val nombreCientifico: String,
    val humedadMin: Double,
    val humedadMax: Double,
    val temperaturaMin: Double,
    val temperaturaMax: Double,
)

/** Zona física del vivero donde está el lote (invernadero o exterior). */
data class Zona(
    val id: String,
    val nombre: String,
)
