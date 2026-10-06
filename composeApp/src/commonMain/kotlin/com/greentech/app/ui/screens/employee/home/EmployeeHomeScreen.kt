package com.greentech.app.ui.screens.employee.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import com.greentech.app.util.FechasUi
import com.greentech.app.ui.strings.conValores
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import com.greentech.app.ui.components.AlReseleccionarPestana
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.BentoGrid
import com.greentech.app.ui.components.BentoSize
import com.greentech.app.ui.components.BentoTile
import com.greentech.app.ui.components.EmblemaGreenTech
import com.greentech.app.ui.components.EstadoSincronizacion
import com.greentech.app.ui.components.GtButton
import com.greentech.app.ui.components.GtButtonVariant
import com.greentech.app.ui.components.GtPanelTopBar
import com.greentech.app.ui.components.GtScaffold
import com.greentech.app.ui.components.IconosDominio
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.NumeroAnimado
import com.greentech.app.ui.components.OfflineBanner
import com.greentech.app.ui.components.SkeletonBloque
import com.greentech.app.ui.components.anchoCompleto
import com.greentech.app.ui.components.bento
import com.greentech.app.ui.components.charts.Gauge
import com.greentech.app.ui.components.charts.esDesvioSevero
import com.greentech.app.ui.components.formatearNumero
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.preview.DatosPreview
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.shared.domain.model.AlertaSensor
import com.greentech.shared.domain.model.CondicionesLote
import com.greentech.shared.domain.model.EventoBitacora
import com.greentech.shared.domain.model.TipoAlerta
import com.greentech.shared.util.Fechas

/**
 * Inicio del empleado en bento: saludo con acciones principales (L), alerta de sensores (M),
 * lotes activos y anomalías abiertas (S + S), última actividad (M) y condiciones de la zona (M).
 */
@Composable
fun EmployeeHomeScreen(estado: EmployeeHomeUiState, onEvent: (EmployeeHomeEvent) -> Unit) {
    LaunchedEffect(Unit) { onEvent(EmployeeHomeEvent.Entrar) }
    val cuadricula = rememberLazyGridState()
    AlReseleccionarPestana { cuadricula.animateScrollToItem(0) }
    GtScaffold(topBar = { GtPanelTopBar(onAyuda = { onEvent(EmployeeHomeEvent.Ayuda) }) }) { padding ->
        val direccion = LocalLayoutDirection.current
        BentoGrid(
            modifier = Modifier.fillMaxSize(),
            estado = cuadricula,
            contentPadding = PaddingValues(
                start = padding.calculateStartPadding(direccion) + 20.dp,
                end = padding.calculateEndPadding(direccion) + 20.dp,
                top = padding.calculateTopPadding() + 8.dp,
                bottom = padding.calculateBottomPadding() + 16.dp,
            ),
        ) {
            bento(BentoSize.L, "saludo") { MosaicoSaludo(estado, onEvent) }
            bento(BentoSize.M, "alerta") { MosaicoAlerta(estado.alertas, onReintentar = { onEvent(EmployeeHomeEvent.Entrar) }) }
            bento(BentoSize.S, "lotesActivos") {
                MosaicoNumero(
                    etiqueta = Textos.Disenio.LOTES_ACTIVOS,
                    valor = estado.lotesActivos,
                    icono = IconosModulo.lote,
                    contexto = Textos.Disenio.EN_TU_ZONA,
                    onClick = { onEvent(EmployeeHomeEvent.VerLotes) },
                )
            }
            bento(BentoSize.S, "anomalias") {
                MosaicoNumero(
                    etiqueta = Textos.Disenio.ANOMALIAS_ABIERTAS,
                    valor = estado.anomaliasAbiertas,
                    icono = IconosModulo.anomalia,
                    contexto = Textos.Disenio.POR_ATENDER,
                    onClick = null,
                )
            }
            bento(BentoSize.M, "actividad") {
                MosaicoActividad(estado.recientes) { onEvent(EmployeeHomeEvent.AbrirLote(it)) }
            }
            bento(BentoSize.M, "condiciones") {
                MosaicoCondiciones(estado.condiciones) { onEvent(EmployeeHomeEvent.AbrirLote(it)) }
            }
            anchoCompleto("sync") { OfflineBanner(estado.sync, onSincronizar = { onEvent(EmployeeHomeEvent.Sincronizar) }) }
        }
    }
}

