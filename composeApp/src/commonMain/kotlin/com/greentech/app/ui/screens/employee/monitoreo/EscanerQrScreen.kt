package com.greentech.app.ui.screens.employee.monitoreo

import androidx.compose.foundation.layout.BoxWithConstraints
import com.greentech.app.platform.LocalNivelRendimiento
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.FlashlightOff
import androidx.compose.material.icons.outlined.FlashlightOn
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.NoPhotography
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greentech.app.platform.EstadoPermiso
import com.greentech.app.platform.Permiso
import com.greentech.app.platform.rememberEstadoPermiso
import com.greentech.app.ui.components.CheckAnimado
import com.greentech.app.ui.components.ConfirmSheet
import com.greentech.app.ui.components.GlassSurface
import com.greentech.app.ui.components.GtBottomSheet
import com.greentech.app.ui.components.GtButton
import com.greentech.app.ui.components.GtButtonVariant
import com.greentech.app.ui.components.GtScaffold
import com.greentech.app.ui.components.GtTextField
import com.greentech.app.ui.components.GtTopBar
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.efectoPresion
import com.greentech.app.ui.components.formatearNumero
import com.greentech.app.ui.components.fuenteVidrio
import com.greentech.app.ui.components.rememberEnfoqueInicial
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.BarrasSistema
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.Motion
import com.greentech.app.ui.theme.ThemeMode
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Escanear QR: vista oscura en ambos temas (como una cámara), marco con esquinas redondeadas que
 * "respira" lento y botones flotantes de vidrio sobre la vista.
 *
 * [vistaCamara] dibuja la vista previa real (la provee el grafo de navegación). El permiso de cámara
 * se explica y se pide al abrir la pantalla; sin permiso se muestra "Sin acceso a la cámara" y el
 * botón "Escribir código del lote" sigue disponible.
 */
