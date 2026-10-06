package com.greentech.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.Motion

/**
 * Campo de texto de GreenTech UI: 16 dp de radio, borde que se anima al enfocar, error con ícono
 * que aparece con fade y sacudida horizontal cuando se intenta enviar con errores.
 *
 * Validación amable: mientras se escribe por primera vez no se muestra el error; aparece al salir
 * del campo (o al intentar guardar) y, desde ese momento, se actualiza en vivo y desaparece en
 * cuanto el valor es correcto.
 *
 * Teclado: sin [alAccionIme], "Siguiente" pasa al próximo campo y "Listo" cierra el teclado.
 */
@Composable
fun GtTextField(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    error: String? = null,
    ayuda: String? = null,
    placeholder: String? = null,
    esPassword: Boolean = false,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    accionIme: ImeAction = ImeAction.Next,
    alAccionIme: (() -> Unit)? = null,
    lineas: Int = 1,
    habilitado: Boolean = true,
    mayusculas: KeyboardCapitalization = KeyboardCapitalization.None,
    accionFinal: @Composable (() -> Unit)? = null,
    /** false cuando la etiqueta ya se muestra arriba; se conserva para lectores de pantalla. */
    etiquetaVisible: Boolean = true,
) {
    var mostrar by rememberSaveable { mutableStateOf(false) }
    val interaccion = remember { MutableInteractionSource() }
    val enfocado by interaccion.collectIsFocusedAsState()
    var estuvoEnfocado by remember { mutableStateOf(false) }
    var salioDelCampo by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(enfocado) {
        if (enfocado) estuvoEnfocado = true else if (estuvoEnfocado) salioDelCampo = true
    }
    val errorVisible = error?.takeIf { !enfocado || salioDelCampo }
    val disparoSacudida = disparadorSacudida(errorVisible, enfocado)
    val esquema = MaterialTheme.colorScheme

    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .sacudir(disparoSacudida)
            .semantics {
                if (!etiquetaVisible) contentDescription = etiqueta
                if (errorVisible != null) error(errorVisible)
            },
        enabled = habilitado,
        label = if (etiquetaVisible) { { Text(etiqueta, style = MaterialTheme.typography.labelMedium) } } else null,
        placeholder = placeholder?.let { { Text(it, color = esquema.onSurfaceVariant) } },
        leadingIcon = icono?.let { { Icon(it, contentDescription = null) } },
        trailingIcon = when {
            esPassword -> {
                {
                    IconButton(onClick = { mostrar = !mostrar }, modifier = Modifier.size(56.dp)) {
                        Icon(
                            if (mostrar) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (mostrar) Textos.Comun.OCULTAR_PASSWORD else Textos.Comun.MOSTRAR_PASSWORD,
                        )
                    }
                }
            }
            accionFinal != null -> accionFinal
            else -> null
        },
        isError = errorVisible != null,
        supportingText = if (errorVisible != null || ayuda != null) {
            {
                AnimatedContent(
                    targetState = errorVisible ?: ayuda,
                    transitionSpec = { fadeIn(Motion.media()) togetherWith fadeOut(Motion.rapida()) },
                    label = "textoApoyo",
                ) { texto ->
                    if (texto == errorVisible && errorVisible != null) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Outlined.ErrorOutline, contentDescription = null, modifier = Modifier.size(20.dp))
                            Text(errorVisible, style = MaterialTheme.typography.bodySmall)
                        }
                    } else if (texto != null) {
                        Text(texto, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        } else {
            null
        },
        visualTransformation = if (esPassword && !mostrar) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (esPassword) KeyboardType.Password else tipoTeclado,
            imeAction = accionIme,
            capitalization = mayusculas,
            autoCorrectEnabled = !esPassword && tipoTeclado == KeyboardType.Text,
        ),
        keyboardActions = if (alAccionIme != null) KeyboardActions(onAny = { alAccionIme() }) else KeyboardActions.Default,
        singleLine = lineas == 1,
        minLines = lineas,
        maxLines = if (lineas == 1) 1 else lineas + 3,
        textStyle = MaterialTheme.typography.bodyLarge,
        shape = GtShapes.campo,
        interactionSource = interaccion,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = esquema.surface,
            unfocusedContainerColor = esquema.surface,
            disabledContainerColor = esquema.surfaceContainerHigh,
            focusedBorderColor = esquema.primary,
            unfocusedBorderColor = esquema.outline,
            unfocusedLabelColor = esquema.onSurfaceVariant,
            unfocusedLeadingIconColor = esquema.onSurfaceVariant,
            focusedLeadingIconColor = esquema.primary,
            unfocusedTrailingIconColor = esquema.onSurfaceVariant,
        ),
    )
}

/**
 * Cuándo sacudir un campo con error: al aparecer (o cambiar) el error sin que el campo esté
 * enfocado —es decir, tras pulsar enviar— o cuando el Logic notifica un error.
 * Mientras se escribe no se sacude.
 */
@Composable
internal fun disparadorSacudida(error: String?, enfocado: Boolean): Any? {
    val retro = LocalRetroalimentacion.current
    var disparo by remember { mutableStateOf<Any?>(null) }
    var errorAnterior by remember { mutableStateOf<String?>(null) }
    var errorNotificado by remember { mutableStateOf(retro.ultimoError) }
    if (error != errorAnterior) {
        if (error != null && !enfocado) disparo = error to retro.ultimoError
        errorAnterior = error
    }
    if (retro.ultimoError != errorNotificado) {
        if (error != null) disparo = error to retro.ultimoError
        errorNotificado = retro.ultimoError
    }
    return disparo
}
