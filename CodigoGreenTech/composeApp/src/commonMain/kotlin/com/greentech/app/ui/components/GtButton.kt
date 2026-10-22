package com.greentech.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.Motion

enum class GtButtonVariant { Primario, Tonal, Secundario, Texto, Peligro, PeligroSecundario }

private enum class EstadoBoton { Normal, Cargando, Exito }

/**
 * Botón de GreenTech UI: totalmente redondeado (pill), 60 dp de alto (56 dp el de texto),
 * a todo el ancho por defecto, con ícono + texto.
 *
 * - Al presionar se escala a 0.97 y los botones rellenos pasan de pill a 16 dp (shape morph con resorte).
 * - Estados: presionado (escala + morph), deshabilitado (surfaceContainerHigh + onSurfaceVariant),
 *   foco (anillo azul agua de 3 dp, ver [anilloFoco]), cargando y éxito.
 * - [cargando] muestra el LoadingIndicator expresivo; [exito] dibuja un check animado (tras guardar).
 */
@Composable
fun GtButton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    variante: GtButtonVariant = GtButtonVariant.Primario,
    icono: ImageVector? = null,
    cargando: Boolean = false,
    habilitado: Boolean = true,
    exito: Boolean = false,
) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    val alto = if (variante == GtButtonVariant.Texto) Dimens.tactilMin else Dimens.botonAlto
    val relleno = variante == GtButtonVariant.Primario || variante == GtButtonVariant.Peligro || variante == GtButtonVariant.Tonal
    val radio by animateDpAsState(
        targetValue = if (presionado && relleno) 16.dp else alto / 2,
        animationSpec = Motion.resorteRapido(),
        label = "morphBoton",
    )
    val forma = RoundedCornerShape(radio)
    val activo = habilitado && !cargando && !exito
    val esquema = MaterialTheme.colorScheme
    val deshabilitadoFondo = esquema.surfaceContainerHigh
    val deshabilitadoTexto = esquema.onSurfaceVariant

    val colores: ButtonColors = when (variante) {
        GtButtonVariant.Primario -> ButtonDefaults.buttonColors(esquema.primary, esquema.onPrimary, deshabilitadoFondo, deshabilitadoTexto)
        GtButtonVariant.Peligro -> ButtonDefaults.buttonColors(esquema.error, esquema.onError, deshabilitadoFondo, deshabilitadoTexto)
        GtButtonVariant.Tonal -> ButtonDefaults.buttonColors(
            esquema.secondaryContainer, esquema.onSecondaryContainer, deshabilitadoFondo, deshabilitadoTexto,
        )
        GtButtonVariant.Secundario -> ButtonDefaults.buttonColors(Color.Transparent, esquema.primary, Color.Transparent, deshabilitadoTexto)
        GtButtonVariant.PeligroSecundario -> ButtonDefaults.buttonColors(Color.Transparent, esquema.error, Color.Transparent, deshabilitadoTexto)
        GtButtonVariant.Texto -> ButtonDefaults.buttonColors(Color.Transparent, esquema.primary, Color.Transparent, deshabilitadoTexto)
    }
    val borde = when (variante) {
        GtButtonVariant.Secundario -> BorderStroke(1.5.dp, if (activo || exito) esquema.primary else esquema.outline)
        GtButtonVariant.PeligroSecundario -> BorderStroke(1.5.dp, if (activo) esquema.error else esquema.outline)
        else -> null
    }
    // Mientras carga o muestra el check, conserva sus colores (no se ve "apagado").
    val coloresVisibles = if (cargando || exito) {
        colores.copy(disabledContainerColor = colores.containerColor, disabledContentColor = colores.contentColor)
    } else {
        colores
    }
    val estadoVisual = when {
        exito -> EstadoBoton.Exito
        cargando -> EstadoBoton.Cargando
        else -> EstadoBoton.Normal
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = alto)
            .efectoPresion(interaccion)
            .anilloFoco(interaccion, forma)
            .semantics {
                when (estadoVisual) {
                    EstadoBoton.Cargando -> stateDescription = Textos.Comun.CARGANDO
                    EstadoBoton.Exito -> stateDescription = Textos.Disenio.GUARDADO
                    EstadoBoton.Normal -> Unit
                }
            },
        enabled = activo,
        shape = forma,
        colors = coloresVisibles,
        border = borde,
        elevation = null,
        contentPadding = PaddingValues(horizontal = Dimens.xl, vertical = Dimens.md),
        interactionSource = interaccion,
    ) {
        AnimatedContent(
            targetState = estadoVisual,
            transitionSpec = { fadeIn(Motion.media()) togetherWith fadeOut(Motion.rapida()) },
            label = "contenidoBoton",
        ) { estado ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                when (estado) {
                    EstadoBoton.Cargando -> {
                        GtCargando(tamano = Dimens.iconoMediano, color = LocalContentColor.current)
                        Spacer(Modifier.width(Dimens.sm))
                    }
                    EstadoBoton.Exito -> {
                        CheckAnimado(Modifier.size(Dimens.icono), color = LocalContentColor.current)
                        Spacer(Modifier.width(Dimens.sm))
                    }
                    EstadoBoton.Normal -> if (icono != null) {
                        Icon(icono, contentDescription = null, modifier = Modifier.size(Dimens.icono))
                        Spacer(Modifier.width(Dimens.sm))
                    }
                }
                Text(
                    if (estado == EstadoBoton.Exito) Textos.Disenio.GUARDADO else texto,
                    style = GtTexto.boton,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
