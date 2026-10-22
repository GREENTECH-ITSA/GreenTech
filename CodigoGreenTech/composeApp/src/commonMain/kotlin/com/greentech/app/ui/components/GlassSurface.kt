package com.greentech.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import com.greentech.app.platform.LocalNivelRendimiento
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.greentech.app.platform.desenfoqueDisponible
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.Motion
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.blur.HazeBlurStyle
import dev.chrisbanes.haze.blur.HazeColorEffect
import dev.chrisbanes.haze.blur.hazeBlur
import dev.chrisbanes.haze.hazeSource

/** Radio del desenfoque del vidrio (20–24 dp). */
private val RadioVidrio = 22.dp

/**
 * Contenido que el vidrio más cercano puede desenfocar. Lo proveen GtScaffold (barra superior),
 * el panel con barra inferior y App (banners de notificación).
 */
val LocalHazeState = staticCompositionLocalOf<HazeState?> { null }

/** Marca este contenido como fuente del desenfoque de [estado]. */
fun Modifier.fuenteVidrio(estado: HazeState?): Modifier = if (estado != null) hazeSource(estado) else this

/**
 * Superficie de vidrio ligero de GreenTech UI. Úsala solo en elementos flotantes:
 * barra inferior, barra superior al hacer scroll, banners de notificación y tooltips.
 * Nunca detrás de formularios, tablas o párrafos largos.
 *
 * - Desenfoque real de 22 dp (Haze) con tinte surface al 78 % (claro) / 70 % (oscuro).
 * - Borde de 1 dp blanco 50 % (claro) / 8 % (oscuro) y brillo superior sutil en claro.
 * - Sin desenfoque disponible (Android < 12), sin fuente, en equipos de nivel bajo o con "Reducir
 *   animaciones": surface al 94 % (el desenfoque real cuesta GPU en cada frame).
 * - El texto encima va en onSurface (garantiza 4.5:1 sobre cualquier fondo; ver ContrasteTest).
 * - Los gestos (clickable, toggleable…) van en el contenido, no en [modifier]: el efecto de desenfoque
 *   intercepta los toques que lleguen al contenedor.
 *
 * @param fuente contenido a desenfocar; por defecto el de [LocalHazeState].
 * @param elevada agrega una sombra muy difusa (elevación 2) para separarla del contenido.
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    fuente: HazeState? = LocalHazeState.current,
    elevada: Boolean = false,
    conBorde: Boolean = true,
    bordeColor: Color? = null,
    contenido: @Composable BoxScope.() -> Unit,
) {
    val colores = GreenTechTheme.colors
    val superficie = MaterialTheme.colorScheme.surface
    val nivel = LocalNivelRendimiento.current
    val usarDesenfoque = fuente != null && desenfoqueDisponible && !nivel.bajo && !Motion.reducir
    val estilo = remember(colores.vidrioTinte, superficie) {
        HazeBlurStyle {
            blurRadius(RadioVidrio)
            noiseFactor(0f)
            backgroundColor(superficie)
            colorEffects(listOf(HazeColorEffect.tint(colores.vidrioTinte)))
            fallbackColorEffect(HazeColorEffect.tint(colores.vidrioRespaldo))
        }
    }
    val brillo = remember(colores.vidrioBrillo) {
        Brush.verticalGradient(0f to colores.vidrioBrillo, 0.3f to Color.Transparent)
    }
    Box(
        modifier
            // En nivel bajo, sin sombra (sombras mínimas).
            .then(if (elevada && !nivel.bajo) Modifier.shadow(2.dp, shape, clip = false) else Modifier)
            .clip(shape)
            .then(
                if (usarDesenfoque) Modifier.hazeBlur(HazeInput.Sources(fuente), estilo)
                else Modifier.background(colores.vidrioRespaldo),
            )
            .then(if (!colores.esOscuro) Modifier.background(brillo) else Modifier)
            .then(if (conBorde) Modifier.border(1.dp, bordeColor ?: colores.vidrioBorde, shape) else Modifier),
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) { contenido() }
    }
}

/**
 * Cuenta las hojas inferiores y diálogos abiertos para desenfocar el contenido de la app
 * detrás de su scrim (App aplica el desenfoque).
 */
@Stable
class VeloModal {
    var abiertos by mutableIntStateOf(0)
        internal set
}

val LocalVeloModal = staticCompositionLocalOf<VeloModal?> { null }

/** Registra un modal abierto mientras este composable esté en pantalla. */
@Composable
fun RegistrarModal() {
    val velo = LocalVeloModal.current ?: return
    DisposableEffect(velo) {
        velo.abiertos++
        onDispose { velo.abiertos-- }
    }
}

/**
 * Desenfoque del contenido de fondo mientras haya un modal abierto (scrim con desenfoque).
 * El radio animado se lee en la fase de dibujo (graphicsLayer): abrir una hoja no recompone la app.
 * En equipos de nivel bajo no se desenfoca (solo queda el scrim).
 */
@Composable
fun Modifier.desenfoqueModal(velo: VeloModal): Modifier {
    val activo = velo.abiertos > 0 && desenfoqueDisponible && !LocalNivelRendimiento.current.bajo
    val radio = animateDpAsState(if (activo) 12.dp else 0.dp, Motion.lenta(), label = "veloModal")
    return graphicsLayer {
        val px = radio.value.toPx()
        if (px > 0f) {
            renderEffect = BlurEffect(px, px, TileMode.Clamp)
            clip = true
        } else {
            renderEffect = null
            clip = false
        }
    }
}
