package com.greentech.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.UiEffect
import com.greentech.app.ui.components.LocalRetroalimentacion
import com.greentech.app.ui.components.Retroalimentacion
import com.greentech.app.ui.notification.NotificationManager
import com.greentech.app.ui.notification.TipoNotificacion
import com.greentech.app.ui.theme.Motion
import kotlinx.coroutines.delay
import com.greentech.shared.domain.model.Rol

/**
 * Ejecuta los efectos de los Logic: navegar dentro del panel, volver, cambiar de panel
 * (limpiando la pila) y mostrar notificaciones.
 */
class Navegador(
    private val nav: NavHostController,
    private val raiz: NavHostController,
    private val notificaciones: NotificationManager,
    private val pestanas: List<AppDestination> = emptyList(),
) {
    fun manejar(efecto: UiEffect) {
        when (efecto) {
            is UiEffect.Notificar -> notificaciones.mostrar(efecto.tipo, efecto.mensaje, efecto.titulo)
            is UiEffect.Navegar -> navegar(efecto.destino, efecto.reemplazar)
            UiEffect.Volver -> if (!nav.popBackStack()) raiz.popBackStack()
            is UiEffect.EntrarAlPanel -> {
                val panel = if (efecto.rol == Rol.ADMINISTRADOR) AppDestination.PanelAdministrador else AppDestination.PanelEmpleado
                raiz.navigate(panel) { popUpTo(raiz.graph.id) { inclusive = true } }
            }
            UiEffect.IrAlLogin -> raiz.navigate(AppDestination.Login) { popUpTo(raiz.graph.id) { inclusive = true } }
        }
    }

    fun navegar(destino: AppDestination, reemplazar: Boolean = false) {
        if (destino in pestanas) {
            irAPestana(destino)
            return
        }
        val actual = nav.currentBackStackEntry?.destination?.id
        nav.navigate(destino) {
            launchSingleTop = true
            if (reemplazar && actual != null) popUpTo(actual) { inclusive = true }
        }
    }

    fun irAPestana(destino: AppDestination) {
        nav.navigate(destino) {
            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun volver() = manejar(UiEffect.Volver)
}

/**
 * Conecta un Logic con su pantalla: observa el estado y ejecuta sus efectos.
 *
 * Además avisa a la pantalla (LocalRetroalimentacion) cuando se notifica un éxito o un error, para
 * las micro-interacciones (check del botón de guardar, sacudida de campos). Tras un éxito espera
 * 600 ms antes del siguiente efecto para que el check se vea antes de navegar.
 */
@Composable
fun <S, E> Conectar(
    logic: BaseLogic<S, E>,
    navegador: Navegador,
    pantalla: @Composable (estado: S, onEvent: (E) -> Unit) -> Unit,
) {
    val estado by logic.state.collectAsStateWithLifecycle()
    val retro = remember(logic) { Retroalimentacion() }
    LaunchedEffect(logic) {
        logic.efectos.collect { efecto ->
            navegador.manejar(efecto)
            if (efecto is UiEffect.Notificar) {
                when (efecto.tipo) {
                    TipoNotificacion.EXITO -> {
                        retro.ultimoExito++
                        delay(Motion.ms(Motion.CHECK_GUARDADO.toInt()).toLong())
                    }
                    TipoNotificacion.ERROR -> retro.ultimoError++
                    else -> Unit
                }
            }
        }
    }
    CompositionLocalProvider(LocalRetroalimentacion provides retro) {
        pantalla(estado, logic::onEvent)
    }
}
