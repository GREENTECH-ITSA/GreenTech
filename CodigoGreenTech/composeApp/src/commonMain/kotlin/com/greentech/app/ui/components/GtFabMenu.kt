package com.greentech.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.GtTexto

/** Acción del menú del FAB (ícono + texto). */
data class AccionFab(val texto: String, val icono: ImageVector, val onClick: () -> Unit)

/**
 * FAB "+" expresivo de Material 3 (FloatingActionButtonMenu): al tocarlo se transforma en "×" y
 * despliega sus acciones con ícono y texto. Espacio para agregar más acciones en el futuro.
 */
@Composable
fun GtFabMenu(acciones: List<AccionFab>, modifier: Modifier = Modifier) {
    var abierto by rememberSaveable { mutableStateOf(false) }
    FloatingActionButtonMenu(
        expanded = abierto,
        modifier = modifier,
        button = {
            ToggleFloatingActionButton(
                checked = abierto,
                onCheckedChange = { abierto = it },
                modifier = Modifier.semantics {
                    contentDescription = if (abierto) Textos.Disenio.CERRAR_MENU else Textos.Disenio.MAS_ACCIONES
                    stateDescription = if (abierto) Textos.Disenio.MENU_ABIERTO else Textos.Disenio.MENU_CERRADO
                },
            ) {
                Icon(
                    if (checkedProgress > 0.5f) Icons.Outlined.Close else Icons.Outlined.Add,
                    contentDescription = null,
                    modifier = Modifier.animateIcon({ checkedProgress }),
                )
            }
        },
    ) {
        acciones.forEach { accion ->
            FloatingActionButtonMenuItem(
                onClick = {
                    abierto = false
                    accion.onClick()
                },
                text = { Text(accion.texto, style = GtTexto.boton) },
                icon = { Icon(accion.icono, contentDescription = null) },
            )
        }
    }
}
