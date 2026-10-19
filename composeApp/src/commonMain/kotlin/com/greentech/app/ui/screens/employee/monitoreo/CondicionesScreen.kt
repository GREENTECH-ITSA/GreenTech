package com.greentech.app.ui.screens.employee.monitoreo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SensorsOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.BentoSize
import com.greentech.app.ui.components.BentoTile
import com.greentech.app.ui.components.EmptyState
import com.greentech.app.ui.components.EstadoContenido
import com.greentech.app.ui.components.GtCargandoCentro
import com.greentech.app.ui.components.GtScaffold
import com.greentech.app.ui.components.GtTopBar
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.LoteResumen
import com.greentech.app.ui.components.charts.Gauge
import com.greentech.app.ui.components.charts.LineChart
import com.greentech.app.ui.components.charts.esDesvioSevero
import com.greentech.app.ui.components.charts.estadoRango
import com.greentech.app.ui.components.charts.estilo
import com.greentech.app.ui.components.contexto
import com.greentech.app.ui.components.formatearNumero
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.preview.DatosPreview
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.shared.domain.model.CondicionesLote
import com.greentech.shared.util.Fechas

private const val HUMEDAD_MAX = 100.0
private const val TEMPERATURA_MAX = 45.0

@Composable
fun CondicionesScreen(estado: CondicionesUiState, onEvent: (CondicionesEvent) -> Unit) {
    GtScaffold(
        topBar = {
            GtTopBar(
                Textos.Condiciones.TITULO,
                subtitulo = estado.lote?.contexto(),
                onVolver = { onEvent(CondicionesEvent.Volver) },
                onAyuda = { onEvent(CondicionesEvent.Ayuda) },
            )
        },
    ) { padding ->
        EstadoContenido(
            estado = estado.condiciones,
            onReintentar = { onEvent(CondicionesEvent.Reintentar) },
            modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding()),
            esVacio = { it == null },
            vacio = { EmptyState(Icons.Outlined.SensorsOff, Textos.Condiciones.VACIO, Textos.Condiciones.VACIO_DESC) },
            cargando = { GtCargandoCentro() },
        ) { condiciones ->
            if (condiciones != null) Contenido(condiciones, Modifier.padding(bottom = padding.calculateBottomPadding()))
        }
    }
}

/** Dos gauges grandes y, debajo, la línea de las últimas 24 h de cada sensor. */
@Composable
private fun Contenido(c: CondicionesLote, modifier: Modifier) {
    val esp = c.lote.especie
    val ultima = c.ultima ?: return
    val lecturas = c.lecturas.sortedBy { it.fecha }
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).then(modifier).padding(horizontal = Dimens.margenLateral, vertical = Dimens.sm),
        verticalArrangement = Arrangement.spacedBy(Dimens.md),
    ) {
        LoteResumen(c.lote)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Schedule, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(Dimens.iconoChico))
            Spacer(Modifier.width(Dimens.sm))
            Text(
                "${Textos.Condiciones.ULTIMA_LECTURA}: ${Fechas.hora(ultima.fecha)} · ${c.lote.zona.nombre}",
                style = GtTexto.ayuda,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.md)) {
            GaugeGrande(Textos.Condiciones.HUMEDAD, ultima.humedad, "%", HUMEDAD_MAX, esp.humedadMin, esp.humedadMax, GreenTechTheme.colors.series.humedad, Modifier.weight(1f))
            GaugeGrande(Textos.Condiciones.TEMPERATURA, ultima.temperatura, "°C", TEMPERATURA_MAX, esp.temperaturaMin, esp.temperaturaMax, GreenTechTheme.colors.series.temperatura, Modifier.weight(1f))
        }
        BentoTile(BentoSize.L, etiqueta = "${Textos.Condiciones.HUMEDAD} · ${Textos.Condiciones.ULTIMAS_24}", icono = IconosModulo.humedad) {
            LineChart(
                valores = lecturas.map { it.humedad },
                color = GreenTechTheme.colors.series.humedad,
                unidad = "%",
                descripcion = Textos.Condiciones.GRAFICA_HUMEDAD,
                escalaMin = 0.0, escalaMax = HUMEDAD_MAX,
                idealMin = esp.humedadMin, idealMax = esp.humedadMax,
                etiquetaInicio = Textos.Condiciones.HACE_24, etiquetaFin = Textos.Condiciones.AHORA,
                etiquetaPunto = { Fechas.hora(lecturas[it].fecha) },
            )
        }
        BentoTile(BentoSize.L, etiqueta = "${Textos.Condiciones.TEMPERATURA} · ${Textos.Condiciones.ULTIMAS_24}", icono = IconosModulo.temperatura) {
            LineChart(
                valores = lecturas.map { it.temperatura },
                color = GreenTechTheme.colors.series.temperatura,
                unidad = "°C",
                descripcion = Textos.Condiciones.GRAFICA_TEMPERATURA,
                escalaMin = 0.0, escalaMax = TEMPERATURA_MAX,
                idealMin = esp.temperaturaMin, idealMax = esp.temperaturaMax,
                etiquetaInicio = Textos.Condiciones.HACE_24, etiquetaFin = Textos.Condiciones.AHORA,
                etiquetaPunto = { Fechas.hora(lecturas[it].fecha) },
            )
        }
    }
}

@Composable
private fun GaugeGrande(titulo: String, valor: Double, unidad: String, maximo: Double, idealMin: Double, idealMax: Double, color: Color, modifier: Modifier) {
    val estilo = estadoRango(valor, idealMin, idealMax).estilo(esDesvioSevero(valor, idealMin, idealMax))
    BentoTile(BentoSize.S, modifier = modifier, etiqueta = titulo) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Gauge(valor, unidad, 0.0, maximo, idealMin, idealMax, tamano = Dimens.gaugeMediano, titulo = titulo, colorSerie = color)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(estilo.icono, null, tint = estilo.color, modifier = Modifier.size(Dimens.iconoChico))
                Spacer(Modifier.width(Dimens.xs))
                Text(estilo.texto, style = GtTexto.etiqueta, color = estilo.color)
            }
            Text(
                "${Textos.Condiciones.IDEAL} ${formatearNumero(idealMin)}–${formatearNumero(idealMax)} $unidad",
                style = GtTexto.etiqueta,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private val estadoPreview = CondicionesUiState(EstadoCarga.Listo(CondicionesLote(DatosPreview.lote, DatosPreview.lecturas)))

@Preview
@Composable
private fun CondicionesPreviewClaro() {
    GreenTechTheme { CondicionesScreen(estadoPreview) {} }
}

@Preview
@Composable
private fun CondicionesPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { CondicionesScreen(estadoPreview) {} }
}
