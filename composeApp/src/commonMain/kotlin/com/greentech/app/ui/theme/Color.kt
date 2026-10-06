package com.greentech.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// GreenTech UI · paletas clara y oscura. Únicos colores fijos de toda la app:
// las pantallas leen MaterialTheme.colorScheme o GreenTechTheme.colors.
// ---------------------------------------------------------------------------

internal val GreenTechLightColors: ColorScheme = lightColorScheme(
    primary = Color(0xFF3D7A3A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE3F0DF),
    onPrimaryContainer = Color(0xFF16361A),
    inversePrimary = Color(0xFF8FCB8A),
    secondary = Color(0xFF2F6E5E),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDCEFE9),
    onSecondaryContainer = Color(0xFF0F3029),
    tertiary = Color(0xFF1570A6),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE3F1FA),
    onTertiaryContainer = Color(0xFF0B3A57),
    background = Color(0xFFF7FAF6),
    onBackground = Color(0xFF1F2A33),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F2A33),
    surfaceVariant = Color(0xFFE6ECE5),
    onSurfaceVariant = Color(0xFF5B6670),
    surfaceTint = Color(0xFF3D7A3A),
    inverseSurface = Color(0xFF2B3530),
    inverseOnSurface = Color(0xFFEEF3EE),
    error = Color(0xFFC62828),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFDECEA),
    onErrorContainer = Color(0xFF8C1D18),
    // La paleta proponía #8A958E, pero como borde de componente no llega a 3:1 sobre las superficies claras.
    outline = Color(0xFF77827B),
    outlineVariant = Color(0xFFD5DED3),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFD8DED6),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF2F6F1),
    surfaceContainer = Color(0xFFEDF2EC),
    surfaceContainerHigh = Color(0xFFE6ECE5),
    surfaceContainerHighest = Color(0xFFDFE6DE),
)

internal val GreenTechDarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFF8FCB8A),
    onPrimary = Color(0xFF0F2A10),
    primaryContainer = Color(0xFF24452A),
    onPrimaryContainer = Color(0xFFCFEBC9),
    inversePrimary = Color(0xFF3D7A3A),
    secondary = Color(0xFF7FC4B3),
    onSecondary = Color(0xFF0B2B24),
    secondaryContainer = Color(0xFF1E4A40),
    onSecondaryContainer = Color(0xFFC9EDE3),
    tertiary = Color(0xFF7CC4F0),
    onTertiary = Color(0xFF06263A),
    tertiaryContainer = Color(0xFF10364F),
    onTertiaryContainer = Color(0xFFCBE6FB),
    background = Color(0xFF0F1512),
    onBackground = Color(0xFFE4EBE5),
    surface = Color(0xFF141B17),
    onSurface = Color(0xFFE4EBE5),
    surfaceVariant = Color(0xFF242E28),
    onSurfaceVariant = Color(0xFFA7B3AB),
    surfaceTint = Color(0xFF8FCB8A),
    inverseSurface = Color(0xFFE4EBE5),
    inverseOnSurface = Color(0xFF1F2A25),
    error = Color(0xFFFF9A8F),
    onError = Color(0xFF4A100C),
    errorContainer = Color(0xFF5C1A16),
    onErrorContainer = Color(0xFFFFDAD5),
    outline = Color(0xFF7C8981),
    outlineVariant = Color(0xFF3A463F),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFF333D37),
    surfaceDim = Color(0xFF0F1512),
    surfaceContainerLowest = Color(0xFF0B110E),
    surfaceContainerLow = Color(0xFF18201B),
    surfaceContainer = Color(0xFF1D2621),
    surfaceContainerHigh = Color(0xFF242E28),
    surfaceContainerHighest = Color(0xFF2B3630),
)

/** Par de colores (contenido sobre fondo) para chips, insignias y estados. Contraste ≥ 4.5:1. */
@Immutable
data class ParColores(val contenido: Color, val fondo: Color)

/**
 * Colores que Material 3 no trae (éxito, advertencia, marca, vidrio…). Cambian con el tema
 * igual que el ColorScheme. Se leen con `GreenTechTheme.colors`.
 */
@Immutable
data class GreenTechExtendedColors(
    val esOscuro: Boolean,
    val success: Color,
    val onSuccess: Color,
    val successContainer: Color,
    val onSuccessContainer: Color,
    val warning: Color,
    val onWarning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    /** Solo para ilustración y acentos grandes (manchas del login, marca de agua). */
    val brandGreen: Color,
    /** Texto principal y secundario (atajos de onSurface / onSurfaceVariant). */
    val ink: Color,
    val inkSecondary: Color,
    /** Borde de componentes (3:1 sobre la superficie). */
    val outlineStrong: Color,
    // Pares semánticos para chips y estados.
    val exito: ParColores,
    val advertencia: ParColores,
    val error: ParColores,
    val info: ParColores,
    val peligro: ParColores,
    val floracion: ParColores,
    val primario: ParColores,
    val secundario: ParColores,
    val neutro: ParColores,
    // Skeletons.
    val skeleton: Color,
    val skeletonBrillo: Color,
    // Vista de cámara: oscura en ambos temas.
    val camara: Color,
    val sobreCamara: Color,
    val velo: Color,
    // Vidrio (GlassSurface).
    val vidrioTinte: Color,
    val vidrioRespaldo: Color,
    val vidrioBorde: Color,
    val vidrioBrillo: Color,
    // QR: siempre blanco con tinta oscura para que se pueda escanear.
    val qrFondo: Color,
    val qrTinta: Color,
    val qrAcento: Color,
)

