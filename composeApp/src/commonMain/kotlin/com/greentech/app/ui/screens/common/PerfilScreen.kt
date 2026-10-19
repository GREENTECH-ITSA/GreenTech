package com.greentech.app.ui.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Animation
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.greentech.app.platform.EstadoPermiso
import com.greentech.app.platform.Permiso
import com.greentech.app.platform.rememberEstadoPermiso
import com.greentech.app.ui.components.AlReseleccionarPestana
import com.greentech.app.ui.components.ConfirmDialog
import com.greentech.app.ui.components.ConfirmSheet
import com.greentech.app.ui.components.EmblemaGreenTech
import com.greentech.app.ui.components.GtButton
import com.greentech.app.ui.components.GtButtonVariant
import com.greentech.app.ui.components.GtScaffold
import com.greentech.app.ui.components.GtSegmentado
import com.greentech.app.ui.components.GtTopBar
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.LocalIntroduccion
import com.greentech.app.ui.components.StatusChip
import com.greentech.app.ui.components.efectoPresion
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.LocalApariencia
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.shared.domain.model.Rol
import com.greentech.shared.domain.model.Usuario
import com.greentech.shared.util.Fechas

@Composable
fun PerfilScreen(estado: PerfilUiState, onEvent: (PerfilEvent) -> Unit) {
    val intro = LocalIntroduccion.current
    val desplazamiento = rememberScrollState()
    AlReseleccionarPestana { desplazamiento.animateScrollTo(0) }
    GtScaffold(topBar = { GtTopBar(Textos.Perfil.TITULO, onAyuda = { onEvent(PerfilEvent.Ayuda) }, principal = true) }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(desplazamiento)
                .padding(padding)
                .padding(horizontal = Dimens.margenLateral, vertical = Dimens.lg),
            verticalArrangement = Arrangement.spacedBy(Dimens.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val ancho = Modifier.widthIn(max = Dimens.anchoMaximo).fillMaxWidth()
            estado.usuario?.let { Cabecera(it, ancho) }

            Seccion(Textos.Perfil.CUENTA, ancho) {
                Opcion(Icons.Outlined.Key, Textos.Perfil.CAMBIAR_PASSWORD, Textos.Perfil.CAMBIAR_PASSWORD_DESC) {
                    onEvent(PerfilEvent.CambiarPassword)
                }
            }

            Seccion(Textos.Apariencia.TITULO, ancho) { Apariencia() }

            Seccion(Textos.Perfil.AYUDA_SOPORTE, ancho) {
                Opcion(IconosModulo.ayuda, Textos.Perfil.AYUDA, Textos.Perfil.AYUDA_DESC) { onEvent(PerfilEvent.Ayuda) }
                Opcion(Icons.Outlined.AutoStories, Textos.Perfil.INTRO, Textos.Perfil.INTRO_DESC) { intro.abrir() }
            }

            Seccion(Textos.Permisos.TITULO, ancho) { Permisos() }

            Seccion(Textos.Perfil.ACERCA_DE, ancho) { AcercaDe() }

            GtButton(
                texto = Textos.Perfil.CERRAR_SESION,
                onClick = { onEvent(PerfilEvent.PedirCerrarSesion) },
                modifier = ancho,
                variante = GtButtonVariant.PeligroSecundario,
                icono = Icons.AutoMirrored.Outlined.Logout,
            )
        }
    }
    if (estado.confirmarSalida) {
        ConfirmSheet(
            titulo = Textos.Perfil.CONFIRMAR_SALIR_TITULO,
            mensaje = Textos.Perfil.CONFIRMAR_SALIR_MENSAJE,
            textoConfirmar = Textos.Perfil.CERRAR_SESION,
            onConfirmar = { onEvent(PerfilEvent.ConfirmarCerrarSesion) },
            onCancelar = { onEvent(PerfilEvent.CancelarCerrarSesion) },
            peligro = true,
            icono = Icons.AutoMirrored.Outlined.Logout,
            cargando = estado.saliendo,
        )
    }
}

