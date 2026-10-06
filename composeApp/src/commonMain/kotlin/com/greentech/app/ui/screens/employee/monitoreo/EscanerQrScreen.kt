package com.greentech.app.ui.screens.employee.monitoreo

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FlashlightOff
import androidx.compose.material.icons.outlined.FlashlightOn
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.GlassSurface
import com.greentech.app.ui.components.GtBottomSheet
import com.greentech.app.ui.components.GtButton
import com.greentech.app.ui.components.GtButtonVariant
import com.greentech.app.ui.components.GtScaffold
import com.greentech.app.ui.components.GtTextField
import com.greentech.app.ui.components.rememberEnfoqueInicial
import androidx.compose.ui.focus.focusRequester
import com.greentech.app.ui.components.GtTopBar
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.efectoPresion
import com.greentech.app.ui.components.fuenteVidrio
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.BarrasSistema
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.Motion
import com.greentech.app.ui.theme.ThemeMode
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState

/**
 * Escanear QR: vista oscura en ambos temas (como una cámara), marco con esquinas redondeadas que
 * "respira" lento y botones flotantes de vidrio sobre la vista.
 */
@Composable
fun EscanerQrScreen(estado: EscanerQrUiState, onEvent: (EscanerQrEvent) -> Unit) {
    // La vista de cámara siempre es oscura: se fuerza el esquema oscuro (y los íconos claros de las
    // barras del sistema) solo en esta pantalla.
    BarrasSistema.ForzarOscuras()
    GreenTechTheme(modo = ThemeMode.OSCURO) {
        GtScaffold(topBar = { GtTopBar(Textos.Escaner.TITULO, onAyuda = { onEvent(EscanerQrEvent.Ayuda) }, principal = true) }) { padding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .background(GreenTechTheme.colors.camara)
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val vista = rememberHazeState()
                Box(Modifier.widthIn(max = 360.dp).fillMaxWidth().aspectRatio(0.82f)) {
                    Visor(Modifier.fillMaxSize().fuenteVidrio(vista))
                    BotonesFlotantes(estado, onEvent, vista, Modifier.align(Alignment.BottomCenter).padding(12.dp))
                }
                Text(
                    if (estado.camaraReal) Textos.Escaner.INSTRUCCION else Textos.Escaner.SIN_CAMARA,
                    style = MaterialTheme.typography.titleMedium,
                    color = GreenTechTheme.colors.sobreCamara,
                    textAlign = TextAlign.Center,
                )
                // Alternativa siempre visible al escaneo: escribir el código impreso debajo del QR.
                GtButton(
                    Textos.Escaner.MANUAL,
                    { onEvent(EscanerQrEvent.AbrirManual) },
                    modifier = Modifier.widthIn(max = 360.dp).fillMaxWidth(),
                    variante = GtButtonVariant.Tonal,
                    icono = Icons.Outlined.Keyboard,
                )
            }
        }
        if (estado.mostrarManual) HojaCodigoManual(estado, onEvent)
    }
}

/** Vista tipo cámara (simulada) con el marco de esquinas redondeadas respirando lento. */
@Composable
private fun Visor(modifier: Modifier) {
    val respiracion = if (Motion.reducir) {
        1f
    } else {
        val v by rememberInfiniteTransition(label = "visor").animateFloat(
            0.92f, 1f, infiniteRepeatable(tween(2_400), RepeatMode.Reverse), label = "respiracion",
        )
        v
    }
    val marco = GreenTechTheme.colors.sobreCamara
    val acento = MaterialTheme.colorScheme.primary
    Box(
        modifier.clip(GtShapes.mosaico).background(MaterialTheme.colorScheme.surfaceContainerLowest),
        contentAlignment = Alignment.Center,
    ) {
        Icon(IconosModulo.qr, null, tint = marco.copy(alpha = 0.08f), modifier = Modifier.size(140.dp))
        Canvas(Modifier.fillMaxSize().padding(36.dp)) {
            val lado = size.minDimension * 0.86f * respiracion
            val ox = (size.width - lado) / 2
            val oy = (size.height - lado) / 2 - 24.dp.toPx()
            val r = 22.dp.toPx()
            val largo = 34.dp.toPx()
            val grosor = 5.dp.toPx()
            val estilo = Stroke(grosor, cap = StrokeCap.Round)
            // Cuatro esquinas redondeadas: arco de 90° + dos tramos rectos.
            listOf(
                Triple(Offset(ox, oy), 180f, Pair(1f, 1f)),
                Triple(Offset(ox + lado - 2 * r, oy), 270f, Pair(-1f, 1f)),
                Triple(Offset(ox + lado - 2 * r, oy + lado - 2 * r), 0f, Pair(-1f, -1f)),
                Triple(Offset(ox, oy + lado - 2 * r), 90f, Pair(1f, -1f)),
            ).forEach { (esquina, angulo, dir) ->
                drawArc(marco, angulo, 90f, false, esquina, Size(2 * r, 2 * r), style = estilo)
                val cx = if (dir.first > 0) esquina.x else esquina.x + 2 * r
                val cy = if (dir.second > 0) esquina.y else esquina.y + 2 * r
                drawLine(marco, Offset(cx + r * dir.first, cy), Offset(cx + (r + largo) * dir.first, cy), grosor, StrokeCap.Round)
                drawLine(marco, Offset(cx, cy + r * dir.second), Offset(cx, cy + (r + largo) * dir.second), grosor, StrokeCap.Round)
            }
            drawCircle(acento.copy(alpha = 0.9f), radius = 4.dp.toPx(), center = Offset(size.width / 2, oy + lado / 2))
        }
    }
}

