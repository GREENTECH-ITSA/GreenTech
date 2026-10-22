package com.greentech.app.ui.screens.employee.monitoreo

import com.greentech.app.platform.MarcarDibujado
import com.greentech.app.platform.Medicion
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.AccionGrande
import com.greentech.app.ui.components.BarraDisponibilidad
import com.greentech.app.ui.components.BentoGrid
import com.greentech.app.ui.components.BentoSize
import com.greentech.app.ui.components.BentoTile
import com.greentech.app.ui.components.BotonLeyendaSalud
import com.greentech.app.ui.components.EstadoContenido
import com.greentech.app.ui.components.EtapaChip
import com.greentech.app.ui.components.GtCard
import com.greentech.app.ui.components.GtCargandoCentro
import com.greentech.app.ui.components.GtScaffold
import com.greentech.app.ui.components.GtTopBar
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.NumeroAnimado
import com.greentech.app.ui.components.PlantHealthIndicator
import com.greentech.app.ui.components.StatusChip
import com.greentech.app.ui.components.anchoCompleto
import com.greentech.app.ui.components.bento
import com.greentech.app.ui.components.charts.Gauge
import com.greentech.app.ui.components.contexto
import com.greentech.app.ui.components.formatearNumero
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.preview.DatosPreview
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.Motion
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.app.ui.theme.tabular
import com.greentech.shared.domain.model.CondicionesLote
import com.greentech.shared.domain.model.Lote
import com.greentech.shared.util.Fechas

private data class OpcionAccion(val accion: AccionLote, val icono: ImageVector, val titulo: String)

/** Acciones del lote en el orden de la guía: Etapa, Observación, Actividad, Condiciones, Anomalía, Recomendaciones, Bitácora y Salida. */
private val acciones = listOf(
    OpcionAccion(AccionLote.ETAPA, IconosModulo.etapa, Textos.Detalle.ETAPA),
    OpcionAccion(AccionLote.OBSERVACION, IconosModulo.observacion, Textos.Detalle.OBSERVACION),
    OpcionAccion(AccionLote.ACTIVIDAD, IconosModulo.actividad, Textos.Detalle.ACTIVIDAD),
    OpcionAccion(AccionLote.CONDICIONES, IconosModulo.condiciones, Textos.Detalle.CONDICIONES),
    OpcionAccion(AccionLote.ANOMALIA, IconosModulo.anomalia, Textos.Detalle.ANOMALIA),
    OpcionAccion(AccionLote.RECOMENDACIONES, IconosModulo.recomendaciones, Textos.Detalle.RECOMENDACIONES),
    OpcionAccion(AccionLote.BITACORA, IconosModulo.bitacora, Textos.Detalle.BITACORA),
    OpcionAccion(AccionLote.SALIDA, IconosModulo.salida, Textos.Detalle.SALIDA),
)

/** En solo lectura (administrador) únicamente se muestran las consultas, igual que antes. */
private val accionesConsulta = setOf(AccionLote.BITACORA, AccionLote.CONDICIONES)

