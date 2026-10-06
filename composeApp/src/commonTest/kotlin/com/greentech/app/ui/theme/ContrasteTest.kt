package com.greentech.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.fail

/**
 * Revisión de contraste WCAG 2.1 de GreenTech UI en ambos temas.
 * Texto: ≥ 4.5:1. Íconos grandes y bordes de componentes: ≥ 3:1.
 */
class ContrasteTest {

    private data class Par(val nombre: String, val frente: Color, val fondo: Color, val minimo: Double)

    private fun canal(c: Float): Double = if (c <= 0.04045f) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)

    private fun luminancia(c: Color): Double =
        0.2126 * canal(c.red) + 0.7152 * canal(c.green) + 0.0722 * canal(c.blue)

    private fun contraste(a: Color, b: Color): Double {
        val la = luminancia(a)
        val lb = luminancia(b)
        return (max(la, lb) + 0.05) / (min(la, lb) + 0.05)
    }

    private fun pares(tema: String, s: ColorScheme, x: GreenTechExtendedColors): List<Par> {
        val texto = 4.5
        val grafico = 3.0
        val superficies = listOf(
            "background" to s.background,
            "surface" to s.surface,
            "surfaceContainerLow" to s.surfaceContainerLow,
            "surfaceContainer" to s.surfaceContainer,
            "surfaceContainerHigh" to s.surfaceContainerHigh,
        )
        // Peor caso del vidrio: el tinte sobre contenido blanco y sobre contenido negro.
        val vidrios = listOf(
            "vidrio/blanco" to x.vidrioTinte.compositeOver(Color.White),
            "vidrio/negro" to x.vidrioTinte.compositeOver(Color.Black),
            "vidrioRespaldo/blanco" to x.vidrioRespaldo.compositeOver(Color.White),
            "vidrioRespaldo/negro" to x.vidrioRespaldo.compositeOver(Color.Black),
        )
        return buildList {
            superficies.forEach { (nombre, fondo) ->
                add(Par("$tema onSurface/$nombre", s.onSurface, fondo, texto))
                add(Par("$tema onSurfaceVariant/$nombre", s.onSurfaceVariant, fondo, texto))
            }
            // Sobre vidrio el texto va en onSurface: se garantiza aunque detrás haya blanco o negro puro.
            vidrios.forEach { (nombre, fondo) -> add(Par("$tema onSurface/$nombre", s.onSurface, fondo, texto)) }
            // onSurfaceVariant solo se permite sobre vidrio cuando detrás está el fondo del propio tema.
            add(Par("$tema onSurfaceVariant/vidrio-sobre-fondo", s.onSurfaceVariant, x.vidrioTinte.compositeOver(s.background), texto))
            superficies.forEach { (nombre, fondo) ->
                add(Par("$tema error/$nombre", s.error, fondo, texto))
                add(Par("$tema outline/$nombre (borde)", s.outline, fondo, grafico))
                add(Par("$tema success/$nombre (ícono)", x.success, fondo, grafico))
                add(Par("$tema warning/$nombre (ícono)", x.warning, fondo, grafico))
                add(Par("$tema tertiary/$nombre (ícono)", s.tertiary, fondo, grafico))
            }
            listOf("background" to s.background, "surface" to s.surface, "surfaceContainerLow" to s.surfaceContainerLow)
                .forEach { (nombre, fondo) ->
                    add(Par("$tema primary/$nombre", s.primary, fondo, texto))
                    add(Par("$tema secondary/$nombre", s.secondary, fondo, texto))
                    add(Par("$tema success/$nombre", x.success, fondo, texto))
                    add(Par("$tema warning/$nombre", x.warning, fondo, texto))
                }
            add(Par("$tema primary/surfaceContainerHigh (ícono)", s.primary, s.surfaceContainerHigh, grafico))
            add(Par("$tema onPrimary/primary", s.onPrimary, s.primary, texto))
            add(Par("$tema onPrimaryContainer/primaryContainer", s.onPrimaryContainer, s.primaryContainer, texto))
            add(Par("$tema onSecondary/secondary", s.onSecondary, s.secondary, texto))
            add(Par("$tema onSecondaryContainer/secondaryContainer", s.onSecondaryContainer, s.secondaryContainer, texto))
            add(Par("$tema onTertiary/tertiary", s.onTertiary, s.tertiary, texto))
            add(Par("$tema onTertiaryContainer/tertiaryContainer", s.onTertiaryContainer, s.tertiaryContainer, texto))
            add(Par("$tema onError/error", s.onError, s.error, texto))
            add(Par("$tema onErrorContainer/errorContainer", s.onErrorContainer, s.errorContainer, texto))
            add(Par("$tema onSuccess/success", x.onSuccess, x.success, texto))
            add(Par("$tema onSuccessContainer/successContainer", x.onSuccessContainer, x.successContainer, texto))
            add(Par("$tema onWarning/warning", x.onWarning, x.warning, texto))
            add(Par("$tema onWarningContainer/warningContainer", x.onWarningContainer, x.warningContainer, texto))
            add(Par("$tema inverseOnSurface/inverseSurface", s.inverseOnSurface, s.inverseSurface, texto))
            add(Par("$tema sobreCamara/camara", x.sobreCamara, x.camara, texto))
            add(Par("$tema qrTinta/qrFondo", x.qrTinta, x.qrFondo, texto))
            add(Par("$tema qrAcento/qrFondo (QR)", x.qrAcento, x.qrFondo, grafico))
            add(Par("$tema onWarningContainer/warningContainer (chip)", x.onWarningContainer, x.warningContainer, texto))
            add(Par("$tema vidrio-texto-sobre-camara", s.onSurface, x.vidrioRespaldo.compositeOver(x.camara), texto))
            // Pares semánticos: el contenido va sobre su fondo y también sobre la superficie.
            listOf(
                "exito" to x.exito, "advertencia" to x.advertencia, "error" to x.error, "info" to x.info,
                "peligro" to x.peligro, "floracion" to x.floracion, "primario" to x.primario,
                "secundario" to x.secundario, "neutro" to x.neutro,
            ).forEach { (nombre, par) ->
                add(Par("$tema $nombre.contenido/$nombre.fondo", par.contenido, par.fondo, texto))
                add(Par("$tema $nombre.contenido/surface", par.contenido, s.surface, texto))
                add(Par("$tema $nombre.contenido/surfaceContainerLow", par.contenido, s.surfaceContainerLow, texto))
            }
        }
    }

    @Test
    fun todosLosParesCumplenWcagAA() {
        val todos = pares("claro", GreenTechLightColors, GreenTechLightExtended) +
            pares("oscuro", GreenTechDarkColors, GreenTechDarkExtended)
        val fallos = todos.mapNotNull { p ->
            val r = contraste(p.frente, p.fondo)
            if (r + 1e-9 < p.minimo) "${p.nombre}: ${(r * 100).toInt() / 100.0}:1 (mínimo ${p.minimo}:1)" else null
        }
        if (fallos.isNotEmpty()) fail("Pares por debajo de WCAG AA:\n" + fallos.joinToString("\n"))
    }
}
