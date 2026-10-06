package com.greentech.app.ui.screens.employee.monitoreo

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import com.greentech.app.ui.components.InfoAyuda
import com.greentech.app.ui.components.contexto
import com.greentech.app.ui.components.obligatorio
import com.greentech.app.ui.strings.conValores
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.FormularioScaffold
import com.greentech.app.ui.components.GtChipGroup
import com.greentech.app.ui.components.GtTextField
import com.greentech.app.ui.components.IconosDominio
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.LoteResumenEstado
import com.greentech.app.ui.components.SeccionFormulario
import com.greentech.app.ui.components.StatusChip
import com.greentech.app.ui.components.colores
import com.greentech.app.ui.components.efectoPresion
import com.greentech.app.ui.components.icono
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.preview.DatosPreview
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.Motion
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.shared.domain.model.EstadoSalud
import com.greentech.shared.domain.model.Etapa

@Composable
fun EtapaScreen(estado: EtapaUiState, onEvent: (EtapaEvent) -> Unit) {
    FormularioScaffold(
        titulo = Textos.Etapa.TITULO,
        subtitulo = estado.lote.contexto(),
        onCerrar = { onEvent(EtapaEvent.Volver) },
        onAyuda = { onEvent(EtapaEvent.Ayuda) },
        hayCambios = estado.hayCambios,
        textoGuardar = Textos.Etapa.GUARDAR,
        onGuardar = { onEvent(EtapaEvent.Guardar) },
        habilitado = estado.puedeGuardar,
        cargando = estado.cargando,
        faltantes = estado.faltantes,
        icono = IconosModulo.etapa,
    ) {
        LoteResumenEstado(estado.lote)
        SeccionFormulario(
            obligatorio(Textos.Etapa.LINEA),
            info = InfoAyuda(Textos.Etapa.INFO_TITULO, Textos.Etapa.INFO),
            onVerAyuda = { onEvent(EtapaEvent.Ayuda) },
        ) {
            estado.etapaActual?.let {
                Text(
                    Textos.Etapa.ETAPA_ACTUAL.conValores(it.etiqueta),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(Modifier.selectableGroup()) {
                Etapa.entries.forEach { etapa ->
                    PasoEtapa(
                        etapa = etapa,
                        actual = etapa == estado.etapaActual,
                        alcanzada = estado.etapaActual?.let { etapa.ordinal <= it.ordinal } ?: false,
                        seleccionada = etapa == estado.etapa,
                        esUltima = etapa == Etapa.entries.last(),
                        onClick = { onEvent(EtapaEvent.ElegirEtapa(etapa)) },
                    )
                }
            }
        }
        SeccionFormulario(Textos.Etapa.SALUD) {
            GtChipGroup(
                opciones = EstadoSalud.entries,
                seleccionado = estado.salud,
                onSeleccion = { onEvent(EtapaEvent.ElegirSalud(it)) },
                etiqueta = { it.etiqueta },
                icono = { it.icono() },
                colores = { it.colores() },
                columnas = 2,
            )
        }
        SeccionFormulario(null) {
            GtTextField(
                valor = estado.nota,
                onValorChange = { onEvent(EtapaEvent.NotaCambio(it)) },
                etiqueta = Textos.Etapa.NOTA,
                placeholder = Textos.Etapa.NOTA_EJEMPLO,
                icono = Icons.AutoMirrored.Outlined.Notes,
                lineas = 3,
            )
        }
    }
}

/** Paso de la línea de etapas: nodo + línea conectora + fila seleccionable grande. */
@Composable
private fun PasoEtapa(etapa: Etapa, actual: Boolean, alcanzada: Boolean, seleccionada: Boolean, esUltima: Boolean, onClick: () -> Unit) {
    val esquema = MaterialTheme.colorScheme
    val fondo by animateColorAsState(
        if (seleccionada) esquema.secondaryContainer else esquema.surface, Motion.media(), label = "paso",
    )
    val interaccion = remember { MutableInteractionSource() }
    Row(Modifier.height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(44.dp)) {
            Box(
                Modifier.padding(top = 10.dp).size(44.dp).clip(GtShapes.circulo)
                    .background(if (alcanzada) esquema.primary else esquema.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    IconosDominio.etapa(etapa), null,
                    tint = if (alcanzada) esquema.onPrimary else esquema.onPrimaryContainer,
                )
            }
            if (!esUltima) {
                Box(Modifier.width(2.dp).weight(1f).fillMaxHeight().background(if (alcanzada) esquema.primary else esquema.outlineVariant))
            }
        }
        Spacer(Modifier.width(12.dp))
        Surface(
            modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                .efectoPresion(interaccion)
                .clip(GtShapes.campo)
                .selectable(seleccionada, onClick = onClick, role = Role.RadioButton, interactionSource = interaccion, indication = ripple()),
            shape = GtShapes.campo,
            color = fondo,
            border = BorderStroke(1.dp, if (seleccionada) Color.Transparent else esquema.outline),
        ) {
            Row(
                Modifier.heightIn(min = 64.dp).padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    etapa.etiqueta,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (seleccionada) esquema.onSecondaryContainer else esquema.onSurface,
                    modifier = Modifier.weight(1f),
                )
                if (actual) StatusChip(Textos.Etapa.ACTUAL, GreenTechTheme.colors.neutro)
                if (seleccionada) {
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Outlined.CheckCircle, null, tint = esquema.onSecondaryContainer)
                }
            }
        }
    }
}

private val estadoPreview = EtapaUiState(lote = EstadoCarga.Listo(DatosPreview.lote), etapa = Etapa.LISTA_PARA_VENTA)

@Preview
@Composable
private fun EtapaPreviewClaro() {
    GreenTechTheme { EtapaScreen(estadoPreview) {} }
}

@Preview
@Composable
private fun EtapaPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { EtapaScreen(estadoPreview) {} }
}
