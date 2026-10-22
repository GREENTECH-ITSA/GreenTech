package com.greentech.app.ui.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

/**
 * Etiqueta de una línea en un contenedor de alto fijo (barra inferior, segmentados): con letra
 * grande se reduce un poco en lugar de cortarse con "…".
 */
@Composable
fun TextoAjustable(
    texto: String,
    estilo: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    alineacion: TextAlign = TextAlign.Center,
) {
    val colorFinal = if (color != Color.Unspecified) color else LocalContentColor.current
    BasicText(
        texto,
        modifier = modifier,
        style = estilo.copy(color = colorFinal, textAlign = alineacion),
        maxLines = 1,
        autoSize = TextAutoSize.StepBased(
            minFontSize = MaterialTheme.typography.labelSmall.fontSize * 0.8f,
            maxFontSize = estilo.fontSize,
        ),
    )
}
