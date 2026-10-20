package com.greentech.app.navigation

import androidx.compose.runtime.LaunchedEffect
import com.greentech.app.platform.Medicion
import com.greentech.app.platform.AnotarPantalla
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.EspacioBarraInferior
import com.greentech.app.ui.components.IconosModulo
import com.greentech.app.ui.components.LocalEspacioInferior
import com.greentech.app.ui.components.LocalHazeState
import com.greentech.app.ui.components.fuenteVidrio
import dev.chrisbanes.haze.rememberHazeState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.greentech.app.ui.components.LocalReseleccionPestana
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.greentech.app.ui.components.GtBottomBar
import com.greentech.app.ui.components.GtBottomItem
import com.greentech.app.ui.notification.NotificationManager
import com.greentech.app.ui.screens.admin.home.AdminHomeLogic
import com.greentech.app.ui.screens.admin.home.AdminHomeScreen
import com.greentech.app.ui.screens.admin.personal.AltaTrabajadorLogic
import com.greentech.app.ui.screens.admin.personal.AltaTrabajadorScreen
import com.greentech.app.ui.screens.admin.personal.TrabajadoresLogic
import com.greentech.app.ui.screens.admin.personal.TrabajadoresScreen
import com.greentech.app.ui.screens.admin.reportes.DashboardLogic
import com.greentech.app.ui.screens.admin.supervision.AuditoriaLogic
import com.greentech.app.ui.screens.admin.supervision.AuditoriaScreen
import com.greentech.app.ui.screens.admin.supervision.LotesLogic
import com.greentech.app.ui.screens.admin.supervision.LotesScreen
import com.greentech.app.ui.screens.auth.CambiarPasswordScreen
import com.greentech.app.ui.screens.auth.LoginScreen
import com.greentech.app.ui.screens.auth.SplashScreen
import com.greentech.app.ui.screens.common.AyudaLogic
import com.greentech.app.ui.screens.common.IntroduccionEvent
import com.greentech.app.ui.screens.common.IntroduccionLogic
import com.greentech.app.ui.screens.common.IntroduccionScreen
import com.greentech.app.ui.components.SolicitudNotificaciones
import com.greentech.app.ui.components.ControlIntroduccion
import com.greentech.app.ui.components.LocalIntroduccion
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greentech.app.ui.screens.common.AyudaScreen
import com.greentech.app.ui.screens.common.PerfilScreen
import com.greentech.app.ui.screens.employee.home.EmployeeHomeLogic
import com.greentech.app.ui.screens.employee.home.EmployeeHomeScreen
import com.greentech.app.ui.screens.employee.lotes.ListaLotesLogic
import com.greentech.app.ui.screens.employee.lotes.ListaLotesScreen
import com.greentech.app.ui.screens.employee.lotes.QrLoteLogic
import com.greentech.app.ui.screens.employee.lotes.QrLoteScreen
import com.greentech.app.ui.screens.employee.lotes.RegistrarLoteLogic
import com.greentech.app.ui.screens.employee.lotes.RegistrarLoteScreen
import com.greentech.app.ui.screens.employee.monitoreo.ActividadLogic
import com.greentech.app.ui.screens.employee.monitoreo.ActividadScreen
import com.greentech.app.ui.screens.employee.monitoreo.AnomaliaLogic
import com.greentech.app.ui.screens.employee.monitoreo.AnomaliaScreen
import com.greentech.app.ui.screens.employee.monitoreo.BitacoraLogic
import com.greentech.app.ui.screens.employee.monitoreo.BitacoraScreen
import com.greentech.app.ui.screens.employee.monitoreo.CondicionesLogic
import com.greentech.app.ui.screens.employee.monitoreo.CondicionesScreen
import com.greentech.app.ui.screens.employee.monitoreo.DetalleLoteLogic
import com.greentech.app.ui.screens.employee.monitoreo.DetalleLoteScreen
import com.greentech.app.ui.screens.employee.monitoreo.EscanerQrLogic
import com.greentech.app.ui.screens.employee.monitoreo.EscanerQrScreen
import com.greentech.app.platform.CameraScanner
import com.greentech.app.platform.VistaCamara
import com.greentech.app.ui.screens.employee.monitoreo.EtapaLogic
import com.greentech.app.ui.screens.employee.monitoreo.EtapaScreen
import com.greentech.app.ui.screens.employee.monitoreo.ObservacionLogic
import com.greentech.app.ui.screens.employee.monitoreo.ObservacionScreen
import com.greentech.app.ui.screens.employee.monitoreo.RecomendacionesLogic
import com.greentech.app.ui.screens.employee.monitoreo.RecomendacionesScreen
import com.greentech.app.ui.screens.employee.salidas.RegistrarSalidaLogic
import com.greentech.app.ui.screens.employee.salidas.RegistrarSalidaScreen
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Motion
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