private val Blanco = Color(0xFFFFFFFF)

internal val GreenTechLightExtended = GreenTechExtendedColors(
    esOscuro = false,
    success = Color(0xFF2E7D32),
    onSuccess = Blanco,
    successContainer = Color(0xFFE3F0DF),
    onSuccessContainer = Color(0xFF1E6B23),
    warning = Color(0xFF8A5A00),
    onWarning = Blanco,
    warningContainer = Color(0xFFFFF4DB),
    onWarningContainer = Color(0xFF6E4700),
    brandGreen = Color(0xFF559058),
    ink = GreenTechLightColors.onSurface,
    inkSecondary = GreenTechLightColors.onSurfaceVariant,
    outlineStrong = GreenTechLightColors.outline,
    exito = ParColores(Color(0xFF1E6B23), Color(0xFFE3F0DF)),
    advertencia = ParColores(Color(0xFF8A5A00), Color(0xFFFFF4DB)),
    error = ParColores(Color(0xFFB3261E), Color(0xFFFDECEA)),
    info = ParColores(Color(0xFF12659A), Color(0xFFE3F1FA)),
    peligro = ParColores(Color(0xFFA13C0A), Color(0xFFFDEDE3)),
    floracion = ParColores(Color(0xFF8A3A86), Color(0xFFF6E8F5)),
    primario = ParColores(Color(0xFF2F6A2C), Color(0xFFE3F0DF)),
    secundario = ParColores(Color(0xFF2F6E5E), Color(0xFFDCEFE9)),
    neutro = ParColores(Color(0xFF5B6670), Color(0xFFE6ECE5)),
    skeleton = Color(0xFFE6ECE5),
    skeletonBrillo = Color(0xFFF4F7F3),
    camara = Color(0xFF0F1512),
    sobreCamara = Blanco,
    velo = Color(0x99000000),
    vidrioTinte = Blanco.copy(alpha = 0.78f),
    vidrioRespaldo = Blanco.copy(alpha = 0.94f),
    vidrioBorde = Blanco.copy(alpha = 0.50f),
    vidrioBrillo = Blanco.copy(alpha = 0.20f),
    qrFondo = Blanco,
    qrTinta = Color(0xFF111A14),
    qrAcento = Color(0xFF3D7A3A),
)

internal val GreenTechDarkExtended = GreenTechExtendedColors(
    esOscuro = true,
    success = Color(0xFF8FCB8A),
    onSuccess = Color(0xFF0F2A10),
    successContainer = Color(0xFF24452A),
    onSuccessContainer = Color(0xFFCFEBC9),
    warning = Color(0xFFF2C266),
    onWarning = Color(0xFF2E2100),
    warningContainer = Color(0xFF4A3500),
    onWarningContainer = Color(0xFFFFE3A8),
    brandGreen = Color(0xFF559058),
    ink = GreenTechDarkColors.onSurface,
    inkSecondary = GreenTechDarkColors.onSurfaceVariant,
    outlineStrong = GreenTechDarkColors.outline,
    exito = ParColores(Color(0xFF8FCB8A), Color(0xFF24452A)),
    advertencia = ParColores(Color(0xFFF2C266), Color(0xFF4A3500)),
    error = ParColores(Color(0xFFFF9A8F), Color(0xFF5C1A16)),
    info = ParColores(Color(0xFF7CC4F0), Color(0xFF10364F)),
    peligro = ParColores(Color(0xFFFFB38A), Color(0xFF5A2A0E)),
    floracion = ParColores(Color(0xFFE7A8E2), Color(0xFF4A2147)),
    primario = ParColores(Color(0xFF8FCB8A), Color(0xFF24452A)),
    secundario = ParColores(Color(0xFF7FC4B3), Color(0xFF1E4A40)),
    neutro = ParColores(Color(0xFFA7B3AB), Color(0xFF242E28)),
    skeleton = Color(0xFF1D2621),
    skeletonBrillo = Color(0xFF2B3630),
    camara = Color(0xFF0B110E),
    sobreCamara = Blanco,
    velo = Color(0xB3000000),
    vidrioTinte = GreenTechDarkColors.surface.copy(alpha = 0.70f),
    vidrioRespaldo = GreenTechDarkColors.surface.copy(alpha = 0.94f),
    vidrioBorde = Blanco.copy(alpha = 0.08f),
    vidrioBrillo = Blanco.copy(alpha = 0f),
    qrFondo = Blanco,
    qrTinta = Color(0xFF111A14),
    qrAcento = Color(0xFF3D7A3A),
)
