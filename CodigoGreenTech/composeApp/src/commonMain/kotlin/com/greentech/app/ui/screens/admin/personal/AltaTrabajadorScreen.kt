package com.greentech.app.ui.screens.admin.personal

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.greentech.app.ui.components.ConfirmSheet
import com.greentech.app.ui.components.FormularioScaffold
import com.greentech.app.ui.components.GtButton
import com.greentech.app.ui.components.GtButtonVariant
import com.greentech.app.ui.components.GtChipGroup
import com.greentech.app.ui.components.GtTextField
import com.greentech.app.ui.components.ResumenConfirmacion
import com.greentech.app.ui.components.ResumenFila
import com.greentech.app.ui.components.SeccionFormulario
import com.greentech.app.ui.components.obligatorio
import com.greentech.app.ui.components.rememberEnfoqueInicial
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.strings.conValores
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.shared.domain.model.Rol

/**
 * Alta de trabajador: una columna (nombre → correo → contraseña → rol), "Generar contraseña segura"
 * tonal y resumen en hoja inferior antes de confirmar.
 */
@Composable
fun AltaTrabajadorScreen(estado: AltaTrabajadorUiState, onEvent: (AltaTrabajadorEvent) -> Unit) {
    val foco = LocalFocusManager.current
    FormularioScaffold(
        titulo = Textos.AltaTrabajador.TITULO,
        onCerrar = { onEvent(AltaTrabajadorEvent.Volver) },
        onAyuda = { onEvent(AltaTrabajadorEvent.Ayuda) },
        textoGuardar = Textos.AltaTrabajador.GUARDAR,
        onGuardar = { onEvent(AltaTrabajadorEvent.Guardar) },
        habilitado = estado.puedeGuardar,
        cargando = estado.cargando,
        hayCambios = estado.hayCambios,
        faltantes = estado.faltantes,
        icono = Icons.Outlined.PersonAddAlt,
    ) {
        SeccionFormulario(null) {
            GtTextField(
                valor = estado.nombre,
                onValorChange = { onEvent(AltaTrabajadorEvent.NombreCambio(it)) },
                etiqueta = obligatorio(Textos.AltaTrabajador.NOMBRE),
                placeholder = Textos.AltaTrabajador.NOMBRE_EJEMPLO,
                icono = Icons.Outlined.Person,
                error = estado.errorNombre,
                mayusculas = KeyboardCapitalization.Words,
                modifier = Modifier.focusRequester(rememberEnfoqueInicial()),
            )
            GtTextField(
                valor = estado.correo,
                onValorChange = { onEvent(AltaTrabajadorEvent.CorreoCambio(it)) },
                etiqueta = obligatorio(Textos.AltaTrabajador.CORREO),
                placeholder = Textos.Login.CORREO_EJEMPLO,
                icono = Icons.Outlined.Email,
                error = estado.errorCorreo,
                tipoTeclado = KeyboardType.Email,
            )
        }
        SeccionFormulario(null) {
            GtTextField(
                valor = estado.password,
                onValorChange = { onEvent(AltaTrabajadorEvent.PasswordCambio(it)) },
                etiqueta = obligatorio(Textos.AltaTrabajador.PASSWORD),
                icono = Icons.Outlined.Key,
                error = estado.errorPassword,
                ayuda = Textos.AltaTrabajador.PASSWORD_AYUDA,
                accionIme = ImeAction.Done,
                alAccionIme = {
                    foco.clearFocus()
                    onEvent(AltaTrabajadorEvent.Guardar)
                },
            )
            GtButton(
                Textos.AltaTrabajador.GENERAR,
                { onEvent(AltaTrabajadorEvent.GenerarPassword) },
                variante = GtButtonVariant.Tonal,
                icono = Icons.Outlined.AutoAwesome,
            )
        }
        SeccionFormulario(obligatorio(Textos.AltaTrabajador.ROL)) {
            GtChipGroup(
                opciones = Rol.entries,
                seleccionado = estado.rol,
                onSeleccion = { onEvent(AltaTrabajadorEvent.ElegirRol(it)) },
                etiqueta = { it.etiqueta },
                icono = { iconoRol(it) },
                columnas = 2,
            )
        }
    }
    if (estado.confirmar) {
        ConfirmSheet(
            titulo = Textos.AltaTrabajador.CONFIRMAR_TITULO.conValores(estado.nombre.trim()),
            mensaje = Textos.AltaTrabajador.CONFIRMAR_MENSAJE,
            textoConfirmar = Textos.AltaTrabajador.GUARDAR,
            onConfirmar = { onEvent(AltaTrabajadorEvent.Confirmar) },
            onCancelar = { onEvent(AltaTrabajadorEvent.CancelarConfirmacion) },
            icono = Icons.Outlined.PersonAddAlt,
            cargando = estado.cargando,
        ) {
            ResumenConfirmacion {
                ResumenFila(Textos.AltaTrabajador.NOMBRE, estado.nombre.trim())
                ResumenFila(Textos.AltaTrabajador.CORREO, estado.correo)
                ResumenFila(Textos.AltaTrabajador.ROL, estado.rol?.etiqueta.orEmpty())
                ResumenFila(Textos.AltaTrabajador.PASSWORD, estado.password)
            }
        }
    }
}

private fun iconoRol(rol: Rol) = if (rol == Rol.ADMINISTRADOR) Icons.Outlined.AdminPanelSettings else Icons.Outlined.Badge

@Preview
@Composable
private fun AltaTrabajadorPreviewClaro() {
    GreenTechTheme { AltaTrabajadorScreen(AltaTrabajadorUiState(nombre = "Ana 2", correo = "ana@")) {} }
}

@Preview
@Composable
private fun AltaTrabajadorPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) {
        AltaTrabajadorScreen(AltaTrabajadorUiState(nombre = "Ana López", correo = "ana@gmail.com", password = "verde2026", confirmar = true)) {}
    }
}
