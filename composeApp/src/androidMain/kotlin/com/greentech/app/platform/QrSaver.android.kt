package com.greentech.app.platform

import kotlinx.coroutines.delay

/**
 * TODO(QR Android): renderizar el painter de qrose a un Bitmap y guardarlo con MediaStore
 * (Pictures/GreenTech). Compartir con un Intent ACTION_SEND y un FileProvider.
 */
actual class QrSaver actual constructor() {
    actual suspend fun guardarImagen(codigo: String): Boolean {
        delay(400)
        return true
    }

    actual fun compartir(codigo: String): Boolean = true
}
