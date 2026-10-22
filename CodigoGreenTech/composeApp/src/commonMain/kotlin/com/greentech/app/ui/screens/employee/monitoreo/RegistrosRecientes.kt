package com.greentech.app.ui.screens.employee.monitoreo

/**
 * Recuerda qué se acaba de registrar en un lote para que, al volver a su detalle, el elemento
 * correspondiente se resalte un momento (etapa, actividad, observación, anomalía o salida).
 * Se consume una sola vez.
 */
class RegistrosRecientes {
    private val pendientes = mutableMapOf<String, AccionLote>()

    fun marcar(loteId: String, accion: AccionLote) {
        pendientes[loteId] = accion
    }

    fun tomar(loteId: String): AccionLote? = pendientes.remove(loteId)
}