/** Botón flotante de vidrio sobre la vista: linterna (alternable). */
@Composable
private fun BotonesFlotantes(estado: EscanerQrUiState, onEvent: (EscanerQrEvent) -> Unit, vista: HazeState, modifier: Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        BotonVidrio(
            icono = if (estado.linterna) Icons.Outlined.FlashlightOn else Icons.Outlined.FlashlightOff,
            texto = if (estado.linterna) Textos.Escaner.LINTERNA_OFF else Textos.Escaner.LINTERNA_ON,
            vista = vista,
            activo = estado.linterna,
            onClick = { onEvent(EscanerQrEvent.AlternarLinterna) },
            soloIcono = true,
        )
    }
}

@Composable
private fun BotonVidrio(
    icono: ImageVector,
    texto: String,
    vista: HazeState,
    onClick: () -> Unit,
    activo: Boolean = false,
    soloIcono: Boolean = false,
) {
    val interaccion = remember { MutableInteractionSource() }
    // El gesto va en el contenido (dentro del vidrio): el efecto de desenfoque intercepta los toques
    // que lleguen al contenedor.
    GlassSurface(
        modifier = Modifier.heightIn(min = 56.dp).efectoPresion(interaccion),
        shape = GtShapes.pastilla,
        fuente = vista,
        bordeColor = if (activo) MaterialTheme.colorScheme.primary else null,
    ) {
        Row(
            Modifier
                .heightIn(min = 56.dp)
                .clip(GtShapes.pastilla)
                .toggleable(activo, onValueChange = { onClick() }, role = if (soloIcono) Role.Switch else Role.Button, interactionSource = interaccion, indication = ripple())
                .padding(horizontal = if (soloIcono) 16.dp else 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icono, contentDescription = if (soloIcono) texto else null, tint = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
            if (!soloIcono) {
                Spacer(Modifier.width(8.dp))
                Text(texto, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
            }
        }
    }
}

@Composable
private fun HojaCodigoManual(estado: EscanerQrUiState, onEvent: (EscanerQrEvent) -> Unit) {
    GtBottomSheet(onCerrar = { onEvent(EscanerQrEvent.CerrarManual) }) {
        Column(
            Modifier.padding(start = 20.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(Textos.Escaner.MANUAL_TITULO, style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
            GtTextField(
                valor = estado.codigo,
                onValorChange = { onEvent(EscanerQrEvent.CodigoCambio(it)) },
                etiqueta = Textos.Escaner.CODIGO,
                modifier = Modifier.focusRequester(rememberEnfoqueInicial()),
                placeholder = Textos.Escaner.CODIGO_EJEMPLO,
                icono = IconosModulo.qr,
                error = estado.errorCodigo,
                ayuda = Textos.Escaner.MANUAL_AYUDA,
                mayusculas = KeyboardCapitalization.Characters,
                accionIme = ImeAction.Go,
                alAccionIme = { onEvent(EscanerQrEvent.BuscarCodigo) },
            )
            GtButton(
                if (estado.buscando) Textos.Escaner.BUSCANDO else Textos.Escaner.BUSCAR,
                { onEvent(EscanerQrEvent.BuscarCodigo) },
                icono = Icons.AutoMirrored.Outlined.ArrowForward,
                cargando = estado.buscando,
                habilitado = estado.puedeBuscar,
            )
        }
    }
}

@Preview
@Composable
private fun EscanerPreviewClaro() {
    GreenTechTheme { EscanerQrScreen(EscanerQrUiState()) {} }
}

@Preview
@Composable
private fun EscanerPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { EscanerQrScreen(EscanerQrUiState(linterna = true, mostrarManual = true)) {} }
}
