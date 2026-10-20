package com.greentech.app.ui.components

import androidx.compose.ui.unit.offset
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.Motion
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay

/**
 * Espacio que ocupa la barra inferior flotante del panel (cápsula + margen). GtScaffold lo suma
 * al padding inferior para que el contenido pueda pasar por debajo del vidrio sin quedar tapado.
 */
val LocalEspacioInferior = staticCompositionLocalOf<State<Dp>> { mutableStateOf(0.dp) }

/**
 * Padding del contenido más el espacio de la barra inferior. El espacio se anima al entrar o salir de
 * las pestañas: se lee al medir (padding/contentPadding), así la animación solo vuelve a hacer layout.
 */
@Stable
private class PaddingConBarra(private val base: PaddingValues, private val extra: State<Dp>) : PaddingValues {
    override fun calculateLeftPadding(layoutDirection: LayoutDirection) = base.calculateLeftPadding(layoutDirection)
    override fun calculateTopPadding() = base.calculateTopPadding()
    override fun calculateRightPadding(layoutDirection: LayoutDirection) = base.calculateRightPadding(layoutDirection)
    override fun calculateBottomPadding() = base.calculateBottomPadding() + extra.value
}

/** Deja [extra] de espacio debajo (leído en layout). */
private fun Modifier.espacioInferior(extra: State<Dp>): Modifier = layout { medible, restricciones ->
    val abajo = extra.value.roundToPx()
    val colocable = medible.measure(restricciones.offset(vertical = -abajo))
    layout(colocable.width, colocable.height + abajo) { colocable.place(0, 0) }
}

/** true cuando el contenido ya se desplazó: la barra superior pasa de transparente a vidrio. */
internal val LocalBarraDesplazada = compositionLocalOf { false }

/**
 * Scaffold base: fondo del tema, barra superior que se vuelve vidrio al hacer scroll y
 * fuente de desenfoque para las barras. El padding que recibe [contenido] ya incluye el
 * espacio de la barra inferior flotante del panel.
 */
@Composable
fun GtScaffold(
    topBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    barraInferior: @Composable () -> Unit = {},
    botonFlotante: @Composable () -> Unit = {},
    contenido: @Composable (PaddingValues) -> Unit,
) {
    val comportamiento = TopAppBarDefaults.pinnedScrollBehavior()
    val desplazada by remember { derivedStateOf { comportamiento.state.contentOffset < -1f } }
    val haze = rememberHazeState()
    val extra = LocalEspacioInferior.current
    Scaffold(
        modifier = modifier.nestedScroll(comportamiento.nestedScrollConnection),
        topBar = {
            CompositionLocalProvider(LocalHazeState provides haze, LocalBarraDesplazada provides desplazada) { topBar() }
        },
        bottomBar = { CompositionLocalProvider(LocalHazeState provides haze) { barraInferior() } },
        floatingActionButton = { Box(Modifier.espacioInferior(extra)) { botonFlotante() } },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        val conBarra = remember(padding, extra) { PaddingConBarra(padding, extra) }
        Box(Modifier.fillMaxSize().fuenteVidrio(haze)) { contenido(conBarra) }
    }
}

/**
 * Pantalla de formulario con el patrón único de la app:
 * - Barra superior con X ([onCerrar]), título y, si aplica, subtítulo de contexto del lote.
 * - Nota "* Obligatorio" al inicio y campos en una sola columna.
 * - Acción principal fija abajo dentro de una GlassSurface; si faltan datos se listan debajo.
 * - Si [hayCambios], salir con la X o con "atrás" pregunta "¿Descartar cambios?".
 *
 * Al guardar se quita el foco del campo activo (así se valida al salir de él) y, tras un éxito,
 * el botón muestra un check por 600 ms antes de que la pantalla navegue.
 */