/** L: saludo + fecha + acciones principales sobre primaryContainer, con el emblema como marca de agua (6 %). */
@Composable
private fun MosaicoSaludo(estado: EmployeeHomeUiState, onEvent: (EmployeeHomeEvent) -> Unit) {
    val sobre = MaterialTheme.colorScheme.onPrimaryContainer
    BentoTile(
        size = BentoSize.L,
        colorFondo = MaterialTheme.colorScheme.primaryContainer,
        colorContenido = sobre,
        fondo = {
            EmblemaGreenTech(
                tamano = 220.dp,
                descripcion = null,
                modifier = Modifier.align(Alignment.BottomEnd).offset(x = 48.dp, y = 56.dp).alpha(0.06f),
            )
        },
    ) {
        Text(
            "${Textos.Inicio.HOLA}, ${estado.nombre}",
            style = MaterialTheme.typography.headlineMedium,
            color = sobre,
            modifier = Modifier.semantics { heading() },
        )
        Text(estado.fecha.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodyMedium, color = sobre)
        Spacer(Modifier.height(20.dp))
        // Con letra grande los botones se apilan para no partir palabras.
        if (LocalDensity.current.fontScale > 1.3f) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                GtButton(Textos.Inicio.NUEVO_LOTE, { onEvent(EmployeeHomeEvent.NuevoLote) }, icono = Icons.Outlined.Add)
                GtButton(
                    Textos.Inicio.ESCANEAR, { onEvent(EmployeeHomeEvent.Escanear) },
                    variante = GtButtonVariant.Tonal, icono = IconosModulo.escanear,
                )
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GtButton(
                    Textos.Inicio.NUEVO_LOTE,
                    { onEvent(EmployeeHomeEvent.NuevoLote) },
                    modifier = Modifier.weight(1f),
                    icono = Icons.Outlined.Add,
                )
                GtButton(
                    Textos.Inicio.ESCANEAR,
                    { onEvent(EmployeeHomeEvent.Escanear) },
                    modifier = Modifier.weight(1f),
                    variante = GtButtonVariant.Tonal,
                    icono = IconosModulo.escanear,
                )
            }
        }
    }
}

/** M: la alerta de sensores más relevante en warning/errorContainer; si no hay, confirmación en verde. */
@Composable
private fun MosaicoAlerta(alertas: EstadoCarga<List<AlertaSensor>>, onReintentar: () -> Unit) {
    when (alertas) {
        EstadoCarga.Cargando -> SkeletonBloque(Modifier.fillMaxWidth().height(BentoSize.M.altoMinimo))
        is EstadoCarga.Error -> BentoTile(BentoSize.M, etiqueta = Textos.Inicio.ALERTAS, onClick = onReintentar, contexto = Textos.Comun.REINTENTAR) {
            Text(alertas.mensaje, style = MaterialTheme.typography.bodyMedium)
        }
        is EstadoCarga.Listo -> {
            val alerta = alertas.datos.firstOrNull()
            if (alerta == null) {
                val x = GreenTechTheme.colors
                BentoTile(
                    BentoSize.M,
                    etiqueta = Textos.Inicio.ALERTAS,
                    icono = Icons.Outlined.CheckCircle,
                    colorFondo = x.successContainer,
                    colorContenido = x.onSuccessContainer,
                    colorEtiqueta = x.onSuccessContainer,
                    colorIcono = x.onSuccessContainer,
                ) {
                    Text(Textos.Inicio.SIN_ALERTAS, style = MaterialTheme.typography.titleMedium, color = x.onSuccessContainer)
                }
            } else {
                TarjetaAlerta(alerta, restantes = alertas.datos.size - 1)
            }
        }
    }
}

@Composable
private fun TarjetaAlerta(alerta: AlertaSensor, restantes: Int) {
    val esHumedad = alerta.tipo == TipoAlerta.HUMEDAD_BAJA || alerta.tipo == TipoAlerta.HUMEDAD_ALTA
    val unidad = if (esHumedad) "%" else "°C"
    val severa = esDesvioSevero(alerta.valor, alerta.rangoMin, alerta.rangoMax)
    val fondo = if (severa) MaterialTheme.colorScheme.errorContainer else GreenTechTheme.colors.warningContainer
    val texto = if (severa) MaterialTheme.colorScheme.onErrorContainer else GreenTechTheme.colors.onWarningContainer
    val esBaja = alerta.tipo == TipoAlerta.HUMEDAD_BAJA || alerta.tipo == TipoAlerta.TEMPERATURA_BAJA
    val limite = if (esBaja) {
        "${Textos.Inicio.MINIMO_IDEAL} ${formatearNumero(alerta.rangoMin)} $unidad"
    } else {
        "${Textos.Inicio.MAXIMO_IDEAL} ${formatearNumero(alerta.rangoMax)} $unidad"
    }
    BentoTile(
        BentoSize.M,
        etiqueta = "${Textos.Inicio.ALERTAS} · ${alerta.zona.nombre}",
        icono = if (esHumedad) IconosModulo.humedad else IconosModulo.temperatura,
        colorFondo = fondo,
        colorContenido = texto,
        colorEtiqueta = texto,
        colorIcono = texto,
        contexto = buildString {
            append("${Textos.Inicio.AFECTA}: ${alerta.especiesAfectadas.joinToString(", ")}")
            if (restantes > 0) append(" · ${Textos.Inicio.ALERTAS_MAS.conValores(restantes)}")
        },
    ) {
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            NumeroAnimado(alerta.valor, MaterialTheme.typography.headlineMedium, decimales = 1, sufijo = " $unidad", color = texto)
            Text(alerta.tipo.etiqueta, style = MaterialTheme.typography.titleMedium, color = texto, modifier = Modifier.weight(1f))
        }
        Text(limite, style = MaterialTheme.typography.bodySmall, color = texto)
    }
}

