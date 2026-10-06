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
    val circulo = CircleShape
}