// ---------------------------------------------------------------------------
// Transiciones: fade + desplazamiento lateral de 24 dp (250 ms).
// ---------------------------------------------------------------------------
private class Transiciones(desplazamiento: Int) {
    val entrada: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        fadeIn(Motion.tween(Motion.NAVEGACION)) + slideInHorizontally(Motion.tween(Motion.NAVEGACION)) { desplazamiento }
    }
    val salida: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        fadeOut(Motion.tween(Motion.MEDIA)) + slideOutHorizontally(Motion.tween(Motion.NAVEGACION)) { -desplazamiento }
    }
    val entradaAtras: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        fadeIn(Motion.tween(Motion.NAVEGACION)) + slideInHorizontally(Motion.tween(Motion.NAVEGACION)) { -desplazamiento }
    }
    val salidaAtras: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        fadeOut(Motion.tween(Motion.MEDIA)) + slideOutHorizontally(Motion.tween(Motion.NAVEGACION)) { desplazamiento }
    }
}

@Composable
private fun rememberTransiciones(): Transiciones {
    val px = with(LocalDensity.current) { 24.dp.roundToPx() }
    return remember(px) { Transiciones(px) }
}

/**
 * Grafo raíz: Splash → Login → panel según el rol. Cada panel tiene su propio
 * NavHost: el administrador no ve pantallas de campo y el empleado no ve las de administración.
 */
@Composable
fun AppNavGraph(notificaciones: NotificationManager) {
    val raiz = rememberNavController()
    NavHost(
        navController = raiz,
        startDestination = AppDestination.Splash,
        enterTransition = { fadeIn(Motion.lenta()) },
        exitTransition = { fadeOut(Motion.lenta()) },
    ) {
        composable<AppDestination.Splash> {
            val navegador = remember { Navegador(raiz, raiz, notificaciones) }
            Conectar(koinViewModel<com.greentech.app.ui.screens.auth.SplashLogic>(), navegador) { _, _ -> SplashScreen() }
        }
        composable<AppDestination.Login> {
            val navegador = remember { Navegador(raiz, raiz, notificaciones) }
            Conectar(koinViewModel<com.greentech.app.ui.screens.auth.LoginLogic>(), navegador) { s, on -> LoginScreen(s, on) }
        }
        composable<AppDestination.PanelEmpleado> { PanelEmpleado(raiz, notificaciones) }
        composable<AppDestination.PanelAdministrador> { PanelAdministrador(raiz, notificaciones) }
    }
}

// ---------------------------------------------------------------------------
// Panel del EMPLEADO: Inicio · Lotes · Escanear · Perfil
// ---------------------------------------------------------------------------
private val pestanasEmpleado = listOf(
    AppDestination.InicioEmpleado,
    AppDestination.ListaLotes,
    AppDestination.EscanerQr,
    AppDestination.Perfil,
)
private val itemsEmpleado = listOf(
    GtBottomItem(Textos.Navegacion.INICIO, IconosModulo.inicio, IconosModulo.Seleccionado.inicio),
    GtBottomItem(Textos.Navegacion.LOTES, IconosModulo.lote, IconosModulo.Seleccionado.lote),
    GtBottomItem(Textos.Navegacion.ESCANEAR, IconosModulo.escanear, IconosModulo.Seleccionado.escanear, destacado = true),
    GtBottomItem(Textos.Navegacion.PERFIL, IconosModulo.perfil, IconosModulo.Seleccionado.perfil),
)

