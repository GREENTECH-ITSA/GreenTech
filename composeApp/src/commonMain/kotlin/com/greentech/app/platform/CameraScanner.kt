package com.greentech.app.platform

/**
 * Lector de QR con la cámara. Por ahora es un esqueleto: mientras [disponible] sea false,
 * la pantalla de escaneo pide escribir el código del lote.
 */
expect class CameraScanner() {
    /** true cuando exista una implementación real de cámara en la plataforma. */
    val disponible: Boolean

    fun iniciar(onCodigo: (String) -> Unit)

    fun detener()

    fun alternarLinterna(encendida: Boolean)
}
