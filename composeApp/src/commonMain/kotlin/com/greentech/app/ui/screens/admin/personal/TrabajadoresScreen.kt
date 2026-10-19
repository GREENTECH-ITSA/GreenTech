package com.greentech.app.ui.screens.admin.personal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.AccionFab
import com.greentech.app.ui.components.AlReseleccionarPestana
import com.greentech.app.ui.components.ConfirmDialog
import com.greentech.app.ui.components.ConfirmSheet
import com.greentech.app.ui.components.EmptyState
import com.greentech.app.ui.components.EstadoContenido
import com.greentech.app.ui.components.GtBuscador
import com.greentech.app.ui.components.GtButton
import com.greentech.app.ui.components.GtButtonVariant
import com.greentech.app.ui.components.GtCard
import com.greentech.app.ui.components.GtFabMenu
import com.greentech.app.ui.components.GtScaffold
import com.greentech.app.ui.components.GtSegmentado
import com.greentech.app.ui.components.GtTopBar
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.ResumenConfirmacion
import com.greentech.app.ui.components.ResumenFila
import com.greentech.app.ui.components.StatusChip
import com.greentech.app.ui.components.entradaEscalonada
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.preview.DatosPreview
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.strings.conValores
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.app.util.FechasUi
import com.greentech.shared.domain.model.Usuario
import com.greentech.shared.util.Fechas

/** Personal: buscador, segmentado Activos / Inactivos, tarjetas con avatar de iniciales y FAB menu expresivo. */
@Composable
fun TrabajadoresScreen(estado: TrabajadoresUiState, onEvent: (TrabajadoresEvent) -> Unit) {
    LaunchedEffect(Unit) { onEvent(TrabajadoresEvent.Entrar) }
    val listaEstado = rememberLazyListState()
    AlReseleccionarPestana { listaEstado.animateScrollToItem(0) }
    GtScaffold(
        topBar = { GtTopBar(Textos.Personal.TITULO, onAyuda = { onEvent(TrabajadoresEvent.Ayuda) }, principal = true) },
        botonFlotante = {
            GtFabMenu(listOf(AccionFab(Textos.Personal.AGREGAR, Icons.Outlined.PersonAddAlt) { onEvent(TrabajadoresEvent.Agregar) }))
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(top = padding.calculateTopPadding()), verticalArrangement = Arrangement.spacedBy(Dimens.md)) {
            GtBuscador(
                valor = estado.busqueda,
                onValorChange = { onEvent(TrabajadoresEvent.Buscar(it)) },
                placeholder = Textos.Personal.BUSCAR,
                modifier = Modifier.padding(horizontal = Dimens.margenLateral),
            )
            GtSegmentado(
                opciones = PestanaPersonal.entries,
                seleccionado = estado.pestana,
                onSeleccion = { onEvent(TrabajadoresEvent.CambiarPestana(it)) },
                etiqueta = {
                    when (it) {
                        PestanaPersonal.ACTIVOS -> "${Textos.Personal.ACTIVOS} (${estado.totalActivos})"
                        PestanaPersonal.INACTIVOS -> "${Textos.Personal.INACTIVOS} (${estado.totalInactivos})"
                    }
                },
                modifier = Modifier.padding(horizontal = Dimens.margenLateral),
            )
            EstadoContenido(estado.trabajadores, onReintentar = { onEvent(TrabajadoresEvent.Entrar) }) {
                val lista = estado.visibles
                when {
                    lista.isEmpty() && estado.busqueda.isNotBlank() -> EmptyState(
                        icono = Icons.Outlined.SearchOff,
                        titulo = Textos.Personal.SIN_COINCIDENCIAS.conValores(estado.busqueda.trim()),
                        mensaje = Textos.Personal.SIN_COINCIDENCIAS_DESC,
                        textoAccion = Textos.Comun.LIMPIAR_BUSQUEDA,
                        iconoAccion = Icons.Outlined.Close,
                        onAccion = { onEvent(TrabajadoresEvent.Buscar("")) },
                    )
                    lista.isEmpty() && estado.pestana == PestanaPersonal.INACTIVOS -> EmptyState(
                        icono = IconosModulo.personal,
                        titulo = Textos.Personal.VACIO_INACTIVOS,
                        mensaje = Textos.Personal.VACIO_INACTIVOS_DESC,
                    )
                    lista.isEmpty() -> EmptyState(
                        icono = IconosModulo.personal,
                        titulo = Textos.Personal.VACIO_ACTIVOS,
                        mensaje = Textos.Personal.VACIO_DESC,
                        textoAccion = Textos.Personal.AGREGAR,
                        iconoAccion = Icons.Outlined.PersonAddAlt,
                        onAccion = { onEvent(TrabajadoresEvent.Agregar) },
                    )
                    else -> LazyColumn(
                        Modifier.fillMaxSize(),
                        state = listaEstado,
                        contentPadding = PaddingValues(start = Dimens.margenLateral, end = Dimens.margenLateral, top = Dimens.xs, bottom = padding.calculateBottomPadding() + Dimens.espacioFab),
                        verticalArrangement = Arrangement.spacedBy(Dimens.md),
                    ) {
                        itemsIndexed(lista, key = { _, u -> u.id }) { i, u ->
                            TarjetaTrabajador(
                                usuario = u,
                                esSesion = u.id == estado.idSesion,
                                onBaja = { onEvent(TrabajadoresEvent.PedirBaja(u)) },
                                modifier = Modifier.animateItem().entradaEscalonada(i),
                            )
                        }
                    }
                }
            }
        }
    }
    estado.porDarDeBaja?.let { u ->
        ConfirmSheet(
            titulo = Textos.Personal.CONFIRMAR_TITULO.conValores(u.nombre),
            mensaje = Textos.Personal.CONFIRMAR_MENSAJE.conValores(u.primerNombre),
            textoConfirmar = Textos.Personal.DAR_BAJA,
            onConfirmar = { onEvent(TrabajadoresEvent.ConfirmarBaja) },
            onCancelar = { onEvent(TrabajadoresEvent.CancelarBaja) },
            peligro = true,
            icono = Icons.Outlined.PersonOff,
            cargando = estado.procesando,
        ) {
            ResumenConfirmacion {
                ResumenFila(Textos.AltaTrabajador.CORREO, u.correo)
                ResumenFila(Textos.AltaTrabajador.ROL, u.rol.etiqueta)
            }
        }
    }
}

