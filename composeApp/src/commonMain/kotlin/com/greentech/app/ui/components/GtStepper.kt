package com.greentech.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.Motion
import com.greentech.app.ui.theme.tabular

/**
 * Selector de cantidad grande: botones −/+ de 64 dp y el número al centro. El número cambia con
 * AnimatedContent (sube o baja según el paso) y cada paso da una vibración breve.
 * Tocar el número permite escribirlo directo.
 */
@Composable
fun GtStepper(
    valor: Int?,
    onValorChange: (Int?) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    minimo: Int = 0,
    maximo: Int = 99_999,
    paso: Int = 1,
    error: String? = null,
    ayuda: String? = null,
    /** Ejemplo que se ve mientras no hay valor (ej. "Ej. 120"). */
    placeholder: String? = null,
) {
    val haptica = LocalHapticFeedback.current
    val esquema = MaterialTheme.colorScheme
    val foco = remember { FocusRequester() }
    val gestorFoco = LocalFocusManager.current
    val interaccion = remember { MutableInteractionSource() }
    val enfocado by interaccion.collectIsFocusedAsState()
    val cambiar: (Int) -> Unit = { nuevo ->
        haptica.performHapticFeedback(HapticFeedbackType.SegmentTick)
        onValorChange(nuevo)
    }
    Column(modifier.sacudir(disparadorSacudida(error, enfocado))) {
        Text(etiqueta, style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(8.dp))
        Surface(
            shape = GtShapes.mosaico,
            color = esquema.surface,
            border = BorderStroke(if (enfocado || error != null) 2.dp else 1.dp, if (error != null) esquema.error else if (enfocado) esquema.primary else esquema.outline),
        ) {
            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                BotonPaso(Icons.Outlined.Remove, Textos.Comun.DISMINUIR, (valor ?: 0) > minimo) {
                    cambiar(((valor ?: 0) - paso).coerceAtLeast(minimo))
                }
                Box(Modifier.weight(1f).height(64.dp), contentAlignment = Alignment.Center) {
                    // El campo real está siempre presente (recibe el foco y el teclado); mientras no
                    // se edita, encima se ve el número animado.
                    BasicTextField(
                        value = valor?.toString().orEmpty(),
                        onValueChange = { texto ->
                            val limpio = texto.filter { it.isDigit() }.take(5)
                            onValorChange(limpio.toIntOrNull()?.coerceAtMost(maximo))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(foco)
                            .alpha(if (enfocado) 1f else 0f)
                            .semantics {
                                contentDescription = etiqueta
                                if (error != null) error(error)
                            },
                        textStyle = MaterialTheme.typography.displaySmall.tabular().copy(color = esquema.onSurface, textAlign = TextAlign.Center),
                        singleLine = true,
                        cursorBrush = SolidColor(esquema.primary),
                        interactionSource = interaccion,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { gestorFoco.clearFocus() }),
                    )
                    if (!enfocado) {
                        AnimatedContent(
                            targetState = valor,
                            transitionSpec = {
                                val sube = (targetState ?: 0) >= (initialState ?: 0)
                                val entrada = slideInVertically(Motion.resorte()) { if (sube) it / 2 else -it / 2 } + fadeIn(Motion.rapida())
                                val salida = slideOutVertically(Motion.resorte()) { if (sube) -it / 2 else it / 2 } + fadeOut(Motion.rapida())
                                (entrada togetherWith salida).using(SizeTransform(clip = false))
                            },
                            label = "stepper",
                        ) { v ->
                            Text(
                                v?.toString() ?: placeholder ?: "0",
                                style = if (v == null && placeholder != null) {
                                    MaterialTheme.typography.headlineSmall
                                } else {
                                    MaterialTheme.typography.displaySmall.tabular()
                                },
                                color = if (v == null) esquema.onSurfaceVariant else esquema.onSurface,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
                BotonPaso(Icons.Outlined.Add, Textos.Comun.AUMENTAR, (valor ?: 0) < maximo) {
                    cambiar(((valor ?: 0) + paso).coerceAtMost(maximo))
                }
            }
        }
        when {
            error != null -> MensajeError(error, Modifier.padding(start = 16.dp, top = 4.dp))
            ayuda != null -> Text(
                ayuda,
                style = MaterialTheme.typography.bodySmall,
                color = esquema.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp),
            )
        }
    }
}

@Composable
private fun BotonPaso(icono: ImageVector, descripcion: String, habilitado: Boolean, onClick: () -> Unit) {
    val interaccion = remember { MutableInteractionSource() }
    FilledTonalIconButton(
        onClick = onClick,
        enabled = habilitado,
        modifier = Modifier.size(64.dp).efectoPresion(interaccion),
        shape = GtShapes.campo,
        interactionSource = interaccion,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    ) { Icon(icono, contentDescription = descripcion, modifier = Modifier.size(28.dp)) }
}
