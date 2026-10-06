package com.greentech.app.ui.screens.employee.monitoreo

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.runtime.Composable
import com.greentech.app.ui.components.InfoAyuda
import com.greentech.app.ui.components.contexto
import com.greentech.app.ui.components.obligatorio
import androidx.compose.ui.tooling.preview.Preview
import com.greentech.app.ui.components.FormularioScaffold
import com.greentech.app.ui.components.GtChipGroup
import com.greentech.app.ui.components.GtTextField
import com.greentech.app.ui.components.IconosDominio
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.LoteResumenEstado
import com.greentech.app.ui.components.SeccionFormulario
import com.greentech.app.ui.components.colores
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.preview.DatosPreview
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.Motion
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.shared.domain.model.Severidad
import com.greentech.shared.domain.model.TipoAnomalia

@Composable
fun AnomaliaScreen(estado: AnomaliaUiState, onEvent: (AnomaliaEvent) -> Unit) {
    FormularioScaffold(
        titulo = Textos.Anomalia.TITULO,
        subtitulo = estado.lote.contexto(),
        onCerrar = { onEvent(AnomaliaEvent.Volver) },
        onAyuda = { onEvent(AnomaliaEvent.Ayuda) },
        hayCambios = estado.hayCambios,
        textoGuardar = Textos.Anomalia.GUARDAR,
        onGuardar = { onEvent(AnomaliaEvent.Guardar) },
        habilitado = estado.puedeGuardar,
        cargando = estado.cargando,
        faltantes = estado.faltantes,
        icono = IconosModulo.anomalia,
    ) {
        LoteResumenEstado(estado.lote)
        SeccionFormulario(
            obligatorio(Textos.Anomalia.TIPO),
            info = InfoAyuda(Textos.Anomalia.INFO_TITULO, Textos.Anomalia.INFO),
            onVerAyuda = { onEvent(AnomaliaEvent.Ayuda) },
        ) {
            GtChipGroup(
                opciones = TipoAnomalia.entries,
                seleccionado = estado.tipo,
                onSeleccion = { onEvent(AnomaliaEvent.ElegirTipo(it)) },
                etiqueta = { it.etiqueta },
                icono = { IconosDominio.anomalia(it) },
                colores = { GreenTechTheme.colors.error },
            )
            AnimatedContent(
                targetState = estado.tipo,
                transitionSpec = { fadeIn(Motion.media()) togetherWith fadeOut(Motion.rapida()) },
                label = "catalogo",
            ) { tipo ->
                if (tipo != null) {
                    GtChipGroup(
                        titulo = obligatorio("${Textos.Anomalia.OPCION} (${tipo.etiqueta.lowercase()})"),
                        opciones = tipo.catalogo,
                        seleccionado = estado.nombre,
                        onSeleccion = { onEvent(AnomaliaEvent.ElegirNombre(it)) },
                        etiqueta = { it },
                    )
                }
            }
        }
        SeccionFormulario(obligatorio(Textos.Anomalia.SEVERIDAD)) {
            GtChipGroup(
                opciones = Severidad.entries,
                seleccionado = estado.severidad,
                onSeleccion = { onEvent(AnomaliaEvent.ElegirSeveridad(it)) },
                etiqueta = { it.etiqueta },
                icono = { Icons.Outlined.ErrorOutline },
                colores = { it.colores() },
                columnas = 3,
            )
        }
        SeccionFormulario(null) {
            GtTextField(
                valor = estado.descripcion,
                onValorChange = { onEvent(AnomaliaEvent.DescripcionCambio(it)) },
                etiqueta = obligatorio(Textos.Anomalia.DESCRIPCION),
                placeholder = Textos.Anomalia.DESCRIPCION_EJEMPLO,
                icono = IconosModulo.observacion,
                error = estado.errorDescripcion,
                lineas = 3,
            )
        }
    }
}

private val estadoPreview = AnomaliaUiState(
    lote = EstadoCarga.Listo(DatosPreview.lote), tipo = TipoAnomalia.PLAGA, nombre = "Pulgón", severidad = Severidad.ALTA,
)

@Preview
@Composable
private fun AnomaliaPreviewClaro() {
    GreenTechTheme { AnomaliaScreen(estadoPreview) {} }
}

@Preview
@Composable
private fun AnomaliaPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { AnomaliaScreen(estadoPreview.copy(intentoGuardar = true)) {} }
}