@Composable
fun DetalleLoteScreen(estado: DetalleLoteUiState, onEvent: (DetalleLoteEvent) -> Unit) {
    LaunchedEffect(Unit) { onEvent(DetalleLoteEvent.Entrar) }
    // T2 de la consulta por QR: primer frame del detalle con los datos del lote.
    MarcarDibujado(estado.lote is EstadoCarga.Listo, Medicion.QR_CONSULTA, Medicion.QR_ABRIR_A_DETALLE)
    val cuadricula = rememberLazyGridState()
    // Lo recién registrado debe verse: el detalle se desplaza hasta el resumen o hasta su acción.
    LaunchedEffect(estado.resaltado) {
        val accion = estado.resaltado ?: return@LaunchedEffect
        val previos = (if (estado.soloLectura) 1 else 0) + 1 +
            (if (estado.condiciones?.ultima != null) 2 else 0) + 1
        val indice = if (accion == AccionLote.ETAPA || accion == AccionLote.SALIDA) {
            0
        } else {
            previos + acciones.indexOfFirst { it.accion == accion }.coerceAtLeast(0)
        }
        cuadricula.animateScrollToItem(indice)
    }
    GtScaffold(
        topBar = {
            GtTopBar(
                titulo = Textos.Detalle.TITULO,
                subtitulo = listOfNotNull(estado.lote.contexto(), Textos.Comun.SOLO_LECTURA.takeIf { estado.soloLectura })
                    .joinToString(" · ").ifEmpty { null },
                onVolver = { onEvent(DetalleLoteEvent.Volver) },
                onAyuda = { onEvent(DetalleLoteEvent.Ayuda) },
                acciones = {
                    if (!estado.soloLectura) {
                        IconButton(onClick = { onEvent(DetalleLoteEvent.VerQr) }, modifier = Modifier.size(Dimens.tactilMin)) {
                            Icon(IconosModulo.qr, contentDescription = Textos.Detalle.VER_QR, modifier = Modifier.size(Dimens.iconoMediano))
                        }
                    }
                },
            )
        },
    ) { padding ->
        EstadoContenido(
            estado.lote,
            onReintentar = { onEvent(DetalleLoteEvent.Entrar) },
            modifier = Modifier.fillMaxSize(),
            cargando = { GtCargandoCentro(Modifier.fillMaxSize()) },
        ) { lote ->
            BentoGrid(
                modifier = Modifier.fillMaxSize(),
                estado = cuadricula,
                contentPadding = PaddingValues(
                    start = Dimens.margenLateral, end = Dimens.margenLateral,
                    top = padding.calculateTopPadding() + Dimens.sm,
                    bottom = padding.calculateBottomPadding() + Dimens.xl,
                ),
            ) {
                if (estado.soloLectura) anchoCompleto("aviso") { AvisoSoloLectura() }
                bento(BentoSize.L, "resumen") { MosaicoResumen(lote, estado.resaltado) }
                estado.condiciones?.let { c ->
                    val ultima = c.ultima
                    if (ultima != null) {
                        val esp = c.lote.especie
                        val abrir = { onEvent(DetalleLoteEvent.Accion(AccionLote.CONDICIONES)) }
                        bento(BentoSize.S, "humedad") {
                            MosaicoSensor(Textos.Detalle.HUMEDAD, IconosModulo.humedad, ultima.humedad, "%", 100.0, esp.humedadMin, esp.humedadMax, GreenTechTheme.colors.series.humedad, abrir)
                        }
                        bento(BentoSize.S, "temperatura") {
                            MosaicoSensor(Textos.Detalle.TEMPERATURA, IconosModulo.temperatura, ultima.temperatura, "°C", 45.0, esp.temperaturaMin, esp.temperaturaMax, GreenTechTheme.colors.series.temperatura, abrir)
                        }
                    }
                }
                anchoCompleto("tituloAcciones") {
                    Text(
                        if (estado.soloLectura) Textos.Disenio.QUE_CONSULTAR else Textos.Detalle.ACCIONES,
                        style = GtTexto.tituloTarjeta,
                        modifier = Modifier.semantics { heading() },
                    )
                }
                acciones.filter { !estado.soloLectura || it.accion in accionesConsulta }.forEach { op ->
                    bento(BentoSize.S, op.accion.name) {
                        AccionLoteTile(
                            op = op,
                            descripcion = when (op.accion) {
                                // "Etapa" siempre con el nombre de la etapa actual.
                                AccionLote.ETAPA -> lote.etapa.etiqueta
                                AccionLote.SALIDA -> Textos.Detalle.SALIDA_DESC
                                else -> null
                            },
                            resaltada = estado.resaltado == op.accion,
                            onClick = { onEvent(DetalleLoteEvent.Accion(op.accion)) },
                        )
                    }
                }
            }
        }
    }
}

/**
 * Acción del lote. Recién registrada se resalta un momento (contenedor tonal + "Registrado ahora"
 * con ícono, nunca solo con color).
 */
@Composable
private fun AccionLoteTile(op: OpcionAccion, descripcion: String?, resaltada: Boolean, onClick: () -> Unit) {
    val esquema = MaterialTheme.colorScheme
    val fondo by animateColorAsState(
        if (resaltada) esquema.primaryContainer else esquema.surfaceContainerLow, Motion.lenta(), label = "resaltado",
    )
    AccionGrande(
        icono = if (resaltada) Icons.Outlined.CheckCircle else op.icono,
        titulo = op.titulo,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        descripcion = if (resaltada) Textos.Comun.REGISTRADO_AHORA else descripcion,
        colorFondo = fondo,
        colorContenido = if (resaltada) esquema.onPrimaryContainer else esquema.onSurface,
        colorIcono = if (resaltada) esquema.onPrimaryContainer else esquema.primary,
    )
}

