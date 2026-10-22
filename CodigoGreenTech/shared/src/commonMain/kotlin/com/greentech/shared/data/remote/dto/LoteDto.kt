package com.greentech.shared.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** TODO(Supabase): ajustar los nombres de columnas a los de la tabla real `lotes`. */
@Serializable
data class LoteDto(
    val id: String,
    val codigo: String,
    @SerialName("especie_id") val especieId: String,
    @SerialName("zona_id") val zonaId: String,
    @SerialName("cantidad_inicial") val cantidadInicial: Int,
    @SerialName("cantidad_disponible") val cantidadDisponible: Int,
    val etapa: String,
    @SerialName("estado_salud") val estadoSalud: String,
    @SerialName("fecha_siembra") val fechaSiembra: String,
    @SerialName("responsable_id") val responsableId: String,
)
