package com.greentech.app.ui.screens.admin.reportes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Percent
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.BentoGrid
import com.greentech.app.ui.components.BentoSize
import com.greentech.app.ui.components.BentoTile
import com.greentech.app.ui.components.BotonInfo
import com.greentech.app.ui.components.EmptyState
import com.greentech.app.ui.components.ErrorState
import com.greentech.app.ui.components.GtSegmentado
import com.greentech.app.ui.components.IconosDominio
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.InfoAyuda
import com.greentech.app.ui.components.KpiTile
import com.greentech.app.ui.components.NumeroAnimado
import com.greentech.app.ui.components.SeveridadChip
import com.greentech.app.ui.components.SkeletonBloque
import com.greentech.app.ui.components.VacioMosaico
import com.greentech.app.ui.components.anchoCompleto
import com.greentech.app.ui.components.bento
import com.greentech.app.ui.components.charts.BarChart
import com.greentech.app.ui.components.charts.HorizontalBarChart
import com.greentech.app.ui.components.charts.SerieBarras
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.app.util.FechasUi
import com.greentech.shared.domain.model.Anomalia
import com.greentech.shared.domain.model.Dashboard
import com.greentech.shared.domain.model.Indicador
import com.greentech.shared.domain.model.MermaEspecie
import com.greentech.shared.domain.model.Periodo
import com.greentech.shared.domain.model.PuntoMensual
import com.greentech.shared.util.Fechas
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus

/** Dashboard independiente (bento con selector de periodo). En la app se muestra dentro de AdminHomeScreen. */
@Composable
fun DashboardScreen(estado: DashboardUiState, onEvent: (DashboardEvent) -> Unit, modifier: Modifier = Modifier) {
    LaunchedEffect(Unit) { onEvent(DashboardEvent.Entrar) }
    BentoGrid(modifier, contentPadding = PaddingValues(Dimens.lgx)) {
        dashboardBento(estado, onEvent, onAuditoria = null)
    }
}

/**
 * Mosaicos del dashboard: selector de periodo, 4 KPI (S), ventas vs merma (L), merma por especie (L),
 * anomalías abiertas (M) y acceso a Auditoría (M).
 */
fun LazyGridScope.dashboardBento(
    estado: DashboardUiState,
    onEvent: (DashboardEvent) -> Unit,
    onAuditoria: (() -> Unit)?,
    onAyuda: (() -> Unit)? = null,
) {
    anchoCompleto("tituloIndicadores") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                Textos.Dashboard.INDICADORES,
                style = GtTexto.tituloTarjeta,
                modifier = Modifier.weight(1f).semantics { heading() },
            )
            BotonInfo(InfoAyuda(Textos.Dashboard.INDICADORES, Textos.Dashboard.INFO_INDICADORES), onAyuda)
        }
    }
    anchoCompleto("periodo") {
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.sm)) {
            GtSegmentado(
                opciones = Periodo.entries,
                seleccionado = estado.periodo,
                onSeleccion = { onEvent(DashboardEvent.CambiarPeriodo(it)) },
                etiqueta = { it.etiqueta },
            )
            // Periodo exacto que cubren las ventas y la merma (los últimos N días hasta hoy).
            val hoy = Fechas.hoy()
            Text(
                FechasUi.rango(hoy.minus(estado.periodo.dias - 1, DateTimeUnit.DAY), hoy),
                style = GtTexto.ayuda,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite },
                textAlign = TextAlign.Center,
            )
        }
    }
    when (val d = estado.dashboard) {
        EstadoCarga.Cargando -> {
            repeat(4) { i -> bento(BentoSize.S, "skKpi$i") { SkeletonBloque(Modifier.fillMaxWidth().height(Dimens.skeletonKpi)) } }
            bento(BentoSize.L, "skGrafica") { SkeletonBloque(Modifier.fillMaxWidth().height(Dimens.skeletonGrafica)) }
        }
        is EstadoCarga.Error -> anchoCompleto("error") { ErrorState(d.mensaje, { onEvent(DashboardEvent.Entrar) }) }
        is EstadoCarga.Listo -> if (d.datos.sinDatos) {
            anchoCompleto("vacio") { EmptyState(IconosModulo.dashboard, Textos.Dashboard.VACIO, Textos.Dashboard.VACIO_DESC) }
        } else {
            contenido(d.datos, onAuditoria, onAyuda)
        }
    }
}

