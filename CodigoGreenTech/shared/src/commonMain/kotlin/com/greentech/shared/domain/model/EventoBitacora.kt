package com.greentech.shared.domain.model

import kotlinx.datetime.LocalDateTime

enum class TipoEvento(val etiqueta: String) {
    REGISTRO("Registro"),
    ETAPA("Etapa"),
    OBSERVACION("Observación"),
    ACTIVIDAD("Actividad"),
    ANOMALIA("Anomalía"),
    SALIDA("Salida"),
}

/** Entrada de la bitácora (historial) de un lote. */
data class EventoBitacora(
    val id: String,
    val loteId: String,
    val loteCodigo: String,
    val especie: String,
    val tipo: TipoEvento,
    val titulo: String,
    val descripcion: String,
    val autor: String,
    val fecha: LocalDateTime,
)
