package com.greentech.app.platform

/**
 * TODO(Cámara Android): implementar con CameraX + ML Kit Barcode Scanning.
 * - Pedir el permiso CAMERA (ya declarado en el AndroidManifest).
 * - Analizar cuadros con BarcodeScanning.getClient() y llamar a onCodigo con el texto del QR.
 * - Linterna: camera.cameraControl.enableTorch(encendida).
 */
actual class CameraScanner actual constructor() {
    actual val disponible: Boolean = false

    actual fun iniciar(onCodigo: (String) -> Unit) {
        // TODO: arrancar CameraX y el analizador de códigos QR.
    }

    actual fun detener() {
        // TODO: liberar la cámara.
    }

    actual fun alternarLinterna(encendida: Boolean) {
        // TODO: cameraControl.enableTorch(encendida)
    }
}