@Composable
private fun PanelEmpleado(raiz: NavHostController, notificaciones: NotificationManager) {
    val nav = rememberNavController()
    val navegador = remember(nav) { Navegador(nav, raiz, notificaciones, pestanasEmpleado) }
    val t = rememberTransiciones()
    ConIntroduccion {
        PanelConBarra(nav, navegador, pestanasEmpleado, itemsEmpleado) {
            NavHost(
                navController = nav,
                startDestination = AppDestination.InicioEmpleado,
                modifier = it,
                enterTransition = t.entrada, exitTransition = t.salida,
                popEnterTransition = t.entradaAtras, popExitTransition = t.salidaAtras,
            ) {
                pantallasEmpleado(navegador)
                pantallasConsultaLote(navegador)
                pantallasComunes(navegador)
            }
        }
    }
}

/** Pantallas de campo: solo existen en el grafo del empleado. */
private fun NavGraphBuilder.pantallasEmpleado(navegador: Navegador) {
    composable<AppDestination.InicioEmpleado> {
        Conectar(koinViewModel<EmployeeHomeLogic>(), navegador) { s, on -> EmployeeHomeScreen(s, on) }
    }
    composable<AppDestination.ListaLotes> {
        Conectar(koinViewModel<ListaLotesLogic>(), navegador) { s, on -> ListaLotesScreen(s, on) }
    }
    composable<AppDestination.RegistrarLote> {
        Conectar(koinViewModel<RegistrarLoteLogic>(), navegador) { s, on -> RegistrarLoteScreen(s, on) }
    }
    composable<AppDestination.QrLote> { entrada ->
        val ruta = entrada.toRoute<AppDestination.QrLote>()
        Conectar(koinViewModel<QrLoteLogic> { parametersOf(ruta.loteId, ruta.recienCreado) }, navegador) { s, on -> QrLoteScreen(s, on) }
    }
    composable<AppDestination.EscanerQr> {
        val camara = koinInject<CameraScanner>()
        LaunchedEffect(Unit) { Medicion.iniciar(Medicion.QR_ABRIR_A_DETALLE) }
        Conectar(koinViewModel<EscanerQrLogic>(), navegador) { s, on ->
            EscanerQrScreen(s, on, vistaCamara = { VistaCamara(camara, it) })
        }
    }
    composable<AppDestination.CambiarEtapa> { entrada ->
        val ruta = entrada.toRoute<AppDestination.CambiarEtapa>()
        Conectar(koinViewModel<EtapaLogic> { parametersOf(ruta.loteId) }, navegador) { s, on -> EtapaScreen(s, on) }
    }
    composable<AppDestination.NuevaObservacion> { entrada ->
        val ruta = entrada.toRoute<AppDestination.NuevaObservacion>()
        Conectar(koinViewModel<ObservacionLogic> { parametersOf(ruta.loteId) }, navegador) { s, on -> ObservacionScreen(s, on) }
    }
    composable<AppDestination.RegistrarActividad> { entrada ->
        val ruta = entrada.toRoute<AppDestination.RegistrarActividad>()
        Conectar(koinViewModel<ActividadLogic> { parametersOf(ruta.loteId) }, navegador) { s, on -> ActividadScreen(s, on) }
    }
    composable<AppDestination.ReportarAnomalia> { entrada ->
        val ruta = entrada.toRoute<AppDestination.ReportarAnomalia>()
        Conectar(koinViewModel<AnomaliaLogic> { parametersOf(ruta.loteId) }, navegador) { s, on -> AnomaliaScreen(s, on) }
    }
    composable<AppDestination.Recomendaciones> { entrada ->
        val ruta = entrada.toRoute<AppDestination.Recomendaciones>()
        Conectar(koinViewModel<RecomendacionesLogic> { parametersOf(ruta.loteId) }, navegador) { s, on -> RecomendacionesScreen(s, on) }
    }
    composable<AppDestination.RegistrarSalida> { entrada ->
        val ruta = entrada.toRoute<AppDestination.RegistrarSalida>()
        Conectar(koinViewModel<RegistrarSalidaLogic> { parametersOf(ruta.loteId) }, navegador) { s, on -> RegistrarSalidaScreen(s, on) }
    }
}

