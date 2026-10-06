package com.greentech.app.platform

/**
 * TODO(Cámara iOS): implementar con AVCaptureSession + AVCaptureMetadataOutput (tipo QR).
 * - Agregar NSCameraUsageDescription en iosApp/iosApp/Info.plist.
 * - Linterna: AVCaptureDevice.torchMode.
 */
actual class CameraScanner actual constructor() {
    actual val disponible: Boolean = false

    actual fun iniciar(onCodigo: (String) -> Unit) {
        // TODO: iniciar AVCaptureSession.
    }

    actual fun detener() {
        // TODO: detener AVCaptureSession.
    }

    actual fun alternarLinterna(encendida: Boolean) {
        // TODO: AVCaptureDevice.torchMode
    }
}
