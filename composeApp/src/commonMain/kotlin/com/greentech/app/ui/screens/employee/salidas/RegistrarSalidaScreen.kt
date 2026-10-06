package com.greentech.app.ui.screens.employee.salidas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.greentech.app.ui.components.InfoAyuda
import com.greentech.app.ui.components.ResumenConfirmacion
import com.greentech.app.ui.components.ResumenFila
import com.greentech.app.ui.components.contexto
import com.greentech.app.ui.components.obligatorio
import com.greentech.app.ui.strings.conValores
import com.greentech.app.util.FechasUi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.BentoSize
import com.greentech.app.ui.components.BentoTile
import com.greentech.app.ui.components.ConfirmSheet
import com.greentech.app.ui.components.FormularioScaffold
import com.greentech.app.ui.components.GtButtonVariant
import com.greentech.app.ui.components.GtChipGroup
import com.greentech.app.ui.components.GtDatePicker
import com.greentech.app.ui.components.GtSegmentado
import com.greentech.app.ui.components.GtStepper
import com.greentech.app.ui.components.IconosDominio
import com.greentech.app.ui.components.LoteResumenEstado
import com.greentech.app.ui.components.NumeroAnimado
import com.greentech.app.ui.components.SeccionFormulario
import com.greentech.app.ui.components.formatearNumero
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.preview.DatosPreview
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.Motion
import com.greentech.app.ui.theme.ParColores
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.app.util.formatoMiles
import com.greentech.shared.domain.model.MotivoMerma
import com.greentech.shared.domain.model.TipoSalida

