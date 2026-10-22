package com.greentech.app.platform

/** Guarda en la galería o comparte la imagen del QR del lote (con el código debajo). */
expect class QrSaver() {
    suspend fun guardarImagen(codigo: String): Boolean

    /** Abre el selector del sistema para enviar la imagen (WhatsApp, correo, imprimir…). */
    suspend fun compartir(codigo: String): Boolean
}
