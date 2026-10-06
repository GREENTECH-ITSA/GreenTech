package com.greentech.app.ui.screens.employee.lotes

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Warehouse
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.FormularioScaffold
import com.greentech.app.ui.components.GtChipGroup
import com.greentech.app.ui.components.GtDatePicker
import com.greentech.app.ui.components.GtDropdown
import com.greentech.app.ui.components.GtStepper
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.SeccionFormulario
import com.greentech.app.ui.components.obligatorio
import com.greentech.app.ui.preview.DatosPreview
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.ThemeMode

/** Registrar lote: secciones agrupadas, stepper grande y botón fijo abajo sobre vidrio. */
@Composable
fun RegistrarLoteScreen(estado: RegistrarLoteUiState, onEvent: (RegistrarLoteEvent) -> Unit) {
    FormularioScaffold(
        titulo = Textos.RegistrarLote.TITULO,
        onCerrar = { onEvent(RegistrarLoteEvent.Volver) },
        onAyuda = { onEvent(RegistrarLoteEvent.Ayuda) },
        textoGuardar = Textos.RegistrarLote.GUARDAR,
        onGuardar = { onEvent(RegistrarLoteEvent.Guardar) },
        habilitado = estado.puedeGuardar,
        cargando = estado.cargando,
        hayCambios = estado.hayCambios,
        faltantes = estado.faltantes,
        icono = Icons.Outlined.Save,
    ) {
        SeccionFormulario(null) {
            GtDropdown(
                etiqueta = obligatorio(Textos.RegistrarLote.ESPECIE),
                opciones = estado.especies,
                seleccionado = estado.especie,
                onSeleccion = { onEvent(RegistrarLoteEvent.ElegirEspecie(it)) },
                textoOpcion = { it.nombre },
                subtextoOpcion = { it.nombreCientifico },
                placeholder = Textos.RegistrarLote.ESPECIE_PLACEHOLDER,
                icono = IconosModulo.lote,
                buscable = true,
                etiquetaBusqueda = Textos.RegistrarLote.BUSCAR_ESPECIE,
                error = estado.errorEspecie,
            )
            GtChipGroup(
                titulo = obligatorio(Textos.RegistrarLote.ZONA),
                opciones = estado.zonas,
                seleccionado = estado.zona,
                onSeleccion = { onEvent(RegistrarLoteEvent.ElegirZona(it)) },
                etiqueta = { it.nombre },
                icono = { Icons.Outlined.Warehouse },
                error = estado.errorZona,
            )
            if (estado.zonaSugerida != null && estado.zona == estado.zonaSugerida) {
                Text(
                    Textos.RegistrarLote.ZONA_AYUDA,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        SeccionFormulario(null) {
            GtStepper(
                valor = estado.cantidad,
                onValorChange = { onEvent(RegistrarLoteEvent.CambiarCantidad(it)) },
                etiqueta = obligatorio(Textos.RegistrarLote.CANTIDAD),
                paso = 10,
                error = estado.errorCantidad,
                ayuda = Textos.Comun.STEPPER_AYUDA,
                placeholder = Textos.RegistrarLote.CANTIDAD_EJEMPLO,
            )
        }
        SeccionFormulario(null) {
            GtDatePicker(
                etiqueta = Textos.RegistrarLote.FECHA,
                fecha = estado.fecha,
                onFechaChange = { onEvent(RegistrarLoteEvent.CambiarFecha(it)) },
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Person, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(12.dp))
                Text("${Textos.Comun.RESPONSABLE}: ${estado.responsable}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

private val estadoPreview = RegistrarLoteUiState(
    especies = DatosPreview.especies, zonas = DatosPreview.zonas, responsable = "Juan Pérez", intentoGuardar = true,
)

@Preview
@Composable
private fun RegistrarLotePreviewClaro() {
    GreenTechTheme { RegistrarLoteScreen(estadoPreview) {} }
}

@Preview
@Composable
private fun RegistrarLotePreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { RegistrarLoteScreen(estadoPreview.copy(cantidad = 120)) {} }
}
