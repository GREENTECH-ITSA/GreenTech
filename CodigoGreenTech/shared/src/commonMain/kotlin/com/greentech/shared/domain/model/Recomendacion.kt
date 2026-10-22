package com.greentech.shared.domain.model

enum class CategoriaRecomendacion { RIEGO, PLAGA, ENFERMEDAD, TEMPERATURA, NUTRICION, GENERAL }

data class Recomendacion(
    val id: String,
    val titulo: String,
    val categoria: CategoriaRecomendacion,
    val pasos: List<String>,
    /** Explica de dónde sale: "por anomalía: Pulgón" o "por condición: humedad baja". */
    val origen: String,
)