@Composable
private fun TarjetaTrabajador(usuario: Usuario, esSesion: Boolean, onBaja: () -> Unit, modifier: Modifier = Modifier) {
    val esquema = MaterialTheme.colorScheme
    GtCard(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(52.dp).clip(GtShapes.circulo)
                    .background(if (usuario.activo) esquema.secondaryContainer else esquema.surfaceContainerHigh),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    usuario.iniciales,
                    style = GtTexto.tituloTarjeta,
                    color = if (usuario.activo) esquema.onSecondaryContainer else esquema.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(Dimens.md))
            Column(Modifier.weight(1f)) {
                Text(usuario.nombre, style = GtTexto.tituloTarjeta, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(usuario.correo, style = GtTexto.ayuda, color = esquema.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Spacer(Modifier.height(Dimens.md))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(Dimens.sm), verticalArrangement = Arrangement.spacedBy(Dimens.sm)) {
            StatusChip(usuario.rol.etiqueta, GreenTechTheme.colors.primario, icono = Icons.Outlined.Badge)
            if (!usuario.activo) StatusChip(Textos.Personal.INACTIVO, GreenTechTheme.colors.neutro, icono = Icons.Outlined.PersonOff)
            if (esSesion) StatusChip(Textos.Personal.TU, GreenTechTheme.colors.info)
        }
        Spacer(Modifier.height(Dimens.sm))
        Text(
            "${Textos.Personal.ALTA}: ${FechasUi.dia(usuario.fechaAlta)}",
            style = GtTexto.ayuda,
            color = esquema.onSurfaceVariant,
        )
        if (!esSesion) {
            Spacer(Modifier.height(Dimens.md))
            GtButton(Textos.Personal.DAR_BAJA, onBaja, variante = GtButtonVariant.PeligroSecundario, icono = Icons.Outlined.PersonOff)
        }
    }
}

private val estadoPreview = TrabajadoresUiState(trabajadores = EstadoCarga.Listo(DatosPreview.usuarios), idSesion = "u1")

@Preview
@Composable
private fun TrabajadoresPreviewClaro() {
    GreenTechTheme { TrabajadoresScreen(estadoPreview) {} }
}

@Preview
@Composable
private fun TrabajadoresPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { TrabajadoresScreen(estadoPreview.copy(pestana = PestanaPersonal.INACTIVOS)) {} }
}
