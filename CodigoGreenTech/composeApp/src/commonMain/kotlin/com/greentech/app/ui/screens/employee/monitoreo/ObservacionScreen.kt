package com.greentech.app.ui.screens.employee.monitoreo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greentech.app.platform.MiniaturaFoto
import com.greentech.app.platform.Permiso
import com.greentech.app.platform.rememberEstadoPermiso
import com.greentech.app.platform.rememberSelectorFoto
import com.greentech.app.ui.components.BotonLeyendaSalud
import com.greentech.app.ui.components.ConfirmSheet
import com.greentech.app.ui.components.FormularioScaffold
import com.greentech.app.ui.components.GtBottomSheet
import com.greentech.app.ui.components.GtButton
import com.greentech.app.ui.components.GtButtonVariant
import com.greentech.app.ui.components.GtChipGroup
import com.greentech.app.ui.components.GtTextField
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.LoteResumenEstado
import com.greentech.app.ui.components.SeccionFormulario
import com.greentech.app.ui.components.colores
import com.greentech.app.ui.components.contexto
import com.greentech.app.ui.components.icono
import com.greentech.app.ui.components.obligatorio
import com.greentech.app.ui.components.rememberEnfoqueInicial
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.preview.DatosPreview
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.shared.domain.model.EstadoSalud

@Composable
fun ObservacionScreen(estado: ObservacionUiState, onEvent: (ObservacionEvent) -> Unit) {
    val selector = rememberSelectorFoto { onEvent(ObservacionEvent.FotoElegida(it)) }
    val permisoCamara = rememberEstadoPermiso(Permiso.CAMARA) { permitido -> if (permitido) selector.tomarFoto() }
    FormularioScaffold(
        titulo = Textos.Observacion.TITULO,
        subtitulo = estado.lote.contexto(),
        onCerrar = { onEvent(ObservacionEvent.Volver) },
        onAyuda = { onEvent(ObservacionEvent.Ayuda) },
        hayCambios = estado.hayCambios,
        textoGuardar = Textos.Observacion.GUARDAR,
        onGuardar = { onEvent(ObservacionEvent.Guardar) },
        habilitado = estado.puedeGuardar,
        cargando = estado.cargando,
        faltantes = estado.faltantes,
        icono = IconosModulo.observacion,
    ) {
        LoteResumenEstado(estado.lote)
        SeccionFormulario(null) {
            GtTextField(
                valor = estado.texto,
                onValorChange = { onEvent(ObservacionEvent.TextoCambio(it)) },
                etiqueta = obligatorio(Textos.Observacion.TEXTO),
                modifier = Modifier.focusRequester(rememberEnfoqueInicial()),
                placeholder = Textos.Observacion.TEXTO_EJEMPLO,
                icono = IconosModulo.observacion,
                error = estado.errorTexto,
                lineas = 4,
            )
        }
        SeccionFormulario(Textos.Observacion.ESTADO, accionTitulo = { BotonLeyendaSalud() }) {
            GtChipGroup(
                opciones = EstadoSalud.entries,
                seleccionado = estado.salud,
                onSeleccion = { onEvent(ObservacionEvent.ElegirSalud(it)) },
                etiqueta = { it.etiqueta },
                icono = { it.icono() },
                colores = { it.colores() },
                columnas = 2,
            )
        }
        SeccionFormulario(null) {
            if (estado.conFoto) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(88.dp).clip(GtShapes.campo).background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Outlined.Image, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(Dimens.iconoGrande))
                        estado.fotoUri?.let { MiniaturaFoto(it, Textos.Observacion.FOTO_DESCRIPCION, Modifier.matchParentSize()) }
                    }
                    Spacer(Modifier.width(Dimens.md))
                    Column(Modifier.weight(1f)) {
                        Text(Textos.Observacion.FOTO_AGREGADA, style = GtTexto.tituloTarjeta)
                        GtButton(
                            Textos.Observacion.QUITAR_FOTO, { onEvent(ObservacionEvent.QuitarFoto) },
                            variante = GtButtonVariant.Texto, icono = Icons.Outlined.DeleteOutline,
                        )
                    }
                }
            } else {
                GtButton(
                    Textos.Observacion.FOTO, { onEvent(ObservacionEvent.AgregarFoto) },
                    variante = GtButtonVariant.Tonal, icono = Icons.Outlined.AddAPhoto,
                )
            }
        }
    }
    if (estado.mostrarOpcionesFoto) {
        HojaAgregarFoto(
            puedeTomarFoto = selector.puedeTomarFoto,
            onTomarFoto = {
                onEvent(ObservacionEvent.CerrarOpcionesFoto)
                // El permiso de cámara se explica antes de pedirlo (o se manda a Ajustes si se negó para siempre).
                // Sin permiso: diálogo del sistema directo; al permitir se abre la cámara (ver rememberEstadoPermiso).
                if (permisoCamara.concedido) selector.tomarFoto() else permisoCamara.solicitar()
            },
            onElegirGaleria = {
                onEvent(ObservacionEvent.CerrarOpcionesFoto)
                selector.elegirDeGaleria()
            },
            onCerrar = { onEvent(ObservacionEvent.CerrarOpcionesFoto) },
        )
    }
}

/** "Agregar foto": tomarla con la cámara o elegirla de la galería. */
@Composable
private fun HojaAgregarFoto(
    puedeTomarFoto: Boolean,
    onTomarFoto: () -> Unit,
    onElegirGaleria: () -> Unit,
    onCerrar: () -> Unit,
) {
    GtBottomSheet(onCerrar = onCerrar) {
        Column(
            Modifier.padding(start = Dimens.margenLateral, end = Dimens.margenLateral, bottom = Dimens.xl),
            verticalArrangement = Arrangement.spacedBy(Dimens.md),
        ) {
            Text(Textos.Observacion.FOTO_TITULO, style = GtTexto.tituloTarjeta, modifier = Modifier.semantics { heading() })
            if (puedeTomarFoto) {
                GtButton(Textos.Observacion.TOMAR_FOTO, onTomarFoto, icono = Icons.Outlined.PhotoCamera)
            }
            GtButton(
                Textos.Observacion.ELEGIR_GALERIA, onElegirGaleria,
                variante = if (puedeTomarFoto) GtButtonVariant.Tonal else GtButtonVariant.Primario,
                icono = Icons.Outlined.PhotoLibrary,
            )
        }
    }
}

@Preview
@Composable
private fun ObservacionPreviewClaro() {
    GreenTechTheme { ObservacionScreen(ObservacionUiState(lote = EstadoCarga.Listo(DatosPreview.lote), conFoto = true)) {} }
}

@Preview
@Composable
private fun ObservacionPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { ObservacionScreen(ObservacionUiState(lote = EstadoCarga.Listo(DatosPreview.lote))) {} }
}
