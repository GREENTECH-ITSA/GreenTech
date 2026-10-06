package com.greentech.app.ui.screens.admin.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import com.greentech.app.ui.components.AlReseleccionarPestana
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.AccionGrande
import com.greentech.app.ui.components.BentoGrid
import com.greentech.app.ui.components.BentoSize
import com.greentech.app.ui.components.GtPanelTopBar
import com.greentech.app.ui.components.GtScaffold
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.anchoCompleto
import com.greentech.app.ui.components.bento
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.screens.admin.reportes.DashboardEvent
import com.greentech.app.ui.screens.admin.reportes.DashboardUiState
import com.greentech.app.ui.screens.admin.reportes.dashboardBento
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.shared.util.Fechas

/** Pestaña "Dashboard" del administrador: saludo, indicadores en bento y accesos rápidos. */
@Composable
fun AdminHomeScreen(
    estado: AdminHomeUiState,
    onEvent: (AdminHomeEvent) -> Unit,
    dashboard: DashboardUiState,
    onDashboardEvent: (DashboardEvent) -> Unit,
) {
    LaunchedEffect(Unit) { onDashboardEvent(DashboardEvent.Entrar) }
    val cuadricula = rememberLazyGridState()
    AlReseleccionarPestana { cuadricula.animateScrollToItem(0) }
    GtScaffold(topBar = { GtPanelTopBar(onAyuda = { onEvent(AdminHomeEvent.Ayuda) }) }) { padding ->
        BentoGrid(
            modifier = Modifier.fillMaxSize(),
            estado = cuadricula,
            contentPadding = PaddingValues(
                start = 20.dp, end = 20.dp,
                top = padding.calculateTopPadding() + 4.dp,
                bottom = padding.calculateBottomPadding() + 16.dp,
            ),
        ) {
            anchoCompleto("saludo") {
                Column(Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                    Text(
                        "${Textos.Inicio.HOLA}, ${estado.nombre}",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.semantics { heading() },
                    )
                    Text(
                        estado.fecha.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            dashboardBento(
                dashboard, onDashboardEvent,
                onAuditoria = { onEvent(AdminHomeEvent.Auditoria) },
                onAyuda = { onEvent(AdminHomeEvent.Ayuda) },
            )
            anchoCompleto("tituloAccesos") {
                Text(
                    Textos.Dashboard.ACCESOS,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp).semantics { heading() },
                )
            }
            bento(BentoSize.S, "agregar") {
                AccionGrande(Icons.Outlined.PersonAddAlt, Textos.Dashboard.AGREGAR, { onEvent(AdminHomeEvent.AgregarTrabajador) }, relleno = true)
            }
            bento(BentoSize.S, "supervisar") {
                AccionGrande(IconosModulo.lote, Textos.Dashboard.SUPERVISAR, { onEvent(AdminHomeEvent.SupervisarLotes) })
            }
        }
    }
}

@Preview
@Composable
private fun AdminHomePreviewClaro() {
    GreenTechTheme {
        AdminHomeScreen(AdminHomeUiState("Ana", Fechas.larga(Fechas.hoy())), {}, DashboardUiState(dashboard = EstadoCarga.Cargando), {})
    }
}

@Preview
@Composable
private fun AdminHomePreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) {
        AdminHomeScreen(AdminHomeUiState("Ana", Fechas.larga(Fechas.hoy())), {}, DashboardUiState(dashboard = EstadoCarga.Cargando), {})
    }
}
