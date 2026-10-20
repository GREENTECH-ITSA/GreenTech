package com.greentech.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.greentech.app.platform.LocalNivelRendimiento
import com.greentech.app.ui.theme.Motion
import com.greentech.app.ui.theme.tabular
import com.greentech.app.util.formatoDecimal
import com.greentech.app.util.formatoMiles
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/** Presión: el elemento se escala a 0.97 con un resorte mientras se mantiene tocado. */
@Composable
fun Modifier.efectoPresion(interaccion: InteractionSource): Modifier {
    val presionado by interaccion.collectIsPressedAsState()
    val escala by animateFloatAsState(
        targetValue = if (presionado) Motion.ESCALA_PRESION else 1f,
        animationSpec = Motion.resorteRapido(),
        label = "escalaPresion",
    )
    return graphicsLayer {
        scaleX = escala
        scaleY = escala
    }
}

/**
 * Aparición escalonada de listas: fade + 8 dp hacia arriba, 30 ms entre elementos.
 * Solo la primera vez: al volver a la pantalla (o al filtrar) no se repite.
 */
@Composable
fun Modifier.entradaEscalonada(indice: Int): Modifier {
    var yaEntro by rememberSaveable { mutableStateOf(false) }
    val progreso = remember { Animatable(if (yaEntro || Motion.reducir) 1f else 0f) }
    val desplazamiento = with(LocalDensity.current) { 8.dp.toPx() }
    // En nivel bajo la entrada es más corta: menos escalones y la mitad de duración.
    val bajo = LocalNivelRendimiento.current.bajo
    LaunchedEffect(Unit) {
        if (progreso.value < 1f) {
            val escalones = if (bajo) Motion.ESCALON_MAXIMO / 2 else Motion.ESCALON_MAXIMO
            delay(minOf(indice, escalones) * Motion.ESCALON_LISTA)
            progreso.animateTo(1f, if (bajo) Motion.rapida() else Motion.lenta())
        }
        yaEntro = true
    }
    return graphicsLayer {
        alpha = progreso.value
        translationY = (1f - progreso.value) * desplazamiento
    }
}

/**
 * Sacudida horizontal de 6 dp (3 ciclos, 300 ms) cada vez que cambia [disparador] a un valor no nulo.
 * Se usa en campos con error al intentar enviar.
 */
@Composable
fun Modifier.sacudir(disparador: Any?): Modifier {
    val x = remember { Animatable(0f) }
    val amplitud = with(LocalDensity.current) { 6.dp.toPx() }
    LaunchedEffect(disparador) {
        if (disparador == null || Motion.reducir) return@LaunchedEffect
        x.animateTo(
            0f,
            keyframes {
                durationMillis = Motion.LARGA
                val paso = Motion.LARGA / 6
                amplitud at paso
                -amplitud at paso * 2
                amplitud at paso * 3
                -amplitud at paso * 4
                amplitud * 0.5f at paso * 5
            },
        )
    }
    return graphicsLayer { translationX = x.value }
}

/**
 * Número con contador animado (count-up de 600 ms) al aparecer o cambiar.
 * Usa números tabulares para que el ancho no "baile".
 */
@Composable
fun NumeroAnimado(
    valor: Double,
    estilo: TextStyle,
    modifier: Modifier = Modifier,
    decimales: Int = 0,
    sufijo: String = "",
    color: Color = Color.Unspecified,
) {
    val animado = remember { Animatable(if (Motion.reducir) valor.toFloat() else 0f) }
    LaunchedEffect(valor) { animado.animateTo(valor.toFloat(), Motion.tween(Motion.CONTADOR)) }
    Text(
        formatearNumero(animado.value.toDouble(), decimales) + sufijo,
        style = estilo.tabular(),
        color = color,
        maxLines = 1,
        modifier = modifier,
    )
}

/** 1250 → "1,250" · 12.46 con 1 decimal → "12.5". */
fun formatearNumero(valor: Double, decimales: Int = 0): String =
    if (decimales == 0) valor.roundToInt().formatoMiles() else valor.formatoDecimal(decimales)
