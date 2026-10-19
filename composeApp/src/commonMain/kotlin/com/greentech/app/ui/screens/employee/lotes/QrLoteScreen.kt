package com.greentech.app.ui.screens.employee.lotes

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greentech.app.platform.Permiso
import com.greentech.app.platform.rememberEstadoPermiso
import com.greentech.app.resources.Res
import com.greentech.app.resources.logo_icon
import com.greentech.app.ui.components.CheckAnimado
import com.greentech.app.ui.components.EstadoContenido
import com.greentech.app.ui.components.GtButton
import com.greentech.app.ui.components.GtButtonVariant
import com.greentech.app.ui.components.GtCard
import com.greentech.app.ui.components.GtCargandoCentro
import com.greentech.app.ui.components.GtScaffold
import com.greentech.app.ui.components.GtTopBar
import com.greentech.app.ui.components.contexto
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.preview.DatosPreview
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.app.ui.theme.tabular
import com.greentech.shared.domain.model.Lote
import com.greentech.shared.util.Fechas
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrLogoPadding
import io.github.alexzhirkevich.qrose.options.QrLogoShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import org.jetbrains.compose.resources.painterResource

/**
 * QR del lote: tarjeta siempre blanca (escaneable también en oscuro), código grande en tnum.
 * Recién registrado el lote, confirma lo que pasó y ofrece los siguientes pasos.
 */
@Composable
fun QrLoteScreen(estado: QrLoteUiState, onEvent: (QrLoteEvent) -> Unit) {
    // Solo en Android 8–9 hace falta permiso para guardar en la galería; en 10+ ya está concedido.
    val permisoGuardar = rememberEstadoPermiso(Permiso.GUARDAR_IMAGEN) { permitido ->
        if (permitido) onEvent(QrLoteEvent.GuardarImagen)
    }
    GtScaffold(
        topBar = {
            GtTopBar(
                Textos.Qr.TITULO,
                subtitulo = estado.lote.contexto(),
                onVolver = { onEvent(QrLoteEvent.Volver) },
                onAyuda = { onEvent(QrLoteEvent.Ayuda) },
            )
        },
    ) { padding ->
        EstadoContenido(
            estado.lote,
            onReintentar = { onEvent(QrLoteEvent.Reintentar) },
            modifier = Modifier.fillMaxSize(),
            cargando = { GtCargandoCentro(Modifier.padding(padding)) },
        ) { lote ->
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(padding).padding(horizontal = Dimens.margenLateral, vertical = Dimens.lg),
                verticalArrangement = Arrangement.spacedBy(Dimens.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val ancho = Modifier.widthIn(max = Dimens.anchoMaximo).fillMaxWidth()
                if (estado.recienCreado) AvisoRegistrado(ancho)
                TarjetaQr(lote, Modifier.widthIn(max = 420.dp).fillMaxWidth())
                if (!estado.recienCreado) {
                    Text(
                        Textos.Qr.INSTRUCCION,
                        style = GtTexto.cuerpo,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
                GtButton(
                    Textos.Qr.GUARDAR,
                    { if (permisoGuardar.concedido) onEvent(QrLoteEvent.GuardarImagen) else permisoGuardar.solicitar() },
                    ancho,
                    icono = Icons.Outlined.Download, cargando = estado.guardando,
                )
                if (estado.recienCreado) {
                    GtButton(
                        Textos.Qr.VER_LOTE, { onEvent(QrLoteEvent.VerDetalle) }, ancho,
                        variante = GtButtonVariant.Tonal, icono = Icons.AutoMirrored.Outlined.ArrowForward,
                    )
                    GtButton(
                        Textos.Qr.OTRO_LOTE, { onEvent(QrLoteEvent.RegistrarOtro) }, ancho,
                        variante = GtButtonVariant.Secundario, icono = Icons.Outlined.Add,
                    )
                }
                GtButton(
                    Textos.Qr.COMPARTIR, { onEvent(QrLoteEvent.Compartir) }, ancho,
                    variante = if (estado.recienCreado) GtButtonVariant.Texto else GtButtonVariant.Tonal,
                    icono = Icons.Outlined.Share,
                )
            }
        }
    }
}

/** Confirmación de lo que acaba de pasar, con el check animado del sistema. */
@Composable
private fun AvisoRegistrado(modifier: Modifier) {
    val c = GreenTechTheme.colors.exito
    GtCard(modifier.semantics(mergeDescendants = true) {}, colorFondo = c.fondo) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CheckAnimado(Modifier.size(Dimens.iconoGrande), color = c.contenido)
            Spacer(Modifier.width(Dimens.md))
            Column(Modifier.weight(1f)) {
                Text(Textos.Qr.LISTO_TITULO, style = GtTexto.tituloTarjeta, color = c.contenido, modifier = Modifier.semantics { heading() })
                Text(Textos.Qr.LISTO_MENSAJE, style = GtTexto.ayuda, color = c.contenido)
            }
        }
    }
}

@Composable
private fun TarjetaQr(lote: Lote, modifier: Modifier) {
    val x = GreenTechTheme.colors
    val logo = painterResource(Res.drawable.logo_icon)
    val qr = rememberQrCodePainter(lote.codigo, x.qrTinta, x.qrAcento, logo) {
        shapes {
            darkPixel = QrPixelShape.roundCorners()
            ball = QrBallShape.roundCorners(.3f)
            frame = QrFrameShape.roundCorners(.3f)
        }
        colors {
            dark = QrBrush.solid(x.qrTinta)
            ball = QrBrush.solid(x.qrAcento)
            frame = QrBrush.solid(x.qrAcento)
        }
        logo {
            painter = logo
            size = .22f
            padding = QrLogoPadding.Natural(.1f)
            shape = QrLogoShape.circle()
        }
    }
    // Siempre blanca con tinta oscura, también en tema oscuro, para que cualquier lector la escanee.
    Surface(modifier, shape = GtShapes.mosaico, color = x.qrFondo, contentColor = x.qrTinta) {
        Column(
            Modifier.fillMaxWidth().padding(Dimens.lgx),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.sm),
        ) {
            Image(qr, contentDescription = "${Textos.Qr.DESCRIPCION} ${lote.codigo}", modifier = Modifier.size(260.dp))
            Text(lote.codigo, style = GtTexto.tituloPantalla.tabular(), color = x.qrTinta)
            Text(lote.especie.nombre, style = GtTexto.tituloTarjeta, color = x.qrTinta)
            Text(
                "${lote.etapa.etiqueta} · ${Textos.Qr.SEMBRADO} ${Fechas.corta(lote.fechaSiembra)} · ${lote.zona.nombre}",
                style = GtTexto.ayuda,
                color = x.qrTinta,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview
@Composable
private fun QrLotePreviewClaro() {
    GreenTechTheme { QrLoteScreen(QrLoteUiState(lote = EstadoCarga.Listo(DatosPreview.lote), recienCreado = true)) {} }
}

@Preview
@Composable
private fun QrLotePreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { QrLoteScreen(QrLoteUiState(lote = EstadoCarga.Listo(DatosPreview.lote))) {} }
}
