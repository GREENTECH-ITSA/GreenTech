package com.greentech.app.ui.components

import com.greentech.app.platform.LocalNivelRendimiento
import androidx.compose.ui.draw.drawBehind
import androidx.compose.runtime.State
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.Motion

private const val DURACION_BRILLO = 1_200

/**
 * Brillo que recorre los skeletons. Con "Reducir animaciones" o en equipos de nivel bajo queda estático.
 * La posición del brillo se lee en la fase de dibujo ([brillo]): el skeleton no se recompone en cada frame.
 */
@Composable
private fun posicionBrillo(): State<Float>? {
    if (Motion.reducir || LocalNivelRendimiento.current.bajo) return null
    return rememberInfiniteTransition(label = "skeleton").animateFloat(
        initialValue = -600f,
        targetValue = 1_400f,
        animationSpec = infiniteRepeatable(tween(DURACION_BRILLO, easing = LinearEasing), RepeatMode.Restart),
        label = "brillo",
    )
}

/** Fondo del skeleton: color base o el degradado del brillo en la posición actual. */
@Composable
private fun Modifier.brillo(x: State<Float>?): Modifier {
    val base = GreenTechTheme.colors.skeleton
    val brillo = GreenTechTheme.colors.skeletonBrillo
    if (x == null) return background(base)
    return drawBehind {
        val p = x.value
        drawRect(Brush.linearGradient(listOf(base, brillo, base), start = Offset(p, 0f), end = Offset(p + 500f, 0f)))
    }
}

/** Lista de tarjetas "fantasma" con brillo suave mientras se cargan los datos de una lista. */
@Composable
fun LoadingSkeleton(modifier: Modifier = Modifier, tarjetas: Int = 4) {
    val brillo = posicionBrillo()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.margenLateral, vertical = Dimens.lg)
            .semantics { contentDescription = Textos.Comun.CARGANDO },
        verticalArrangement = Arrangement.spacedBy(Dimens.md),
    ) {
        repeat(tarjetas) {
            Column(
                Modifier.fillMaxWidth().height(112.dp).clip(GtShapes.tarjeta)
                    .background(GreenTechTheme.colors.skeleton.copy(alpha = 0.35f)).padding(Dimens.lg),
                verticalArrangement = Arrangement.spacedBy(Dimens.md),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.md)) {
                    Box(Modifier.size(48.dp).clip(GtShapes.campo).brillo(brillo))
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.sm)) {
                        Box(Modifier.fillMaxWidth(0.6f).height(16.dp).clip(GtShapes.pastilla).brillo(brillo))
                        Box(Modifier.fillMaxWidth(0.35f).height(16.dp).clip(GtShapes.pastilla).brillo(brillo))
                    }
                }
                Box(Modifier.fillMaxWidth().height(8.dp).clip(GtShapes.pastilla).brillo(brillo))
            }
        }
    }
}

/** Bloque con brillo para esqueletos a la medida (mosaicos, resúmenes). */
@Composable
fun SkeletonBloque(modifier: Modifier) {
    Box(modifier.clip(GtShapes.mosaico).brillo(posicionBrillo()))
}