/** Consulta de un lote (detalle, condiciones, bitácora): ambos roles; el administrador en solo lectura. */
private fun NavGraphBuilder.pantallasConsultaLote(navegador: Navegador) {
    composable<AppDestination.DetalleLote> { entrada ->
        val ruta = entrada.toRoute<AppDestination.DetalleLote>()
        Conectar(koinViewModel<DetalleLoteLogic> { parametersOf(ruta.loteId, ruta.soloLectura) }, navegador) { s, on ->
            DetalleLoteScreen(s, on)
        }
    }
    composable<AppDestination.Condiciones> { entrada ->
        val ruta = entrada.toRoute<AppDestination.Condiciones>()
        Conectar(koinViewModel<CondicionesLogic> { parametersOf(ruta.loteId) }, navegador) { s, on -> CondicionesScreen(s, on) }
    }
    composable<AppDestination.Bitacora> { entrada ->
        val ruta = entrada.toRoute<AppDestination.Bitacora>()
        Conectar(koinViewModel<BitacoraLogic> { parametersOf(ruta.loteId, ruta.soloLectura) }, navegador) { s, on ->
            BitacoraScreen(s, on)
        }
    }
}

// ---------------------------------------------------------------------------
// Panel del ADMINISTRADOR: Dashboard · Personal · Lotes · Perfil
// ---------------------------------------------------------------------------
private val pestanasAdmin = listOf(
    AppDestination.Dashboard,
    AppDestination.Trabajadores,
    AppDestination.SupervisionLotes,
    AppDestination.Perfil,
)
private val itemsAdmin = listOf(
    GtBottomItem(Textos.Navegacion.DASHBOARD, IconosModulo.dashboard, IconosModulo.Seleccionado.dashboard),
    GtBottomItem(Textos.Navegacion.PERSONAL, IconosModulo.personal, IconosModulo.Seleccionado.personal),
    GtBottomItem(Textos.Navegacion.LOTES, IconosModulo.lote, IconosModulo.Seleccionado.lote),
    GtBottomItem(Textos.Navegacion.PERFIL, IconosModulo.perfil, IconosModulo.Seleccionado.perfil),
)

@Composable
private fun PanelAdministrador(raiz: NavHostController, notificaciones: NotificationManager) {
    val nav = rememberNavController()
    val navegador = remember(nav) { Navegador(nav, raiz, notificaciones, pestanasAdmin) }
    val t = rememberTransiciones()
    ConIntroduccion {
        PanelConBarra(nav, navegador, pestanasAdmin, itemsAdmin) {
            NavHost(
                navController = nav,
                startDestination = AppDestination.Dashboard,
                modifier = it,
                enterTransition = t.entrada, exitTransition = t.salida,
                popEnterTransition = t.entradaAtras, popExitTransition = t.salidaAtras,
            ) {
                pantallasAdministrador(navegador)
                pantallasConsultaLote(navegador)
                pantallasComunes(navegador)
            }
        }
    }
}

/**
 * Bienvenida de primer uso del panel (una vez por usuario). Ayuda y Perfil la vuelven a abrir con
 * "Ver introducción" a través de [LocalIntroduccion].
 */
@Composable
private fun ConIntroduccion(panel: @Composable () -> Unit) {
    val logic = koinViewModel<IntroduccionLogic>()
    val estado by logic.state.collectAsStateWithLifecycle()
    val control = remember(logic) { ControlIntroduccion { logic.onEvent(IntroduccionEvent.Abrir) } }
    CompositionLocalProvider(LocalIntroduccion provides control) { panel() }
    IntroduccionScreen(estado, logic::onEvent)
    SolicitudNotificaciones(introVisible = estado.visible)
}