@Composable
fun FormularioScaffold(
    titulo: String,
    onCerrar: () -> Unit,
    onAyuda: (() -> Unit)?,
    textoGuardar: String,
    onGuardar: () -> Unit,
    habilitado: Boolean,
    cargando: Boolean,
    modifier: Modifier = Modifier,
    subtitulo: String? = null,
    hayCambios: Boolean = false,
    faltantes: List<String> = emptyList(),
    variante: GtButtonVariant = GtButtonVariant.Primario,
    icono: androidx.compose.ui.graphics.vector.ImageVector? = null,
    notaObligatorios: Boolean = true,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    val exito = rememberExitoReciente()
    val foco = LocalFocusManager.current
    var confirmarDescartar by rememberSaveable { mutableStateOf(false) }
    val protegerSalida = hayCambios && !cargando && !exito
    val salir = { if (protegerSalida) confirmarDescartar = true else onCerrar() }
    ManejarAtras(activo = protegerSalida) { confirmarDescartar = true }
    GtScaffold(
        topBar = { GtTopBar(titulo, onAyuda = onAyuda, onVolver = salir, subtitulo = subtitulo, cerrar = true) },
        modifier = modifier,
        barraInferior = {
            GlassSurface(Modifier.fillMaxWidth()) {
                Column(
                    Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.navigationBars).imePadding()
                        .padding(horizontal = Dimens.margenLateral, vertical = Dimens.md),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    GtButton(
                        textoGuardar,
                        {
                            foco.clearFocus()
                            onGuardar()
                        },
                        modifier = Modifier.fillMaxWidth().widthIn(max = Dimens.anchoMaximo),
                        habilitado = habilitado, cargando = cargando, variante = variante, icono = icono, exito = exito,
                    )
                    AnimatedVisibility(
                        visible = !habilitado && faltantes.isNotEmpty() && !cargando && !exito,
                        enter = expandVertically(Motion.media()) + fadeIn(Motion.media()),
                        exit = shrinkVertically(Motion.media()) + fadeOut(Motion.rapida()),
                    ) {
                        Text(
                            "${Textos.Comun.COMPLETA_OBLIGATORIOS} ${faltantes.joinToString(", ")}",
                            style = GtTexto.ayuda,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = Dimens.sm),
                        )
                    }
                }
            }
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = Dimens.margenLateral, vertical = Dimens.lg),
            verticalArrangement = Arrangement.spacedBy(Dimens.lg),
        ) {
            if (notaObligatorios) {
                Text(
                    Textos.Comun.NOTA_OBLIGATORIO,
                    style = GtTexto.ayuda,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            contenido()
        }
    }
    if (confirmarDescartar) {
        ConfirmSheet(
            titulo = Textos.Comun.DESCARTAR_TITULO,
            mensaje = Textos.Comun.DESCARTAR_MENSAJE,
            textoConfirmar = Textos.Comun.DESCARTAR,
            onConfirmar = {
                confirmarDescartar = false
                onCerrar()
            },
            onCancelar = { confirmarDescartar = false },
            peligro = true,
            icono = Icons.Outlined.EditOff,
            textoCancelar = Textos.Comun.SEGUIR_EDITANDO,
        )
    }
}

/**
 * FocusRequester que enfoca su campo al abrir el formulario. Se llama dentro del contenido del
 * formulario (así el campo ya está en pantalla cuando se pide el foco).
 */
@Composable
fun rememberEnfoqueInicial(): androidx.compose.ui.focus.FocusRequester {
    val requester = remember { androidx.compose.ui.focus.FocusRequester() }
    LaunchedEffect(Unit) {
        androidx.compose.runtime.withFrameNanos { }
        runCatching { requester.requestFocus() }
    }
    return requester
}

/** Agrega el asterisco de campo obligatorio a una etiqueta: "Especie *". */
fun obligatorio(etiqueta: String): String = "$etiqueta *"

/** true durante 600 ms después de que el Logic notifica un éxito (check del botón de guardar). */
@Composable
fun rememberExitoReciente(): Boolean {
    val retro = LocalRetroalimentacion.current
    val inicial = remember { retro.ultimoExito }
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(retro.ultimoExito) {
        if (retro.ultimoExito != inicial && retro.ultimoExito != 0L) {
            visible = true
            delay(Motion.ms(Motion.CHECK_GUARDADO.toInt()).toLong())
            visible = false
        }
    }
    return visible
}

/**
 * Sección agrupada de formulario: tarjeta en surfaceContainerLow con título.
 * Una idea por tarjeta; los campos van dentro con fondo surface.
 */
@Composable
fun SeccionFormulario(
    titulo: String?,
    modifier: Modifier = Modifier,
    info: InfoAyuda? = null,
    onVerAyuda: (() -> Unit)? = null,
    accionTitulo: (@Composable () -> Unit)? = null,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    androidx.compose.material3.Surface(
        modifier = modifier.fillMaxWidth(),
        shape = com.greentech.app.ui.theme.GtShapes.mosaico,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(Modifier.padding(Dimens.lg), verticalArrangement = Arrangement.spacedBy(Dimens.md)) {
            if (titulo != null) {
                androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        titulo,
                        style = GtTexto.tituloTarjeta,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f).semantics { heading() },
                    )
                    if (info != null) BotonInfo(info, onVerAyuda)
                    accionTitulo?.invoke()
                }
            }
            contenido()
        }
    }
}

/** Ancho máximo de lectura para tablets. */
val AnchoMaximo: Dp = 640.dp
