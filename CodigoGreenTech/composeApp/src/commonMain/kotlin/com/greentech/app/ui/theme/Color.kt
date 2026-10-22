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

/**
 * Primitivas derivadas del logo. Las pantallas no las usan directamente: pasan por el ColorScheme
 * o por [GreenTechExtendedColors]. Documentadas en design-system/MASTER.md.
 */
object PaletaGreenTech {
    // Verde de la hoja: 500 es el verde de marca y 600 el primario claro.
    val Verde50 = Color(0xFFF1F7EF)
    val Verde100 = Color(0xFFE3F0DF)
    val Verde200 = Color(0xFFC6E2C0)
    val Verde300 = Color(0xFF8FCB8A)
    val Verde400 = Color(0xFF6FAE6B)
    val Verde500 = Color(0xFF559058)
    val Verde600 = Color(0xFF3D7A3A)
    val Verde700 = Color(0xFF2F6A2C)
    val Verde800 = Color(0xFF24452A)
    val Verde900 = Color(0xFF16361A)

    // Verde azulado (circuito del logo).
    val Azulado100 = Color(0xFFDCEFE9)
    val Azulado300 = Color(0xFF7FC4B3)
    val Azulado600 = Color(0xFF2F6E5E)
    val Azulado800 = Color(0xFF1E4A40)
    val Azulado900 = Color(0xFF0F3029)

    // Agua (la gota del logo): humedad y estado "en observación".
    val Agua100 = Color(0xFFE3F1FA)
    val Agua300 = Color(0xFF7CC4F0)
    /** Tono exacto de la gota: solo ilustración, no llega a 3:1 sobre las superficies claras. */
    val Agua500 = Color(0xFF2A9BD8)
    val Agua600 = Color(0xFF1570A6)
    val Agua800 = Color(0xFF10364F)
    val Agua900 = Color(0xFF0B3A57)
}

private val P = PaletaGreenTech

internal val GreenTechLightColors: ColorScheme = lightColorScheme(
    primary = P.Verde600,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = P.Verde100,
    onPrimaryContainer = P.Verde900,
    inversePrimary = P.Verde300,
    secondary = P.Azulado600,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = P.Azulado100,
    onSecondaryContainer = P.Azulado900,
    tertiary = P.Agua600,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = P.Agua100,
    onTertiaryContainer = P.Agua900,
    background = Color(0xFFF7FAF6),
    onBackground = Color(0xFF1F2A33),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F2A33),
    surfaceVariant = Color(0xFFE6ECE5),
    onSurfaceVariant = Color(0xFF5B6670),
    surfaceTint = P.Verde600,
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
    primary = P.Verde300,
    onPrimary = Color(0xFF0F2A10),
    primaryContainer = P.Verde800,
    onPrimaryContainer = Color(0xFFCFEBC9),
    inversePrimary = P.Verde600,
    secondary = P.Azulado300,
    onSecondary = Color(0xFF0B2B24),
    secondaryContainer = P.Azulado800,
    onSecondaryContainer = Color(0xFFC9EDE3),
    tertiary = P.Agua300,
    onTertiary = Color(0xFF06263A),
    tertiaryContainer = P.Agua800,
    onTertiaryContainer = Color(0xFFCBE6FB),
    background = Color(0xFF0F1512),
    onBackground = Color(0xFFE4EBE5),
    surface = Color(0xFF141B17),
    onSurface = Color(0xFFE4EBE5),
    surfaceVariant = Color(0xFF242E28),
    onSurfaceVariant = Color(0xFFA7B3AB),
    surfaceTint = P.Verde300,
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

/** Salud vegetal: sana (success), en observación (info), afectada (warning) y crítica (error). */
@Immutable
data class ColoresSalud(val sana: ParColores, val observacion: ParColores, val afectada: ParColores, val critica: ParColores)

/** Estado de una tarea (Parte 2): pendiente, en curso, hecha y vencida. */
@Immutable
data class ColoresTarea(val pendiente: ParColores, val enCurso: ParColores, val hecha: ParColores, val vencida: ParColores)

/** Estado de un traslado entre zonas (Parte 2): en tránsito, recibido y cancelado. */
@Immutable
data class ColoresTraslado(val enTransito: ParColores, val recibido: ParColores, val cancelado: ParColores)

/**
 * Colores de serie para gráficas: siempre el mismo color para el mismo dato en toda la app.
 * Son de uso gráfico (≥ 3:1 sobre las superficies); el texto de las etiquetas va en onSurface.
 */
@Immutable
data class SeriesGrafica(val ventas: Color, val merma: Color, val humedad: Color, val temperatura: Color)

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
    // Estados semánticos (alias de los pares anteriores, con nombre de dominio).
    val salud: ColoresSalud,
    val tarea: ColoresTarea,
    val traslado: ColoresTraslado,
    val series: SeriesGrafica,
    /** Anillo de foco visible (teclado / D-pad): 3:1 sobre todas las superficies. */
    val foco: Color,
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

