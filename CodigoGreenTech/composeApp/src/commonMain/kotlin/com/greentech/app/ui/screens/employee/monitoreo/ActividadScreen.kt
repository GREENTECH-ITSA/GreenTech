package com.greentech.app.ui.screens.employee.monitoreo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.greentech.app.ui.components.FormularioScaffold
import com.greentech.app.ui.components.GtChipGroup
import com.greentech.app.ui.components.GtTextField
import com.greentech.app.ui.components.IconosDominio
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.LoteResumenEstado
import com.greentech.app.ui.components.SeccionFormulario
import com.greentech.app.ui.components.contexto
import com.greentech.app.ui.components.obligatorio
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.preview.DatosPreview
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.shared.domain.model.TipoActividad

@Composable
fun ActividadScreen(estado: ActividadUiState, onEvent: (ActividadEvent) -> Unit) {
    FormularioScaffold(
        titulo = Textos.Actividad.TITULO,
        subtitulo = estado.lote.contexto(),
        onCerrar = { onEvent(ActividadEvent.Volver) },
        onAyuda = { onEvent(ActividadEvent.Ayuda) },
        hayCambios = estado.hayCambios,
        textoGuardar = Textos.Actividad.GUARDAR,
        onGuardar = { onEvent(ActividadEvent.Guardar) },
        habilitado = estado.puedeGuardar,
        cargando = estado.cargando,
        faltantes = estado.faltantes,
        icono = IconosModulo.actividad,
    ) {
        LoteResumenEstado(estado.lote)
        SeccionFormulario(obligatorio(Textos.Actividad.TIPO)) {
            GtChipGroup(
                opciones = TipoActividad.entries,
                seleccionado = estado.tipo,
                onSeleccion = { onEvent(ActividadEvent.ElegirTipo(it)) },
                etiqueta = { it.etiqueta },
                icono = { IconosDominio.actividad(it) },
                columnas = 2,
            )
        }
        SeccionFormulario(null) {
            GtTextField(
                valor = estado.observacion,
                onValorChange = { onEvent(ActividadEvent.ObservacionCambio(it)) },
                etiqueta = Textos.Actividad.OBSERVACION,
                placeholder = Textos.Actividad.OBSERVACION_EJEMPLO,
                icono = IconosModulo.observacion,
                lineas = 3,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Schedule, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(Dimens.md))
                Column {
                    Text(
                        "${Textos.Comun.RESPONSABLE}: ${estado.responsable} · ${estado.fechaHora}",
                        style = GtTexto.ayuda,
                    )
                    Text(
                        Textos.Actividad.AUTOMATICO,
                        style = GtTexto.ayuda,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private val estadoPreview = ActividadUiState(
    lote = EstadoCarga.Listo(DatosPreview.lote), tipo = TipoActividad.PODA, responsable = "Juan Pérez", fechaHora = "Hoy, 10:24",
)

@Preview
@Composable
private fun ActividadPreviewClaro() {
    GreenTechTheme { ActividadScreen(estadoPreview) {} }
}

@Preview
@Composable
private fun ActividadPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { ActividadScreen(estadoPreview) {} }
}