@Composable
fun EscanerQrScreen(
    estado: EscanerQrUiState,
    onEvent: (EscanerQrEvent) -> Unit,
    vistaCamara: @Composable (Modifier) -> Unit = {},
) {
    val permiso = rememberEstadoPermiso(Permiso.CAMARA)
    // Al entrar sin permiso se muestra directo el diálogo del sistema (un toque para permitir). Solo una vez
    // por visita: si lo niega, queda la tarjeta "Sin acceso a la cámara" con su botón.
    var yaSePidio by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (estado.camaraReal && !permiso.concedido && !permiso.negadoPermanente && !yaSePidio) {
            yaSePidio = true
            permiso.solicitar()
        }
    }
    val camaraActiva = estado.camaraReal && permiso.concedido
    val haptica = LocalHapticFeedback.current
    LaunchedEffect(estado.detectado) {
        if (estado.detectado) haptica.performHapticFeedback(HapticFeedbackType.Confirm)
    }
    // La vista de cámara siempre es oscura: se fuerza el esquema oscuro (y los íconos claros de las
    // barras del sistema) solo en esta pantalla.
    BarrasSistema.ForzarOscuras()
    GreenTechTheme(modo = ThemeMode.OSCURO) {
        GtScaffold(topBar = { GtTopBar(Textos.Escaner.TITULO, onAyuda = { onEvent(EscanerQrEvent.Ayuda) }, principal = true) }) { padding ->
          BoxWithConstraints(Modifier.fillMaxSize()) {
            // En pantallas bajas (p. ej. 360 × 640 dp) el visor se reduce para que la instrucción y
            // "Escribir código del lote" queden siempre sobre la barra inferior; en el resto no cambia.
            val altoVisor = (
                maxHeight - padding.calculateTopPadding() - padding.calculateBottomPadding() -
                    Dimens.lg * 2 - Dimens.lgx * 2 - ReservaBajoVisor
                ).coerceAtLeast(VisorMinimo)
            Column(
                Modifier
                    .fillMaxSize()
                    .background(GreenTechTheme.colors.camara)
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(horizontal = Dimens.margenLateral, vertical = Dimens.lg),
                verticalArrangement = Arrangement.spacedBy(Dimens.lgx),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val vista = rememberHazeState()
                Box(Modifier.widthIn(max = 360.dp).fillMaxWidth().heightIn(max = altoVisor).aspectRatio(0.82f, matchHeightConstraintsFirst = true)) {
                    if (estado.camaraReal && !permiso.concedido) {
                        SinAccesoCamara(permiso, Modifier.fillMaxSize())
                    } else {
                        Visor(Modifier.fillMaxSize().fuenteVidrio(vista), camaraActiva, estado.detectado, vistaCamara)
                    }
                    if (camaraActiva) {
                        BotonesFlotantes(estado, onEvent, vista, Modifier.align(Alignment.BottomCenter).padding(Dimens.md))
                    }
                }
                if (!estado.camaraReal || camaraActiva) {
                    Text(
                        if (estado.camaraReal) Textos.Escaner.INSTRUCCION else Textos.Escaner.SIN_CAMARA,
                        style = GtTexto.tituloTarjeta,
                        color = GreenTechTheme.colors.sobreCamara,
                        textAlign = TextAlign.Center,
                    )
                }
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
        }
        if (estado.mostrarManual) HojaCodigoManual(estado, onEvent)
    }
}

/** Sin permiso de cámara: explica qué pasa y ofrece pedirlo otra vez o abrir Ajustes. */
@Composable
private fun SinAccesoCamara(permiso: EstadoPermiso, modifier: Modifier) {
    Column(
        modifier.clip(GtShapes.mosaico).background(MaterialTheme.colorScheme.surfaceContainerLowest).padding(Dimens.xl),
        verticalArrangement = Arrangement.spacedBy(Dimens.md, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(Icons.Outlined.NoPhotography, null, tint = GreenTechTheme.colors.sobreCamara, modifier = Modifier.size(48.dp))
        Text(
            Textos.Permisos.SIN_CAMARA_TITULO,
            style = GtTexto.tituloTarjeta,
            color = GreenTechTheme.colors.sobreCamara,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            if (permiso.negadoPermanente) Textos.Permisos.SIN_CAMARA_AJUSTES else Textos.Permisos.SIN_CAMARA_MENSAJE,
            style = GtTexto.cuerpo,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.size(Dimens.xs))
        if (permiso.negadoPermanente) {
            GtButton(Textos.Permisos.ABRIR_AJUSTES, permiso::abrirAjustes, icono = Icons.Outlined.Settings)
        } else {
            GtButton(Textos.Permisos.PERMITIR_CAMARA, permiso::solicitar, icono = Icons.Outlined.PhotoCamera)
        }
    }
}

/**
 * Vista de cámara con el marco de esquinas redondeadas respirando lento. Al leer un QR el marco
 * pasa a primary y aparece el check animado.
 */
@Composable
private fun Visor(
    modifier: Modifier,
    camaraActiva: Boolean = false,
    detectado: Boolean = false,
    vistaCamara: @Composable (Modifier) -> Unit = {},
) {
    // La respiración se lee solo al dibujar el marco (no recompone la vista de cámara). Sin bucle en nivel bajo.
    val respiracion = if (Motion.reducir || LocalNivelRendimiento.current.bajo) {
        null
    } else {
        rememberInfiniteTransition(label = "visor").animateFloat(
            0.92f, 1f, infiniteRepeatable(tween(2_400), RepeatMode.Reverse), label = "respiracion",
        )
    }
    val acento = MaterialTheme.colorScheme.primary
    val marco by animateColorAsState(if (detectado) acento else GreenTechTheme.colors.sobreCamara, Motion.rapida(), label = "marco")
    Box(
        modifier.clip(GtShapes.mosaico).background(MaterialTheme.colorScheme.surfaceContainerLowest),
        contentAlignment = Alignment.Center,
    ) {
        if (camaraActiva) {
            vistaCamara(Modifier.fillMaxSize())
        } else {
            Icon(IconosModulo.qr, null, tint = marco.copy(alpha = 0.08f), modifier = Modifier.size(140.dp))
        }
        Canvas(Modifier.fillMaxSize().padding(Dimens.esquinaVisor)) {
            val lado = size.minDimension * 0.86f * (respiracion?.value ?: 1f)
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
            if (!detectado) drawCircle(acento.copy(alpha = 0.9f), radius = 4.dp.toPx(), center = Offset(size.width / 2, oy + lado / 2))
        }
        if (detectado) {
            // Mismo centro que el marco (está 24 dp arriba del centro de la vista).
            CheckAnimado(
                Modifier.offset(y = (-24).dp).size(72.dp).semantics { contentDescription = Textos.Escaner.CODIGO_LEIDO },
                color = acento,
            )
        }
    }
}

/**
 * Botones flotantes de vidrio sobre la vista: linterna (solo si hay flash) y zoom ×1 / ×2,
 * grandes para usarlos con guantes.
 */
@Composable
private fun BotonesFlotantes(estado: EscanerQrUiState, onEvent: (EscanerQrEvent) -> Unit, vista: HazeState, modifier: Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(Dimens.md)) {
        if (estado.linternaDisponible) BotonVidrio(
            icono = if (estado.linterna) Icons.Outlined.FlashlightOn else Icons.Outlined.FlashlightOff,
            texto = if (estado.linterna) Textos.Escaner.LINTERNA_OFF else Textos.Escaner.LINTERNA_ON,
            vista = vista,
            activo = estado.linterna,
            onClick = { onEvent(EscanerQrEvent.AlternarLinterna) },
            soloIcono = true,
        )
        val decimales = if (abs(estado.zoom - estado.zoom.roundToInt()) < 0.05f) 0 else 1
        BotonVidrio(
            icono = Icons.Outlined.ZoomIn,
            texto = "×${formatearNumero(estado.zoom.toDouble(), decimales)}",
            vista = vista,
            activo = estado.zoom >= 1.5f,
            onClick = { onEvent(EscanerQrEvent.AlternarZoom) },
            descripcion = Textos.Escaner.ZOOM,
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
    descripcion: String? = null,
) {
    val interaccion = remember { MutableInteractionSource() }
    // El gesto va en el contenido (dentro del vidrio): el efecto de desenfoque intercepta los toques
    // que lleguen al contenedor.
    GlassSurface(
        modifier = Modifier.heightIn(min = Dimens.tactilMin).efectoPresion(interaccion),
        shape = GtShapes.pastilla,
        fuente = vista,
        bordeColor = if (activo) MaterialTheme.colorScheme.primary else null,
    ) {
        Row(
            Modifier
                .heightIn(min = Dimens.tactilMin)
                .clip(GtShapes.pastilla)
                .toggleable(activo, onValueChange = { onClick() }, role = if (soloIcono) Role.Switch else Role.Button, interactionSource = interaccion, indication = ripple())
                .then(if (descripcion != null) Modifier.semantics(mergeDescendants = true) { contentDescription = "$descripcion, $texto" } else Modifier)
                .padding(horizontal = if (soloIcono) Dimens.lg else Dimens.lgx),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icono, contentDescription = if (soloIcono) texto else null, tint = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
            if (!soloIcono) {
                Spacer(Modifier.width(Dimens.sm))
                Text(texto, style = GtTexto.boton, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
            }
        }
    }
}

@Composable
private fun HojaCodigoManual(estado: EscanerQrUiState, onEvent: (EscanerQrEvent) -> Unit) {
    GtBottomSheet(onCerrar = { onEvent(EscanerQrEvent.CerrarManual) }) {
        Column(
            Modifier.padding(start = Dimens.margenLateral, end = Dimens.margenLateral, bottom = Dimens.xl),
            verticalArrangement = Arrangement.spacedBy(Dimens.md),
        ) {
            Text(Textos.Escaner.MANUAL_TITULO, style = GtTexto.tituloTarjeta, modifier = Modifier.semantics { heading() })
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
    GreenTechTheme { EscanerQrScreen(EscanerQrUiState(), onEvent = {}) }
}

@Preview
@Composable
private fun EscanerPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { EscanerQrScreen(EscanerQrUiState(linterna = true, mostrarManual = true), onEvent = {}) }
}

/** Alto de la instrucción (2 líneas) y del botón "Escribir código del lote" debajo del visor. */
private val ReservaBajoVisor = 120.dp

/** El visor nunca baja de este alto (con letra muy grande la pantalla se desplaza). */
private val VisorMinimo = 220.dp
