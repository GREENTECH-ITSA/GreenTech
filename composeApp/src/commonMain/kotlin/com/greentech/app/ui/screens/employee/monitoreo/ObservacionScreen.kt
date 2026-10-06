package com.greentech.app.ui.screens.employee.monitoreo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.focusRequester
import com.greentech.app.ui.components.contexto
import com.greentech.app.ui.components.obligatorio
import com.greentech.app.ui.components.rememberEnfoqueInicial
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.FormularioScaffold
import com.greentech.app.ui.components.GtButton
import com.greentech.app.ui.components.GtButtonVariant
import com.greentech.app.ui.components.GtChipGroup
import com.greentech.app.ui.components.GtTextField
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.LoteResumenEstado
import com.greentech.app.ui.components.SeccionFormulario
import com.greentech.app.ui.components.colores
import com.greentech.app.ui.components.icono
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.preview.DatosPreview
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.shared.domain.model.EstadoSalud

@Composable
fun ObservacionScreen(estado: ObservacionUiState, onEvent: (ObservacionEvent) -> Unit) {
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
        SeccionFormulario(Textos.Observacion.ESTADO) {
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
                    ) { Icon(Icons.Outlined.Image, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(32.dp)) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(Textos.Observacion.FOTO_AGREGADA, style = MaterialTheme.typography.titleSmall)
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
