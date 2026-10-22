package com.greentech.shared.domain.model

import kotlinx.datetime.LocalDateTime

data class Observacion(
    val id: String,
    val loteId: String,
    val texto: String,
    val estadoSalud: EstadoSalud,
    val conFoto: Boolean,
    val autor: String,
    val fecha: LocalDateTime,
)
