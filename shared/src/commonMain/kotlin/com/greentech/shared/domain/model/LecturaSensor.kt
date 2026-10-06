package com.greentech.shared.domain.model

import kotlinx.datetime.LocalDateTime

/** Lectura de los sensores de una zona: humedad del sustrato (%) y temperatura (°C). */
data class LecturaSensor(
    val zonaId: String,
    val humedad: Double,
    val temperatura: Double,
    val fecha: LocalDateTime,
)

/** Condiciones de un lote: lecturas de su zona comparadas con el rango ideal de su especie. */
data class CondicionesLote(
    val lote: Lote,
    val lecturas: List<LecturaSensor>,
) {
    val ultima: LecturaSensor? get() = lecturas.maxByOrNull { it.fecha }
}

enum class TipoAlerta(val etiqueta: String) {
    HUMEDAD_BAJA("Humedad baja"),
    HUMEDAD_ALTA("Humedad alta"),
    TEMPERATURA_BAJA("Temperatura baja"),
    TEMPERATURA_ALTA("Temperatura alta"),
}

/** Alerta generada cuando una zona sale del rango ideal de alguna especie que contiene. */
data class AlertaSensor(
    val zona: Zona,
    val tipo: TipoAlerta,
    val valor: Double,
    val rangoMin: Double,
    val rangoMax: Double,
    val especiesAfectadas: List<String>,
)
