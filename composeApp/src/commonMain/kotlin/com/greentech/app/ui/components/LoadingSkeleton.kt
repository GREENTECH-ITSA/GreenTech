package com.greentech.app.ui.components

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

/** Brillo que recorre los skeletons. Con "Reducir animaciones" queda estático. */
@Composable
private fun brilloSkeleton(): Brush {
    val base = GreenTechTheme.colors.skeleton
    val brillo = GreenTechTheme.colors.skeletonBrillo
    if (Motion.reducir) return SolidColor(base)
    val transicion = rememberInfiniteTransition(label = "skeleton")
    val x by transicion.animateFloat(
        initialValue = -600f,
        targetValue = 1_400f,
        animationSpec = infiniteRepeatable(tween(DURACION_BRILLO, easing = LinearEasing), RepeatMode.Restart),
        label = "brillo",
    )
    return Brush.linearGradient(listOf(base, brillo, base), start = Offset(x, 0f), end = Offset(x + 500f, 0f))
}

/** Lista de tarjetas "fantasma" con brillo suave mientras se cargan los datos de una lista. */
@Composable
fun LoadingSkeleton(modifier: Modifier = Modifier, tarjetas: Int = 4) {
    val brush = brilloSkeleton()
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
                    Box(Modifier.size(48.dp).clip(GtShapes.campo).background(brush))
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.sm)) {
                        Box(Modifier.fillMaxWidth(0.6f).height(16.dp).clip(GtShapes.pastilla).background(brush))
                        Box(Modifier.fillMaxWidth(0.35f).height(16.dp).clip(GtShapes.pastilla).background(brush))
                    }
                }
                Box(Modifier.fillMaxWidth().height(8.dp).clip(GtShapes.pastilla).background(brush))
            }
        }
    }
}

/** Bloque con brillo para esqueletos a la medida (mosaicos, resúmenes). */
@Composable
fun SkeletonBloque(modifier: Modifier) {
    Box(modifier.clip(GtShapes.mosaico).background(brilloSkeleton()))
}
