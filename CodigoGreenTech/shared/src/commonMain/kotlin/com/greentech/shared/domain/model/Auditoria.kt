package com.greentech.shared.domain.model

import kotlinx.datetime.LocalDateTime

enum class TipoAuditoria(val etiqueta: String) {
    VENTA("Venta"),
    BAJA_PLANTAS("Baja de plantas"),
    ALTA_USUARIO("Alta de usuario"),
    BAJA_USUARIO("Baja de usuario"),
}

/** Registro de una acción crítica para revisión del administrador. Solo lectura. */
data class Auditoria(
    val id: String,
    val tipo: TipoAuditoria,
    val usuario: String,
    val detalle: String,
    val fecha: LocalDateTime,
)