/** Pantallas de gestión: solo existen en el grafo del administrador. */
private fun NavGraphBuilder.pantallasAdministrador(navegador: Navegador) {
    composable<AppDestination.Dashboard> {
        Conectar(koinViewModel<AdminHomeLogic>(), navegador) { inicio, onInicio ->
            Conectar(koinViewModel<DashboardLogic>(), navegador) { dashboard, onDashboard ->
                AdminHomeScreen(inicio, onInicio, dashboard, onDashboard)
            }
        }
    }
    composable<AppDestination.Trabajadores> {
        Conectar(koinViewModel<TrabajadoresLogic>(), navegador) { s, on -> TrabajadoresScreen(s, on) }
    }
    composable<AppDestination.AltaTrabajador> {
        Conectar(koinViewModel<AltaTrabajadorLogic>(), navegador) { s, on -> AltaTrabajadorScreen(s, on) }
    }
    composable<AppDestination.SupervisionLotes> {
        Conectar(koinViewModel<LotesLogic>(), navegador) { s, on -> LotesScreen(s, on) }
    }
    composable<AppDestination.Auditoria> {
        Conectar(koinViewModel<AuditoriaLogic>(), navegador) { s, on -> AuditoriaScreen(s, on) }
    }
}

// ---------------------------------------------------------------------------
// Pantallas comunes a ambos paneles (la Ayuda muestra contenido según el rol)
// ---------------------------------------------------------------------------
private fun NavGraphBuilder.pantallasComunes(navegador: Navegador) {
    composable<AppDestination.Ayuda> {
        Conectar(koinViewModel<AyudaLogic>(), navegador) { s, on -> AyudaScreen(s, on) }
    }
    composable<AppDestination.Perfil> {
        Conectar(koinViewModel<com.greentech.app.ui.screens.common.PerfilLogic>(), navegador) { s, on -> PerfilScreen(s, on) }
    }
    composable<AppDestination.CambiarPassword> {
        Conectar(koinViewModel<com.greentech.app.ui.screens.auth.CambiarPasswordLogic>(), navegador) { s, on ->
            CambiarPasswordScreen(s, on)
        }
    }
}

/**
 * Contenedor del panel: el contenido ocupa toda la pantalla y la barra inferior flota encima
 * (vidrio sobre el contenido). Solo es visible en las pestañas principales; entra con resorte.
 */
@Composable
private fun PanelConBarra(
    nav: NavHostController,
    navegador: Navegador,
    pestanas: List<AppDestination>,
    items: List<GtBottomItem>,
    contenido: @Composable (Modifier) -> Unit,
) {
    val entrada by nav.currentBackStackEntryAsState()
    val destino = entrada?.destination
    val indice = pestanas.indexOfFirst { destino.esRuta(it) }
    AnotarPantalla(destino?.route?.substringAfterLast('.')?.substringBefore('/')?.substringBefore('?'))
    val haze = rememberHazeState()
    // Se pasa el State (no el valor): el contenido solo vuelve a hacer layout mientras se anima.
    val espacio = animateDpAsState(if (indice >= 0) EspacioBarraInferior else 0.dp, Motion.media(), label = "espacioBarra")
    // Tocar la pestaña activa no navega: la pantalla vuelve al inicio de su lista.
    var reselecciones by remember { mutableIntStateOf(0) }
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        CompositionLocalProvider(LocalEspacioInferior provides espacio, LocalReseleccionPestana provides reselecciones) {
            contenido(Modifier.fillMaxSize().fuenteVidrio(haze))
        }
        AnimatedVisibility(
            visible = indice >= 0,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(Motion.resorteEntrada()) { it } + fadeIn(Motion.media()),
            exit = slideOutVertically(Motion.media()) { it } + fadeOut(Motion.rapida()),
        ) {
            CompositionLocalProvider(LocalHazeState provides haze) {
                GtBottomBar(
                    items,
                    seleccionado = indice,
                    onSeleccion = { if (it == indice) reselecciones++ else navegador.irAPestana(pestanas[it]) },
                )
            }
        }
    }
}

private fun NavDestination?.esRuta(destino: AppDestination): Boolean = this?.hasRoute(destino::class) == true
