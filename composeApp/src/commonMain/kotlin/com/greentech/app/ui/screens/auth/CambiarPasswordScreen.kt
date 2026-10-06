package com.greentech.app.ui.screens.auth

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.greentech.app.ui.components.FormularioScaffold
import com.greentech.app.ui.components.SeccionFormulario
import com.greentech.app.ui.components.GtTextField
import com.greentech.app.ui.components.obligatorio
import com.greentech.app.ui.components.rememberEnfoqueInicial
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import com.greentech.app.ui.strings.Textos
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.Motion

@Composable
fun CambiarPasswordScreen(estado: CambiarPasswordUiState, onEvent: (CambiarPasswordEvent) -> Unit) {
    val foco = LocalFocusManager.current
    FormularioScaffold(
        titulo = Textos.Password.TITULO,
        onCerrar = { onEvent(CambiarPasswordEvent.Volver) },
        onAyuda = { onEvent(CambiarPasswordEvent.Ayuda) },
        hayCambios = estado.hayCambios,
        textoGuardar = Textos.Password.GUARDAR,
        onGuardar = { onEvent(CambiarPasswordEvent.Guardar) },
        habilitado = estado.puedeGuardar,
        cargando = estado.cargando,
        faltantes = estado.faltantes,
    ) {
        GtTextField(
            valor = estado.actual,
            onValorChange = { onEvent(CambiarPasswordEvent.ActualCambio(it)) },
            etiqueta = obligatorio(Textos.Password.ACTUAL),
            modifier = Modifier.focusRequester(rememberEnfoqueInicial()),
            icono = Icons.Outlined.Lock,
            esPassword = true,
            error = estado.errorActual,
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            GtTextField(
                valor = estado.nueva,
                onValorChange = { onEvent(CambiarPasswordEvent.NuevaCambio(it)) },
                etiqueta = obligatorio(Textos.Password.NUEVA),
                icono = Icons.Outlined.Key,
                esPassword = true,
                error = estado.errorNueva,
            )
            SeccionFormulario(Textos.Password.REQUISITOS) {
                Requisito(Textos.Password.REQ_LONGITUD, estado.requisitos.longitud)
                Requisito(Textos.Password.REQ_LETRA, estado.requisitos.letra)
                Requisito(Textos.Password.REQ_NUMERO, estado.requisitos.numero)
            }
        }
        GtTextField(
            valor = estado.confirmacion,
            onValorChange = { onEvent(CambiarPasswordEvent.ConfirmacionCambio(it)) },
            etiqueta = obligatorio(Textos.Password.CONFIRMAR),
            icono = Icons.Outlined.LockReset,
            esPassword = true,
            error = estado.errorConfirmacion,
            accionIme = ImeAction.Done,
            alAccionIme = {
                foco.clearFocus()
                onEvent(CambiarPasswordEvent.Guardar)
            },
        )
    }
}

@Composable
private fun Requisito(texto: String, cumplido: Boolean) {
    val color by animateColorAsState(
        if (cumplido) GreenTechTheme.colors.exito.contenido else MaterialTheme.colorScheme.onSurfaceVariant,
        Motion.media(),
        label = "requisito",
    )
    Row(
        Modifier.heightIn(min = 32.dp).semantics(mergeDescendants = true) {
            stateDescription = if (cumplido) Textos.Password.CUMPLIDO else Textos.Password.PENDIENTE
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            if (cumplido) Icons.Outlined.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp),
        )
        Text(texto, style = MaterialTheme.typography.bodyMedium, color = color)
    }
}

@Preview
@Composable
private fun CambiarPasswordPreviewClaro() {
    GreenTechTheme {
        CambiarPasswordScreen(CambiarPasswordUiState(actual = "empleado123", nueva = "verde2026", confirmacion = "verde")) {}
    }
}

@Preview
@Composable
private fun CambiarPasswordPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) {
        CambiarPasswordScreen(CambiarPasswordUiState(nueva = "verde")) {}
    }
}
