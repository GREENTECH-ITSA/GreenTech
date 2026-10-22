package com.greentech.shared.domain.model

import kotlinx.datetime.LocalDateTime

data class Actividad(
    val id: String,
    val loteId: String,
    val tipo: TipoActividad,
    val observacion: String,
    val responsable: String,
    val fecha: LocalDateTime,
)
