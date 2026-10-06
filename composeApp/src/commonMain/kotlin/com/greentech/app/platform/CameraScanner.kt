package com.greentech.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.StateFlow

/**
 * Lector de QR con la cámara. El Logic recibe los códigos con [iniciar] y controla linterna y zoom;
 * [VistaCamara] muestra la vista previa y liga la cámara al ciclo de vida de la pantalla.
 * Si [disponible] es false (equipo sin cámara), la pantalla pide escribir el código del lote.
 */
expect class CameraScanner() {
    /** true si el equipo tiene cámara. */
    val disponible: Boolean

    /** true si la cámara activa tiene flash (si no, el botón de linterna se oculta). */
    val linternaDisponible: StateFlow<Boolean>

    val linternaEncendida: StateFlow<Boolean>

    /** Zoom actual (1 = sin zoom). Cambia con el botón ×1 / ×2 o al pellizcar. */
    val zoom: StateFlow<Float>

    /** [onCodigo] recibe el texto de cada QR leído (con un intervalo mínimo entre lecturas). */
    fun iniciar(onCodigo: (String) -> Unit)

    fun detener()

    fun alternarLinterna(encendida: Boolean)

    fun cambiarZoom(factor: Float)
}

/** Vista previa de la cámara: solo analiza mientras está en pantalla y la libera al salir. */
@Composable
expect fun VistaCamara(scanner: CameraScanner, modifier: Modifier = Modifier)
