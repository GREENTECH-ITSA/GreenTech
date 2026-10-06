package com.greentech.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.Motion

/**
 * Selector que abre una hoja inferior con opciones grandes. Con [buscable] agrega
 * un buscador arriba (útil para listas largas como especies).
 */
@Composable
fun <T> GtDropdown(
    etiqueta: String,
    opciones: List<T>,
    seleccionado: T?,
    onSeleccion: (T) -> Unit,
    textoOpcion: (T) -> String,
    modifier: Modifier = Modifier,
    placeholder: String = Textos.Comun.ELEGIR,
    icono: ImageVector? = null,
    subtextoOpcion: ((T) -> String)? = null,
    buscable: Boolean = false,
    etiquetaBusqueda: String = Textos.Comun.BUSCAR,
    error: String? = null,
) {
    var abierto by rememberSaveable { mutableStateOf(false) }
    CampoSeleccion(
        etiqueta = etiqueta,
        valor = seleccionado?.let(textoOpcion),
        placeholder = placeholder,
        icono = icono,
        iconoFinal = Icons.Outlined.ExpandMore,
        error = error,
        onClick = { abierto = true },
        modifier = modifier,
    )
    if (abierto) {
        var busqueda by rememberSaveable { mutableStateOf("") }
        val filtradas = remember(busqueda, opciones) {
            if (busqueda.isBlank()) opciones else opciones.filter { textoOpcion(it).contains(busqueda.trim(), ignoreCase = true) }
        }
        GtBottomSheet(onCerrar = { abierto = false }) {
            Column(Modifier.padding(horizontal = 20.dp)) {
                Text(etiqueta, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.size(12.dp))
                if (buscable) {
                    GtTextField(
                        valor = busqueda,
                        onValorChange = { busqueda = it },
                        etiqueta = etiquetaBusqueda,
                        icono = Icons.Outlined.Search,
                        accionIme = ImeAction.Search,
                    )
                    Spacer(Modifier.size(8.dp))
                }
            }
            if (filtradas.isEmpty()) {
                Text(
                    Textos.Comun.SIN_RESULTADOS,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(20.dp),
                )
            }
            LazyColumn(Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(bottom = 24.dp)) {
                items(filtradas, key = { textoOpcion(it) }) { op ->
                    val elegido = op == seleccionado
                    Surface(
                        onClick = { onSeleccion(op); abierto = false },
                        shape = GtShapes.campo,
                        color = if (elegido) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp).semantics {
                            role = Role.RadioButton
                            if (elegido) stateDescription = Textos.Comun.SELECCIONADO
                        },
                    ) {
                        Row(
                            Modifier.heightIn(min = 64.dp).padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(textoOpcion(op), style = MaterialTheme.typography.bodyLarge)
                                subtextoOpcion?.let {
                                    Text(it(op), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            if (elegido) Icon(Icons.Outlined.Check, null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                }
            }
        }
    }
}

/** Campo de solo lectura que se ve como un campo de texto (16 dp) y abre un selector al tocarlo. */
@Composable
internal fun CampoSeleccion(
    etiqueta: String,
    valor: String?,
    placeholder: String,
    icono: ImageVector?,
    iconoFinal: ImageVector,
    error: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaccion = remember { MutableInteractionSource() }
    val esquema = MaterialTheme.colorScheme
    val borde by animateColorAsState(if (error != null) esquema.error else esquema.outline, Motion.media(), label = "bordeCampo")
    Column(modifier.fillMaxWidth().sacudir(disparadorSacudida(error, enfocado = false))) {
        Text(etiqueta, style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.size(8.dp))
        Surface(
            onClick = onClick,
            interactionSource = interaccion,
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).efectoPresion(interaccion),
            shape = GtShapes.campo,
            color = esquema.surface,
            border = BorderStroke(if (error != null) 2.dp else 1.dp, borde),
        ) {
            Row(
                Modifier.heightIn(min = 56.dp).padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (icono != null) {
                    Icon(icono, null, tint = esquema.onSurfaceVariant)
                    Spacer(Modifier.width(12.dp))
                }
                Text(
                    valor ?: placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (valor == null) esquema.onSurfaceVariant else esquema.onSurface,
                    modifier = Modifier.weight(1f),
                )
                Icon(iconoFinal, null, tint = esquema.primary)
            }
        }
        if (error != null) {
            Spacer(Modifier.size(4.dp))
            MensajeError(error, Modifier.padding(start = 16.dp))
        }
    }
}