@Composable
private fun Cabecera(usuario: Usuario, modifier: Modifier) {
    Column(modifier.padding(vertical = Dimens.sm), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(96.dp).clip(GtShapes.circulo).background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(usuario.iniciales, style = GtTexto.tituloPantalla, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
        Spacer(Modifier.size(Dimens.md))
        Text(
            usuario.nombre,
            style = GtTexto.tituloPantalla,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            usuario.correo,
            style = GtTexto.cuerpo,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.size(Dimens.sm))
        StatusChip(
            usuario.rol.etiqueta,
            GreenTechTheme.colors.primario,
            icono = if (usuario.rol == Rol.ADMINISTRADOR) Icons.Outlined.AdminPanelSettings else Icons.Outlined.Badge,
            modifier = Modifier.semantics { contentDescription = "${Textos.Perfil.ROL}: ${usuario.rol.etiqueta}" },
        )
    }
}

/** Versión y una línea sobre la app (sin acción). */
@Composable
private fun AcercaDe() {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = Dimens.lg, vertical = Dimens.md).semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EmblemaGreenTech(tamano = 40.dp, descripcion = null)
        Spacer(Modifier.width(Dimens.lg))
        Column(Modifier.weight(1f)) {
            Text(Textos.Perfil.VERSION, style = GtTexto.tituloTarjeta)
            Text(Textos.Perfil.ACERCA_DESC, style = GtTexto.ayuda, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** Estado de Cámara y Notificaciones; tocar una fila abre los ajustes de la app. Se revisa al volver. */
@Composable
private fun Permisos() {
    FilaPermiso(Icons.Outlined.PhotoCamera, Textos.Permisos.CAMARA, rememberEstadoPermiso(Permiso.CAMARA))
    FilaPermiso(Icons.Outlined.Notifications, Textos.Permisos.NOTIFICACIONES, rememberEstadoPermiso(Permiso.NOTIFICACIONES))
}

@Composable
private fun FilaPermiso(icono: ImageVector, titulo: String, permiso: EstadoPermiso) {
    val interaccion = androidx.compose.runtime.remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val c = GreenTechTheme.colors
    Surface(onClick = permiso::abrirAjustes, color = Color.Transparent, interactionSource = interaccion, modifier = Modifier.efectoPresion(interaccion)) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = Dimens.filaLista).padding(horizontal = Dimens.lg, vertical = Dimens.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icono, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(Dimens.lg))
            Column(Modifier.weight(1f)) {
                Text(titulo, style = GtTexto.tituloTarjeta)
                Text(Textos.Permisos.CAMBIAR_EN_AJUSTES, style = GtTexto.ayuda, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(Dimens.sm))
            // Texto + ícono: el estado no depende solo del color.
            if (permiso.concedido) {
                StatusChip(Textos.Permisos.PERMITIDO, c.exito, icono = Icons.Outlined.CheckCircle)
            } else {
                StatusChip(Textos.Permisos.NO_PERMITIDO, c.advertencia, icono = Icons.Outlined.Block)
            }
        }
    }
}

/** Grupo con título y tarjeta tonal (una idea por tarjeta). */
@Composable
private fun Seccion(titulo: String, modifier: Modifier, contenido: @Composable ColumnScope.() -> Unit) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(Dimens.sm)) {
        Text(
            titulo,
            style = GtTexto.tituloTarjeta,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = Dimens.xs).semantics { heading() },
        )
        Surface(shape = GtShapes.mosaico, color = MaterialTheme.colorScheme.surfaceContainerLow) {
            Column(Modifier.padding(vertical = Dimens.sm), content = contenido)
        }
    }
}

@Composable
private fun Opcion(icono: ImageVector, titulo: String, descripcion: String, onClick: () -> Unit) {
    val interaccion = androidx.compose.runtime.remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    Surface(onClick = onClick, color = Color.Transparent, interactionSource = interaccion, modifier = Modifier.efectoPresion(interaccion)) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = Dimens.filaLista).padding(horizontal = Dimens.lg, vertical = Dimens.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icono, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(Dimens.lg))
            Column(Modifier.weight(1f)) {
                Text(titulo, style = GtTexto.tituloTarjeta)
                Text(descripcion, style = GtTexto.ayuda, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** Tema (Sistema · Claro · Oscuro) y "Reducir animaciones": se aplican al instante y persisten. */
@Composable
private fun Apariencia() {
    val apariencia = LocalApariencia.current
    Column(Modifier.padding(horizontal = Dimens.lg, vertical = Dimens.sm), verticalArrangement = Arrangement.spacedBy(Dimens.md)) {
        Text(Textos.Apariencia.TEMA, style = GtTexto.tituloTarjeta)
        GtSegmentado(
            opciones = listOf(ThemeMode.CLARO, ThemeMode.OSCURO, ThemeMode.SISTEMA),
            seleccionado = apariencia.modo,
            onSeleccion = apariencia::cambiarModo,
            etiqueta = {
                when (it) {
                    ThemeMode.SISTEMA -> Textos.Apariencia.SISTEMA
                    ThemeMode.CLARO -> Textos.Apariencia.CLARO
                    ThemeMode.OSCURO -> Textos.Apariencia.OSCURO
                }
            },
            icono = {
                when (it) {
                    ThemeMode.SISTEMA -> Icons.Outlined.Contrast
                    ThemeMode.CLARO -> Icons.Outlined.LightMode
                    ThemeMode.OSCURO -> Icons.Outlined.DarkMode
                }
            },
            alto = 64.dp,
        )
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(min = Dimens.filaLista)
                .clip(GtShapes.campo)
                .toggleable(
                    value = apariencia.reducirAnimaciones,
                    onValueChange = apariencia::cambiarReducirAnimaciones,
                    role = Role.Switch,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.Animation, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(Dimens.lg))
            Column(Modifier.weight(1f)) {
                Text(Textos.Apariencia.REDUCIR, style = GtTexto.tituloTarjeta)
                Text(Textos.Apariencia.REDUCIR_DESC, style = GtTexto.ayuda, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = apariencia.reducirAnimaciones, onCheckedChange = null)
        }
    }
}

private val usuarioPreview = Usuario("u2", "Juan Pérez", "empleado@gmail.com", Rol.EMPLEADO, true, Fechas.hoy())

@Preview
@Composable
private fun PerfilPreviewClaro() {
    GreenTechTheme { PerfilScreen(PerfilUiState(usuarioPreview)) {} }
}

@Preview
@Composable
private fun PerfilPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { PerfilScreen(PerfilUiState(usuarioPreview.copy(rol = Rol.ADMINISTRADOR))) {} }
}
