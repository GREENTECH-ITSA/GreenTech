package com.greentech.app.ui.screens.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greentech.app.ui.components.EmblemaGreenTech
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.Motion
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.app.ui.theme.interFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Splash breve: emblema con escala + fade y "GreenTech" que aparece por grupos de letras. */
@Composable
fun SplashScreen() {
    val alfa = remember { Animatable(if (Motion.reducir) 1f else 0f) }
    val escala = remember { Animatable(if (Motion.reducir) 1f else 0.85f) }
    // Dos grupos: "Green" y luego "Tech"; después el lema.
    val grupos = remember { List(3) { Animatable(if (Motion.reducir) 1f else 0f) } }
    LaunchedEffect(Unit) {
        launch { alfa.animateTo(1f, Motion.lenta()) }
        launch { escala.animateTo(1f, Motion.resorte()) }
        grupos.forEach { g ->
            delay(Motion.ms(160).toLong())
            launch { g.animateTo(1f, Motion.lenta()) }
        }
    }
    val desplazamiento = with(LocalDensity.current) { 8.dp.toPx() }
    val estilo = TextStyle(fontFamily = interFontFamily(), fontWeight = FontWeight.Bold, fontSize = 36.sp, letterSpacing = (-0.5).sp)
    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).semantics(mergeDescendants = true) {
            contentDescription = Textos.Comun.LOGO
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.lg, Alignment.CenterVertically),
    ) {
        EmblemaGreenTech(
            tamano = 144.dp,
            descripcion = null,
            modifier = Modifier.graphicsLayer {
                alpha = alfa.value
                scaleX = escala.value
                scaleY = escala.value
            },
        )
        Row {
            GrupoLetras("Green", estilo, MaterialTheme.colorScheme.primary, grupos[0].value, desplazamiento)
            GrupoLetras("Tech", estilo, MaterialTheme.colorScheme.onSurface, grupos[1].value, desplazamiento)
        }
        Text(
            Textos.Splash.LEMA,
            style = GtTexto.cuerpo,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.graphicsLayer { alpha = grupos[2].value },
        )
    }
}

@Composable
private fun GrupoLetras(texto: String, estilo: TextStyle, color: Color, progreso: Float, desplazamiento: Float) {
    Text(
        texto,
        style = estilo,
        color = color,
        modifier = Modifier.graphicsLayer {
            alpha = progreso
            translationY = (1f - progreso) * desplazamiento
        },
    )
}

@Preview
@Composable
private fun SplashPreviewClaro() {
    GreenTechTheme { SplashScreen() }
}

@Preview
@Composable
private fun SplashPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { SplashScreen() }
}
