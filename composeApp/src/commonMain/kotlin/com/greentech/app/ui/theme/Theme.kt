package com.greentech.app.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LocalGreenTechColors = staticCompositionLocalOf { GreenTechLightExtended }

/** true si [modo] resuelve a tema oscuro (Sistema sigue la configuración del dispositivo). */
@Composable
fun ThemeMode.esOscuro(): Boolean = when (this) {
    ThemeMode.SISTEMA -> isSystemInDarkTheme()
    ThemeMode.CLARO -> false
    ThemeMode.OSCURO -> true
}

/**
 * Tema de GreenTech UI: Material 3 Expressive (MaterialExpressiveTheme + MotionScheme.expressive),
 * Inter, formas redondeadas y dos esquemas completos (claro/oscuro).
 *
 * Al cambiar de tema, los tokens principales hacen un crossfade de 300 ms.
 */
@Composable
fun GreenTechTheme(
    modo: ThemeMode = ThemeMode.CLARO,
    content: @Composable () -> Unit,
) {
    val oscuro = modo.esOscuro()
    val esquema = animarEsquema(if (oscuro) GreenTechDarkColors else GreenTechLightColors)
    val extendidos = if (oscuro) GreenTechDarkExtended else GreenTechLightExtended
    CompositionLocalProvider(LocalGreenTechColors provides extendidos) {
        MaterialExpressiveTheme(
            colorScheme = esquema,
            motionScheme = if (Motion.reducir) MotionScheme.standard() else MotionScheme.expressive(),
            shapes = GreenTechShapes,
            typography = greenTechTypography(),
            content = content,
        )
    }
}

object GreenTechTheme {
    val colors: GreenTechExtendedColors
        @Composable @ReadOnlyComposable
        get() = LocalGreenTechColors.current
}

@Composable
private fun animar(objetivo: Color): Color {
    val color by animateColorAsState(objetivo, Motion.lenta(), label = "tema")
    return color
}

/** Crossfade de los tokens principales; el resto cambia de golpe (no se nota). */
@Composable
private fun animarEsquema(o: ColorScheme): ColorScheme = o.copy(
    primary = animar(o.primary),
    onPrimary = animar(o.onPrimary),
    primaryContainer = animar(o.primaryContainer),
    onPrimaryContainer = animar(o.onPrimaryContainer),
    secondary = animar(o.secondary),
    secondaryContainer = animar(o.secondaryContainer),
    onSecondaryContainer = animar(o.onSecondaryContainer),
    tertiary = animar(o.tertiary),
    background = animar(o.background),
    onBackground = animar(o.onBackground),
    surface = animar(o.surface),
    onSurface = animar(o.onSurface),
    onSurfaceVariant = animar(o.onSurfaceVariant),
    surfaceContainerLowest = animar(o.surfaceContainerLowest),
    surfaceContainerLow = animar(o.surfaceContainerLow),
    surfaceContainer = animar(o.surfaceContainer),
    surfaceContainerHigh = animar(o.surfaceContainerHigh),
    surfaceContainerHighest = animar(o.surfaceContainerHighest),
    outline = animar(o.outline),
    outlineVariant = animar(o.outlineVariant),
    error = animar(o.error),
    errorContainer = animar(o.errorContainer),
)
