package com.greentech.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.Motion

/**
 * Buscador al estilo SearchBar de Material 3: cápsula de 56 dp en surfaceContainerHigh con lupa
 * y botón para borrar (48 dp).
 */
@Composable
fun GtBuscador(
    valor: String,
    onValorChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val foco = LocalFocusManager.current
    val esquema = MaterialTheme.colorScheme
    Surface(modifier.fillMaxWidth().heightIn(min = Dimens.tactilMin), shape = GtShapes.pastilla, color = esquema.surfaceContainerHigh) {
        Row(Modifier.padding(start = Dimens.lg, end = Dimens.xs).heightIn(min = Dimens.tactilMin), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = esquema.onSurfaceVariant)
            Spacer(Modifier.width(Dimens.md))
            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                if (valor.isEmpty()) {
                    Text(placeholder, style = GtTexto.cuerpo, color = esquema.onSurfaceVariant, maxLines = 1)
                }
                BasicTextField(
                    value = valor,
                    onValueChange = onValorChange,
                    singleLine = true,
                    textStyle = GtTexto.cuerpo.copy(color = esquema.onSurface),
                    cursorBrush = SolidColor(esquema.primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { foco.clearFocus() }),
                    modifier = Modifier.fillMaxWidth().semantics { contentDescription = placeholder },
                )
            }
            AnimatedVisibility(valor.isNotEmpty(), enter = fadeIn(Motion.rapida()), exit = fadeOut(Motion.rapida())) {
                IconButton(onClick = { onValorChange("") }, modifier = Modifier.size(Dimens.tactilMin)) {
                    Icon(Icons.Outlined.Close, contentDescription = Textos.Comun.CERRAR, tint = esquema.onSurfaceVariant)
                }
            }
        }
    }
}
