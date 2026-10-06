package com.greentech.app.platform

import kotlinx.coroutines.delay

/**
 * TODO(QR iOS): convertir el painter a UIImage y guardarlo con UIImageWriteToSavedPhotosAlbum
 * (requiere NSPhotoLibraryAddUsageDescription). Compartir con UIActivityViewController.
 */
actual class QrSaver actual constructor() {
    actual suspend fun guardarImagen(codigo: String): Boolean {
        delay(400)
        return true
    }

    actual fun compartir(codigo: String): Boolean = true
}