/** Registrar salida: segmentado grande Venta / Merma, disponibles como dato protagonista y confirmación en hoja inferior. */
@Composable
fun RegistrarSalidaScreen(estado: RegistrarSalidaUiState, onEvent: (RegistrarSalidaEvent) -> Unit) {
    val esMerma = estado.tipo == TipoSalida.MERMA
    val x = GreenTechTheme.colors
    FormularioScaffold(
        titulo = Textos.Salida.TITULO,
        subtitulo = estado.lote.contexto(),
        onCerrar = { onEvent(RegistrarSalidaEvent.Volver) },
        onAyuda = { onEvent(RegistrarSalidaEvent.Ayuda) },
        hayCambios = estado.hayCambios,
        textoGuardar = when (estado.tipo) {
            TipoSalida.VENTA -> Textos.Salida.GUARDAR_VENTA
            TipoSalida.MERMA -> Textos.Salida.GUARDAR_MERMA
            null -> Textos.Salida.GUARDAR
        },
        onGuardar = { onEvent(RegistrarSalidaEvent.PedirConfirmacion) },
        habilitado = estado.puedeGuardar,
        cargando = estado.cargando,
        faltantes = estado.faltantes,
        variante = if (esMerma) GtButtonVariant.Peligro else GtButtonVariant.Primario,
        icono = IconosDominio.salida(estado.tipo ?: TipoSalida.VENTA),
    ) {
        LoteResumenEstado(estado.lote)
        BentoTile(
            BentoSize.M,
            etiqueta = Textos.Salida.DISPONIBLES,
            icono = com.greentech.app.ui.components.IconosModulo.lote,
            descripcion = "${Textos.Salida.DISPONIBLES}: ${estado.disponibles.formatoMiles()} ${Textos.Comun.PLANTAS}",
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                NumeroAnimado(estado.disponibles.toDouble(), MaterialTheme.typography.displaySmall)
                Spacer(Modifier.width(8.dp))
                Text(Textos.Comun.PLANTAS, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        SeccionFormulario(
            obligatorio(Textos.Salida.TIPO),
            info = InfoAyuda(Textos.Salida.INFO_TITULO, Textos.Salida.INFO),
            onVerAyuda = { onEvent(RegistrarSalidaEvent.Ayuda) },
        ) {
            GtSegmentado(
                opciones = TipoSalida.entries,
                seleccionado = estado.tipo,
                onSeleccion = { onEvent(RegistrarSalidaEvent.ElegirTipo(it)) },
                etiqueta = { if (it == TipoSalida.VENTA) Textos.Salida.VENTA else Textos.Salida.MERMA },
                descripcion = { if (it == TipoSalida.VENTA) Textos.Salida.VENTA_DESC else Textos.Salida.MERMA_DESC },
                icono = { IconosDominio.salida(it) },
                colores = {
                    if (it == TipoSalida.VENTA) ParColores(MaterialTheme.colorScheme.onPrimaryContainer, MaterialTheme.colorScheme.primaryContainer)
                    else ParColores(x.onWarningContainer, x.warningContainer)
                },
            )
        }
        SeccionFormulario(null) {
            GtStepper(
                valor = estado.cantidad,
                onValorChange = { onEvent(RegistrarSalidaEvent.CambiarCantidad(it)) },
                etiqueta = obligatorio(Textos.Salida.CANTIDAD),
                maximo = 99_999,
                error = estado.errorCantidad,
                ayuda = Textos.Salida.DISPONIBLES_AYUDA.conValores(estado.disponibles.formatoMiles()),
                placeholder = Textos.Salida.CANTIDAD_EJEMPLO,
            )
            AnimatedVisibility(
                visible = esMerma,
                enter = expandVertically(Motion.media()) + fadeIn(Motion.media()),
                exit = shrinkVertically(Motion.media()) + fadeOut(Motion.rapida()),
            ) {
                GtChipGroup(
                    titulo = obligatorio(Textos.Salida.MOTIVO),
                    opciones = MotivoMerma.entries,
                    seleccionado = estado.motivo,
                    onSeleccion = { onEvent(RegistrarSalidaEvent.ElegirMotivo(it)) },
                    etiqueta = { it.etiqueta },
                    colores = { ParColores(x.onWarningContainer, x.warningContainer) },
                )
            }
            GtDatePicker(Textos.Salida.FECHA, estado.fecha, { onEvent(RegistrarSalidaEvent.CambiarFecha(it)) })
        }
    }
    if (estado.confirmar) {
        val lote = estado.lote.datosOrNull
        val cantidad = estado.cantidad ?: 0
        ConfirmSheet(
            titulo = if (esMerma) Textos.Salida.CONFIRMAR_TITULO_MERMA else Textos.Salida.CONFIRMAR_TITULO_VENTA,
            mensaje = (if (esMerma) Textos.Salida.CONFIRMAR_MENSAJE_MERMA else Textos.Salida.CONFIRMAR_MENSAJE_VENTA)
                .conValores(cantidad.formatoMiles(), lote?.especie?.nombre.orEmpty()),
            textoConfirmar = if (esMerma) Textos.Salida.GUARDAR_MERMA else Textos.Salida.GUARDAR_VENTA,
            onConfirmar = { onEvent(RegistrarSalidaEvent.Confirmar) },
            onCancelar = { onEvent(RegistrarSalidaEvent.CancelarConfirmacion) },
            peligro = esMerma,
            icono = IconosDominio.salida(estado.tipo ?: TipoSalida.VENTA),
            cargando = estado.cargando,
        ) {
            // Resumen visible antes de confirmar.
            ResumenConfirmacion {
                lote?.let { ResumenFila(Textos.Salida.RESUMEN_LOTE, it.contexto()) }
                ResumenFila(Textos.Salida.RESUMEN_TIPO, if (esMerma) Textos.Salida.MERMA else Textos.Salida.VENTA)
                ResumenFila(Textos.Salida.RESUMEN_CANTIDAD, "${cantidad.formatoMiles()} ${Textos.Comun.PLANTAS}")
                if (esMerma) estado.motivo?.let { ResumenFila(Textos.Salida.RESUMEN_MOTIVO, it.etiqueta) }
                ResumenFila(Textos.Salida.RESUMEN_FECHA, FechasUi.dia(estado.fecha))
                ResumenFila(
                    Textos.Salida.RESUMEN_QUEDAN,
                    Textos.Lotes.DISPONIBLES_DE.conValores(estado.quedaran.formatoMiles(), estado.disponibles.formatoMiles())
                        .removeSuffix(" ${Textos.Lotes.DISPONIBLES}"),
                )
            }
        }
    }
}

private val estadoPreview = RegistrarSalidaUiState(lote = EstadoCarga.Listo(DatosPreview.lote), tipo = TipoSalida.MERMA, cantidad = 400)

@Preview
@Composable
private fun RegistrarSalidaPreviewClaro() {
    GreenTechTheme { RegistrarSalidaScreen(estadoPreview) {} }
}

@Preview
@Composable
private fun RegistrarSalidaPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { RegistrarSalidaScreen(estadoPreview.copy(tipo = TipoSalida.VENTA, cantidad = 20)) {} }
}