/** L (corte de hoja): especie y salud primero, código, etapa, plantas disponibles/iniciales con su barra, zona y siembra. */
@Composable
private fun MosaicoResumen(lote: Lote, resaltado: AccionLote?) {
    BentoTile(
        BentoSize.L,
        forma = GtShapes.hojaCorte,
        descripcion = "${lote.especie.nombre}, ${lote.codigo}, ${lote.etapa.etiqueta}, ${lote.estadoSalud.etiqueta}. " +
            "${Textos.Detalle.DISPONIBLES}: ${lote.cantidadDisponible} ${Textos.Disenio.DISPONIBLES_DE} ${lote.cantidadInicial}",
    ) {
        Text(lote.especie.nombre, style = GtTexto.tituloPantalla, modifier = Modifier.semantics { heading() })
        Text(lote.especie.nombreCientifico, style = GtTexto.ayuda, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(lote.codigo, style = GtTexto.ayuda.tabular(), color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(Dimens.md))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Dimens.sm),
            verticalArrangement = Arrangement.spacedBy(Dimens.sm),
            itemVerticalAlignment = Alignment.CenterVertically,
        ) {
            PlantHealthIndicator(lote.estadoSalud)
            EtapaChip(lote.etapa)
            // La etapa o las disponibles acaban de cambiar: se dice con texto e ícono, no solo con color.
            AnimatedVisibility(resaltado == AccionLote.ETAPA || resaltado == AccionLote.SALIDA, enter = fadeIn(Motion.media()), exit = fadeOut(Motion.lenta())) {
                StatusChip(Textos.Comun.ACTUALIZADO_AHORA, GreenTechTheme.colors.exito, icono = Icons.Outlined.CheckCircle)
            }
            BotonLeyendaSalud()
        }
        Spacer(Modifier.height(Dimens.lg))
        Text(Textos.Detalle.DISPONIBLES, style = GtTexto.etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(verticalAlignment = Alignment.Bottom) {
            NumeroAnimado(lote.cantidadDisponible.toDouble(), GtTexto.dato)
            Spacer(Modifier.width(Dimens.sm))
            Text(
                "${Textos.Lotes.DE} ${formatearNumero(lote.cantidadInicial.toDouble())} ${Textos.Comun.PLANTAS}",
                style = GtTexto.tituloTarjeta.tabular(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = Dimens.sm),
            )
        }
        Spacer(Modifier.height(Dimens.sm))
        BarraDisponibilidad(lote.proporcionDisponible)
        Spacer(Modifier.height(Dimens.md))
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.lg)) {
            Dato(Icons.Outlined.Place, Textos.Detalle.ZONA, lote.zona.nombre, Modifier.weight(1f))
            Dato(Icons.Outlined.CalendarMonth, Textos.Detalle.SIEMBRA, Fechas.corta(lote.fechaSiembra), Modifier.weight(1f))
        }
    }
}

@Composable
private fun Dato(icono: ImageVector, etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(Dimens.iconoChico))
        Spacer(Modifier.width(Dimens.sm))
        Column {
            Text(etiqueta, style = GtTexto.etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valor, style = GtTexto.ayuda)
        }
    }
}

/** S: un sensor con mini gauge (rango ideal de la especie). Toca para ver las condiciones. */
@Composable
private fun MosaicoSensor(
    titulo: String,
    icono: ImageVector,
    valor: Double,
    unidad: String,
    maximo: Double,
    idealMin: Double,
    idealMax: Double,
    color: Color,
    onClick: () -> Unit,
) {
    BentoTile(BentoSize.S, etiqueta = titulo, icono = icono, onClick = onClick) {
        Gauge(
            valor, unidad, 0.0, maximo, idealMin, idealMax,
            tamano = Dimens.gaugeMini, titulo = titulo, colorSerie = color,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
private fun AvisoSoloLectura() {
    val c = GreenTechTheme.colors.info
    GtCard(colorFondo = c.fondo) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Info, null, tint = c.contenido)
            Spacer(Modifier.width(Dimens.md))
            Text(Textos.Detalle.SOLO_LECTURA_AVISO, style = GtTexto.ayuda, color = c.contenido)
        }
    }
}

private val estadoPreview = DetalleLoteUiState(
    lote = EstadoCarga.Listo(DatosPreview.lote),
    condiciones = CondicionesLote(DatosPreview.lote, DatosPreview.lecturas),
)

@Preview
@Composable
private fun DetalleLotePreviewClaro() {
    GreenTechTheme { DetalleLoteScreen(estadoPreview) {} }
}

@Preview
@Composable
private fun DetalleLotePreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { DetalleLoteScreen(estadoPreview.copy(soloLectura = true)) {} }
}
