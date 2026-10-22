package com.greentech.app.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/** Familia redondeada (Expressive): 8 · 12 · 16 · 24 · 32 dp. */
internal val GreenTechShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

object GtShapes {
    /** Campos de texto y selectores. */
    val campo = RoundedCornerShape(16.dp)
    /** Tarjetas de ancho completo en listas. */
    val tarjeta = RoundedCornerShape(20.dp)
    /** Mosaicos bento. */
    val mosaico = RoundedCornerShape(24.dp)
    /** Hojas inferiores. */
    val hoja = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    /** Botones, chips y barra inferior flotante. */
    val pastilla = RoundedCornerShape(50)
    /**
     * Corte de hoja: la silueta de la hoja del logo (dos esquinas opuestas amplias y dos casi en punta).
     * Detalle distintivo de GreenTech; se usa SOLO en el mosaico de saludo de Inicio, el resumen del
     * Detalle del lote y la LoteCard destacada.
     */
    val hojaCorte = RoundedCornerShape(topStart = 32.dp, topEnd = 8.dp, bottomEnd = 32.dp, bottomStart = 8.dp)
    /** Versión chica del corte de hoja para el ícono de la LoteCard destacada. */
    val hojaCorteIcono = RoundedCornerShape(topStart = 20.dp, topEnd = 4.dp, bottomEnd = 20.dp, bottomStart = 4.dp)
    val circulo = CircleShape
}
