package com.greentech.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greentech.app.resources.Res
import com.greentech.app.resources.logo_icon_transparente
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.interFontFamily
import org.jetbrains.compose.resources.painterResource

/** Emblema de GreenTech sin fondo: se ve igual en claro y en oscuro. */
@Composable
fun EmblemaGreenTech(modifier: Modifier = Modifier, tamano: Dp = 40.dp, descripcion: String? = Textos.Comun.LOGO) {
    Image(
        painter = painterResource(Res.drawable.logo_icon_transparente),
        contentDescription = descripcion,
        modifier = modifier.size(tamano),
    )
}

/**
 * Logotipo "GreenTech" compuesto en Compose con Inter Bold: "Green" en primary y "Tech" en onSurface.
 * Sustituye a logo.jpg (fondo blanco) para que funcione en ambos temas.
 */
@Composable
fun LogotipoGreenTech(modifier: Modifier = Modifier, tamano: TextUnit = 28.sp) {
    val texto = buildAnnotatedString {
        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) { append("Green") }
        withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurface)) { append("Tech") }
    }
    Text(
        texto,
        modifier = modifier,
        style = TextStyle(fontFamily = interFontFamily(), fontWeight = FontWeight.Bold, fontSize = tamano, letterSpacing = (-0.5).sp),
        maxLines = 1,
    )
}

/** Emblema + logotipo, horizontal (barras) o vertical (login, splash). */
@Composable
fun MarcaGreenTech(
    modifier: Modifier = Modifier,
    vertical: Boolean = false,
    tamanoEmblema: Dp = 40.dp,
    tamanoTexto: TextUnit = 24.sp,
) {
    val semantica = Modifier.semantics(mergeDescendants = true) { contentDescription = Textos.Comun.LOGO }
    if (vertical) {
        Column(
            modifier.then(semantica),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.md),
        ) {
            EmblemaGreenTech(tamano = tamanoEmblema, descripcion = null)
            LogotipoGreenTech(tamano = tamanoTexto)
        }
    } else {
        Row(
            modifier.then(semantica),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.sm),
        ) {
            EmblemaGreenTech(tamano = tamanoEmblema, descripcion = null)
            LogotipoGreenTech(tamano = tamanoTexto)
        }
    }
}
