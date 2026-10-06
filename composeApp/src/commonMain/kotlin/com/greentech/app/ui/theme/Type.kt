package com.greentech.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.greentech.app.resources.Res
import com.greentech.app.resources.inter_bold
import com.greentech.app.resources.inter_medium
import com.greentech.app.resources.inter_regular
import com.greentech.app.resources.inter_semibold
import org.jetbrains.compose.resources.Font

/** Inter (OFL) en los cuatro pesos que usa la app. */
@Composable
fun interFontFamily(): FontFamily = FontFamily(
    Font(Res.font.inter_regular, FontWeight.Normal),
    Font(Res.font.inter_medium, FontWeight.Medium),
    Font(Res.font.inter_semibold, FontWeight.SemiBold),
    Font(Res.font.inter_bold, FontWeight.Bold),
)

private fun FontFamily.estilo(tamano: Int, linea: Int, peso: FontWeight, espaciado: Double = 0.0) = TextStyle(
    fontFamily = this,
    fontSize = tamano.sp,
    lineHeight = linea.sp,
    fontWeight = peso,
    letterSpacing = espaciado.sp,
)

/**
 * Escala M3 completa con Inter. Los tamaños base (cuerpo 15–17 sp, títulos 22–28 sp)
 * están pensados para leer bajo el sol.
 */
@Composable
internal fun greenTechTypography(): Typography {
    val inter = interFontFamily()
    return remember(inter) {
        Typography(
            displayLarge = inter.estilo(57, 64, FontWeight.SemiBold, -0.25),
            displayMedium = inter.estilo(45, 52, FontWeight.SemiBold),
            displaySmall = inter.estilo(36, 44, FontWeight.SemiBold),
            headlineLarge = inter.estilo(32, 40, FontWeight.SemiBold),
            headlineMedium = inter.estilo(28, 36, FontWeight.SemiBold),
            headlineSmall = inter.estilo(24, 32, FontWeight.SemiBold),
            titleLarge = inter.estilo(22, 28, FontWeight.SemiBold),
            titleMedium = inter.estilo(17, 24, FontWeight.Medium),
            titleSmall = inter.estilo(15, 20, FontWeight.SemiBold),
            bodyLarge = inter.estilo(17, 26, FontWeight.Normal),
            bodyMedium = inter.estilo(15, 22, FontWeight.Normal),
            bodySmall = inter.estilo(14, 20, FontWeight.Normal),
            labelLarge = inter.estilo(15, 20, FontWeight.SemiBold, 0.1),
            labelMedium = inter.estilo(13, 18, FontWeight.Medium, 0.2),
            labelSmall = inter.estilo(12, 16, FontWeight.Medium, 0.3),
        )
    }
}

/** Números tabulares: las cifras no "bailan" al animarse (KPI, sensores, cantidades). */
fun TextStyle.tabular(): TextStyle = copy(fontFeatureSettings = "tnum")
