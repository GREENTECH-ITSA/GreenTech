package com.greentech.app.ui.components

import androidx.compose.ui.graphics.Color
import io.github.alexzhirkevich.qrose.QrCodePainter
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrErrorCorrectionLevel
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrOptions
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.options.square
import kotlin.math.roundToInt

/**
 * QR estándar e imprimible de los lotes (RNF 3.3.6, docs/RENDIMIENTO.md sección 4B):
 * - ISO/IEC 18004 con solo el código del lote (GT-XXXXXXXXXX): mayúsculas, dígitos y "-" caben en el modo
 *   alfanumérico, así el código queda en la versión 1 (21 × 21 módulos) y los módulos son grandes.
 * - Corrección de errores Q (25 %): tolera rayones, manchas y reflejos en etiquetas de plástico o metal.
 * - Módulos cuadrados en negro puro sobre blanco puro (también en tema oscuro) y sin logo encima.
 * - Zona de silencio de [MODULOS_SILENCIO] módulos alrededor (la dibuja quien muestra o exporta el QR).
 */
object QrEstandar {
    const val MODULOS_SILENCIO = 4

    val tinta = Color.Black
    val fondo = Color.White

    val opciones: QrOptions = QrOptions {
        errorCorrectionLevel = QrErrorCorrectionLevel.MediumHigh
        shapes {
            darkPixel = QrPixelShape.square()
            ball = QrBallShape.square()
            frame = QrFrameShape.square()
        }
        colors {
            dark = QrBrush.solid(tinta)
            ball = QrBrush.solid(tinta)
            frame = QrBrush.solid(tinta)
        }
    }

    /** Módulos por lado del código (qrose informa 10 px por módulo como tamaño intrínseco). */
    fun modulos(qr: QrCodePainter): Int = (qr.intrinsicSize.width / 10f).roundToInt().coerceAtLeast(21)
}
