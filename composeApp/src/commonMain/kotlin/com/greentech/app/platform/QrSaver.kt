package com.greentech.app.platform

/** Guarda o comparte la imagen del QR del lote. Esqueleto: hoy solo simula la acción. */
expect class QrSaver() {
    suspend fun guardarImagen(codigo: String): Boolean

    fun compartir(codigo: String): Boolean
}