private fun LazyGridScope.contenido(d: Dashboard, onAuditoria: (() -> Unit)?, onAyuda: (() -> Unit)?) {
    val k: Indicador = d.indicador
    val meses = d.ventasVsMerma
    // Tendencias de la serie mensual. La variación contra el periodo anterior queda pendiente: el modelo no trae
    // el periodo anterior y comparar el mes en curso (incompleto) contra uno completo daría cifras engañosas.
    val ventas = meses.map { it.ventas.toDouble() }
    val porcMerma = meses.map { p -> if (p.ventas + p.merma == 0) 0.0 else p.merma * 100.0 / (p.ventas + p.merma) }
    bento(BentoSize.S, "kpiLotes") {
        KpiTile(
            Textos.Dashboard.LOTES_ACTIVOS, k.lotesActivos.toDouble(), IconosModulo.lote,
            info = InfoAyuda(Textos.Dashboard.LOTES_ACTIVOS, Textos.Dashboard.INFO_LOTES), onVerAyuda = onAyuda,
        )
    }
    bento(BentoSize.S, "kpiDisponibles") {
        KpiTile(
            Textos.Dashboard.DISPONIBLES, k.plantasDisponibles.toDouble(), Icons.Outlined.Spa,
            info = InfoAyuda(Textos.Dashboard.DISPONIBLES, Textos.Dashboard.INFO_DISPONIBLES), onVerAyuda = onAyuda,
        )
    }
    bento(BentoSize.S, "kpiVendidas") {
        KpiTile(
            Textos.Dashboard.VENDIDAS, k.plantasVendidas.toDouble(), IconosModulo.salida,
            tendencia = ventas, colorSerie = GreenTechTheme.colors.series.ventas,
            info = InfoAyuda(Textos.Dashboard.VENDIDAS, Textos.Dashboard.INFO_VENDIDAS), onVerAyuda = onAyuda,
        )
    }
    bento(BentoSize.S, "kpiMerma") {
        KpiTile(
            Textos.Dashboard.PORC_MERMA, k.porcentajeMerma, Icons.Outlined.Percent,
            decimales = 1, sufijo = " %", tendencia = porcMerma,
            mejorSiSube = false, colorSerie = GreenTechTheme.colors.series.merma,
            info = InfoAyuda(Textos.Dashboard.PORC_MERMA, Textos.Dashboard.INFO_MERMA), onVerAyuda = onAyuda,
        )
    }
    bento(BentoSize.L, "ventasMerma") { MosaicoVentasMerma(meses) }
    bento(BentoSize.L, "mermaEspecie") { MosaicoMermaEspecie(d.mermaPorEspecie) }
    bento(BentoSize.M, "anomalias") { MosaicoAnomalias(d.anomaliasAbiertas) }
    if (onAuditoria != null) {
        bento(BentoSize.M, "auditoria") {
            BentoTile(
                BentoSize.M,
                etiqueta = Textos.Dashboard.AUDITORIA,
                icono = IconosModulo.auditoria,
                onClick = onAuditoria,
                contexto = Textos.Dashboard.AUDITORIA_DESC,
            ) {
                Text(Textos.Auditoria.SUBTITULO, style = GtTexto.tituloTarjeta)
            }
        }
    }
}

@Composable
private fun MosaicoVentasMerma(meses: List<PuntoMensual>) {
    BentoTile(BentoSize.L, etiqueta = Textos.Dashboard.VENTAS_VS_MERMA, icono = IconosModulo.dashboard) {
        BarChart(
            etiquetas = meses.map(PuntoMensual::mes),
            series = listOf(
                SerieBarras(Textos.Dashboard.VENTAS, GreenTechTheme.colors.series.ventas, meses.map(PuntoMensual::ventas)),
                SerieBarras(Textos.Dashboard.MERMA, GreenTechTheme.colors.series.merma, meses.map(PuntoMensual::merma)),
            ),
            descripcion = Textos.Dashboard.GRAFICA_BARRAS,
        )
    }
}

@Composable
private fun MosaicoMermaEspecie(datos: List<MermaEspecie>) {
    BentoTile(BentoSize.L, etiqueta = Textos.Dashboard.MERMA_ESPECIE, icono = IconosModulo.merma, contexto = Textos.Dashboard.MERMA_DESC) {
        if (datos.isEmpty()) {
            VacioMosaico(Icons.Outlined.TaskAlt, Textos.Dashboard.SIN_MERMA, colorIcono = GreenTechTheme.colors.success)
        } else {
            HorizontalBarChart(
                datos = datos.sortedByDescending { it.cantidad }.map { it.especie to it.cantidad },
                color = GreenTechTheme.colors.series.merma,
                descripcion = Textos.Dashboard.GRAFICA_ESPECIES,
            )
        }
    }
}

@Composable
private fun MosaicoAnomalias(anomalias: List<Anomalia>) {
    BentoTile(BentoSize.M, etiqueta = Textos.Dashboard.ANOMALIAS, icono = IconosModulo.anomalia) {
        if (anomalias.isEmpty()) {
            VacioMosaico(Icons.Outlined.TaskAlt, Textos.Dashboard.SIN_ANOMALIAS, colorIcono = GreenTechTheme.colors.success)
        } else {
            NumeroAnimado(anomalias.size.toDouble(), GtTexto.dato)
            Spacer(Modifier.height(Dimens.sm))
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.sm)) {
                anomalias.take(3).forEach { a ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            IconosDominio.anomalia(a.tipo), null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(Dimens.iconoChico),
                        )
                        Spacer(Modifier.width(Dimens.sm))
                        Column(Modifier.weight(1f)) {
                            Text("${a.tipo.etiqueta}: ${a.nombre}", style = GtTexto.tituloTarjeta, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(
                                "${a.especie} · ${a.loteCodigo}",
                                style = GtTexto.ayuda,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                            )
                        }
                        SeveridadChip(a.severidad)
                    }
                }
            }
        }
    }
}

private val dashboardPreview = DashboardUiState(
    dashboard = EstadoCarga.Listo(
        Dashboard(
            Indicador(10, 6149, 1820, 210),
            listOf(
                PuntoMensual("Abr", 700, 80), PuntoMensual("May", 820, 90), PuntoMensual("Jun", 640, 40),
                PuntoMensual("Jul", 910, 75), PuntoMensual("Ago", 880, 60), PuntoMensual("Sep", 960, 52),
            ),
            listOf(MermaEspecie("Rosa", 60), MermaEspecie("Agave", 40), MermaEspecie("Lavanda", 22)),
            emptyList(),
        ),
    ),
)

@Preview
@Composable
private fun DashboardPreviewClaro() {
    GreenTechTheme { DashboardScreen(dashboardPreview, {}, Modifier.fillMaxSize()) }
}

@Preview
@Composable
private fun DashboardPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { DashboardScreen(dashboardPreview, {}, Modifier.fillMaxSize()) }
}