/** S: un número protagonista con su ícono. */
@Composable
private fun MosaicoNumero(etiqueta: String, valor: Int?, icono: androidx.compose.ui.graphics.vector.ImageVector, contexto: String?, onClick: (() -> Unit)?) {
    if (valor == null) {
        SkeletonBloque(Modifier.fillMaxWidth().height(BentoSize.S.altoMinimo))
        return
    }
    BentoTile(
        BentoSize.S,
        etiqueta = etiqueta,
        icono = icono,
        contexto = contexto,
        onClick = onClick,
        descripcion = "$etiqueta: $valor",
    ) {
        NumeroAnimado(valor.toDouble(), MaterialTheme.typography.displaySmall)
    }
}

/** M: la última actividad registrada; toca para abrir su lote. */
@Composable
private fun MosaicoActividad(recientes: EstadoCarga<List<EventoBitacora>>, onAbrir: (String) -> Unit) {
    when (recientes) {
        EstadoCarga.Cargando -> SkeletonBloque(Modifier.fillMaxWidth().height(BentoSize.M.altoMinimo))
        is EstadoCarga.Error -> BentoTile(BentoSize.M, etiqueta = Textos.Disenio.ULTIMA_ACTIVIDAD) {
            Text(recientes.mensaje, style = MaterialTheme.typography.bodyMedium)
        }
        is EstadoCarga.Listo -> {
            val evento = recientes.datos.firstOrNull()
            if (evento == null) {
                BentoTile(BentoSize.M, etiqueta = Textos.Disenio.ULTIMA_ACTIVIDAD, icono = IconosModulo.bitacora) {
                    Text(Textos.Inicio.SIN_ACTIVIDAD, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                BentoTile(
                    BentoSize.M,
                    etiqueta = Textos.Disenio.ULTIMA_ACTIVIDAD,
                    icono = IconosDominio.evento(evento.tipo),
                    contexto = "${Textos.Bitacora.POR} ${evento.autor} · ${FechasUi.relativa(evento.fecha)}",
                    onClick = { onAbrir(evento.loteId) },
                ) {
                    Text(evento.titulo, style = MaterialTheme.typography.titleLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(
                        "${evento.especie} · ${evento.loteCodigo}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

/** M: humedad y temperatura de la zona con mini gauges (rango ideal de la especie). */
@Composable
private fun MosaicoCondiciones(condiciones: CondicionesLote?, onAbrir: (String) -> Unit) {
    val lectura = condiciones?.ultima
    if (condiciones == null || lectura == null) {
        BentoTile(BentoSize.M, etiqueta = Textos.Disenio.CONDICIONES_ZONA, icono = IconosModulo.condiciones) {
            Text(Textos.Disenio.SIN_DATOS, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }
    val especie = condiciones.lote.especie
    BentoTile(
        BentoSize.M,
        etiqueta = "${Textos.Disenio.CONDICIONES_ZONA} · ${condiciones.lote.zona.nombre}",
        onClick = { onAbrir(condiciones.lote.id) },
        contexto = "${Textos.Detalle.LECTURA} ${Fechas.hora(lectura.fecha)} · ${especie.nombre}",
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            MiniGauge(Textos.Detalle.HUMEDAD, lectura.humedad, "%", 0.0, 100.0, especie.humedadMin, especie.humedadMax)
            MiniGauge(Textos.Detalle.TEMPERATURA, lectura.temperatura, "°C", 0.0, 45.0, especie.temperaturaMin, especie.temperaturaMax)
        }
    }
}

@Composable
private fun MiniGauge(titulo: String, valor: Double, unidad: String, min: Double, max: Double, idealMin: Double, idealMax: Double) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Gauge(valor, unidad, min, max, idealMin, idealMax, tamano = 96.dp, titulo = titulo)
        Text(titulo, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private val estadoPreview = EmployeeHomeUiState(
    nombre = "Juan",
    fecha = Fechas.larga(Fechas.hoy()),
    alertas = EstadoCarga.Listo(emptyList()),
    recientes = EstadoCarga.Listo(DatosPreview.eventos.take(4)),
    sync = EstadoSincronizacion(pendientes = 2, ultimaHora = "10:30"),
    lotes = EstadoCarga.Listo(DatosPreview.lotes),
    anomaliasAbiertas = 3,
    condiciones = CondicionesLote(DatosPreview.lote, DatosPreview.lecturas),
)

@Preview
@Composable
private fun EmployeeHomePreviewClaro() {
    GreenTechTheme { EmployeeHomeScreen(estadoPreview) {} }
}

@Preview
@Composable
private fun EmployeeHomePreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { EmployeeHomeScreen(estadoPreview) {} }
}
