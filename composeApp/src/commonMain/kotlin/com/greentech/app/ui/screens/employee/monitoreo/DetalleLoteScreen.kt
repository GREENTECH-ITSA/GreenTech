package com.greentech.app.ui.screens.employee.monitoreo

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.runtime.getValue
import com.greentech.app.ui.components.StatusChip
import com.greentech.app.ui.components.contexto
import com.greentech.app.ui.theme.Motion
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.greentech.app.ui.components.EstadoContenido
import com.greentech.app.ui.components.EtapaChip
import com.greentech.app.ui.components.GtCargandoCentro
import com.greentech.app.ui.components.GtScaffold
import com.greentech.app.ui.components.GtTopBar
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.NumeroAnimado
import com.greentech.app.ui.components.SaludChip
import com.greentech.app.ui.components.anchoCompleto
import com.greentech.app.ui.components.bento
import com.greentech.app.ui.components.charts.Gauge
import com.greentech.app.ui.components.formatearNumero
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.preview.DatosPreview
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.GreenTechTheme
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
                        IconButton(onClick = { onEvent(DetalleLoteEvent.VerQr) }, modifier = Modifier.size(56.dp)) {
                            Icon(IconosModulo.qr, contentDescription = Textos.Detalle.VER_QR, modifier = Modifier.size(28.dp))
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
                    start = 20.dp, end = 20.dp,
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = padding.calculateBottomPadding() + 24.dp,
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
                            MosaicoSensor(Textos.Detalle.HUMEDAD, IconosModulo.humedad, ultima.humedad, "%", 100.0, esp.humedadMin, esp.humedadMax, abrir)
                        }
                        bento(BentoSize.S, "temperatura") {
                            MosaicoSensor(Textos.Detalle.TEMPERATURA, IconosModulo.temperatura, ultima.temperatura, "°C", 45.0, esp.temperaturaMin, esp.temperaturaMax, abrir)
                        }
                    }
                }
                anchoCompleto("tituloAcciones") {
                    Text(
                        if (estado.soloLectura) Textos.Disenio.QUE_CONSULTAR else Textos.Detalle.ACCIONES,
                        style = MaterialTheme.typography.titleMedium,
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

/** L: especie, código, etapa y salud, plantas disponibles/iniciales con su barra, zona y siembra. */
@Composable
private fun MosaicoResumen(lote: Lote, resaltado: AccionLote?) {
    BentoTile(
        BentoSize.L,
        descripcion = "${lote.especie.nombre}, ${lote.codigo}, ${lote.etapa.etiqueta}, ${lote.estadoSalud.etiqueta}. " +
            "${Textos.Detalle.DISPONIBLES}: ${lote.cantidadDisponible} ${Textos.Disenio.DISPONIBLES_DE} ${lote.cantidadInicial}",
    ) {
        Text(lote.especie.nombre, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.semantics { heading() })
        Text(lote.especie.nombreCientifico, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(lote.codigo, style = MaterialTheme.typography.labelLarge.tabular(), color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(12.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            EtapaChip(lote.etapa)
            SaludChip(lote.estadoSalud)
            // La etapa o las disponibles acaban de cambiar: se dice con texto e ícono, no solo con color.
            AnimatedVisibility(resaltado == AccionLote.ETAPA || resaltado == AccionLote.SALIDA, enter = fadeIn(Motion.media()), exit = fadeOut(Motion.lenta())) {
                StatusChip(Textos.Comun.ACTUALIZADO_AHORA, GreenTechTheme.colors.exito, icono = Icons.Outlined.CheckCircle)
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(Textos.Detalle.DISPONIBLES, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(verticalAlignment = Alignment.Bottom) {
            NumeroAnimado(lote.cantidadDisponible.toDouble(), MaterialTheme.typography.displaySmall)
            Spacer(Modifier.width(8.dp))
            Text(
                "${Textos.Lotes.DE} ${formatearNumero(lote.cantidadInicial.toDouble())} ${Textos.Comun.PLANTAS}",
                style = MaterialTheme.typography.titleMedium.tabular(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp),
            )
        }
        Spacer(Modifier.height(8.dp))
        BarraDisponibilidad(lote.proporcionDisponible)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Dato(Icons.Outlined.Place, Textos.Detalle.ZONA, lote.zona.nombre, Modifier.weight(1f))
            Dato(Icons.Outlined.CalendarMonth, Textos.Detalle.SIEMBRA, Fechas.corta(lote.fechaSiembra), Modifier.weight(1f))
        }
    }
}

@Composable
private fun Dato(icono: ImageVector, etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valor, style = MaterialTheme.typography.bodyMedium)
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
    onClick: () -> Unit,
) {
    BentoTile(BentoSize.S, etiqueta = titulo, icono = icono, onClick = onClick) {
        Gauge(
            valor, unidad, 0.0, maximo, idealMin, idealMax,
            tamano = 96.dp, titulo = titulo,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
private fun AvisoSoloLectura() {
    val c = GreenTechTheme.colors.info
    com.greentech.app.ui.components.GtCard(colorFondo = c.fondo) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Info, null, tint = c.contenido)
            Spacer(Modifier.width(12.dp))
            Text(Textos.Detalle.SOLO_LECTURA_AVISO, style = MaterialTheme.typography.bodyMedium, color = c.contenido)
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