// Pares semánticos por tema: se declaran aparte para que los alias de dominio (salud, tarea, traslado) los reutilicen.
private object ParesClaro {
    val exito = ParColores(Color(0xFF1E6B23), P.Verde100)
    val advertencia = ParColores(Color(0xFF8A5A00), Color(0xFFFFF4DB))
    val error = ParColores(Color(0xFFB3261E), Color(0xFFFDECEA))
    val info = ParColores(Color(0xFF12659A), P.Agua100)
    val peligro = ParColores(Color(0xFFA13C0A), Color(0xFFFDEDE3))
    val floracion = ParColores(Color(0xFF8A3A86), Color(0xFFF6E8F5))
    val primario = ParColores(P.Verde700, P.Verde100)
    val secundario = ParColores(P.Azulado600, P.Azulado100)
    val neutro = ParColores(Color(0xFF5B6670), Color(0xFFE6ECE5))
}

private object ParesOscuro {
    val exito = ParColores(P.Verde300, P.Verde800)
    val advertencia = ParColores(Color(0xFFF2C266), Color(0xFF4A3500))
    val error = ParColores(Color(0xFFFF9A8F), Color(0xFF5C1A16))
    val info = ParColores(P.Agua300, P.Agua800)
    val peligro = ParColores(Color(0xFFFFB38A), Color(0xFF5A2A0E))
    val floracion = ParColores(Color(0xFFE7A8E2), Color(0xFF4A2147))
    val primario = ParColores(P.Verde300, P.Verde800)
    val secundario = ParColores(P.Azulado300, P.Azulado800)
    val neutro = ParColores(Color(0xFFA7B3AB), Color(0xFF242E28))
}

internal val GreenTechLightExtended = GreenTechExtendedColors(
    esOscuro = false,
    success = Color(0xFF2E7D32),
    onSuccess = Blanco,
    successContainer = P.Verde100,
    onSuccessContainer = Color(0xFF1E6B23),
    warning = Color(0xFF8A5A00),
    onWarning = Blanco,
    warningContainer = Color(0xFFFFF4DB),
    onWarningContainer = Color(0xFF6E4700),
    brandGreen = P.Verde500,
    ink = GreenTechLightColors.onSurface,
    inkSecondary = GreenTechLightColors.onSurfaceVariant,
    outlineStrong = GreenTechLightColors.outline,
    exito = ParesClaro.exito,
    advertencia = ParesClaro.advertencia,
    error = ParesClaro.error,
    info = ParesClaro.info,
    peligro = ParesClaro.peligro,
    floracion = ParesClaro.floracion,
    primario = ParesClaro.primario,
    secundario = ParesClaro.secundario,
    neutro = ParesClaro.neutro,
    salud = ColoresSalud(ParesClaro.exito, ParesClaro.info, ParesClaro.advertencia, ParesClaro.error),
    tarea = ColoresTarea(ParesClaro.neutro, ParesClaro.info, ParesClaro.exito, ParesClaro.error),
    traslado = ColoresTraslado(ParesClaro.secundario, ParesClaro.exito, ParesClaro.neutro),
    series = SeriesGrafica(
        ventas = GreenTechLightColors.primary,
        merma = Color(0xFF8A5A00),
        humedad = GreenTechLightColors.tertiary,
        temperatura = GreenTechLightColors.secondary,
    ),
    foco = P.Agua600,
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
    qrAcento = P.Verde600,
)

internal val GreenTechDarkExtended = GreenTechExtendedColors(
    esOscuro = true,
    success = P.Verde300,
    onSuccess = Color(0xFF0F2A10),
    successContainer = P.Verde800,
    onSuccessContainer = Color(0xFFCFEBC9),
    warning = Color(0xFFF2C266),
    onWarning = Color(0xFF2E2100),
    warningContainer = Color(0xFF4A3500),
    onWarningContainer = Color(0xFFFFE3A8),
    brandGreen = P.Verde500,
    ink = GreenTechDarkColors.onSurface,
    inkSecondary = GreenTechDarkColors.onSurfaceVariant,
    outlineStrong = GreenTechDarkColors.outline,
    exito = ParesOscuro.exito,
    advertencia = ParesOscuro.advertencia,
    error = ParesOscuro.error,
    info = ParesOscuro.info,
    peligro = ParesOscuro.peligro,
    floracion = ParesOscuro.floracion,
    primario = ParesOscuro.primario,
    secundario = ParesOscuro.secundario,
    neutro = ParesOscuro.neutro,
    salud = ColoresSalud(ParesOscuro.exito, ParesOscuro.info, ParesOscuro.advertencia, ParesOscuro.error),
    tarea = ColoresTarea(ParesOscuro.neutro, ParesOscuro.info, ParesOscuro.exito, ParesOscuro.error),
    traslado = ColoresTraslado(ParesOscuro.secundario, ParesOscuro.exito, ParesOscuro.neutro),
    series = SeriesGrafica(
        ventas = GreenTechDarkColors.primary,
        merma = Color(0xFFF2C266),
        humedad = GreenTechDarkColors.tertiary,
        temperatura = GreenTechDarkColors.secondary,
    ),
    foco = P.Agua300,
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
    qrAcento = P.Verde600,
)
