package com.greentech.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.greentech.app.navigation.AppNavGraph
import com.greentech.app.platform.AjustarBarrasSistema
import com.greentech.app.ui.components.GtNotificationHost
import com.greentech.app.ui.components.LocalHazeState
import com.greentech.app.ui.components.LocalVeloModal
import com.greentech.app.ui.components.VeloModal
import com.greentech.app.ui.components.desenfoqueModal
import com.greentech.app.ui.components.fuenteVidrio
import com.greentech.app.ui.notification.NotificationManager
import com.greentech.app.ui.theme.BarrasSistema
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.LocalApariencia
import com.greentech.app.ui.theme.PreferenciasApariencia
import com.greentech.app.ui.theme.esOscuro
import com.russhwolf.settings.Settings
import dev.chrisbanes.haze.rememberHazeState
import org.koin.compose.koinInject
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.greentech.app.platform.LocalNivelRendimiento
import com.greentech.app.platform.animacionesDelSistemaDesactivadas
import com.greentech.app.platform.detectarNivelRendimiento
import com.greentech.app.ui.theme.Motion

/**
 * Raíz de la app: preferencias de apariencia + tema + grafo de navegación + notificaciones encima.
 * El contenido es la fuente del vidrio de los banners y se desenfoca detrás de hojas y diálogos.
 */
@Composable
fun App() {
    val apariencia = remember { PreferenciasApariencia(Settings()) }
    val nivel = remember { detectarNivelRendimiento() }
    // La escala de animación del sistema puede cambiar con la app en segundo plano: se revisa al volver.
    LifecycleResumeEffect(Unit) {
        Motion.reducirPorSistema(animacionesDelSistemaDesactivadas())
        onPauseOrDispose { }
    }
    CompositionLocalProvider(LocalApariencia provides apariencia, LocalNivelRendimiento provides nivel) {
        GreenTechTheme(modo = apariencia.modo) {
            AjustarBarrasSistema(oscuro = apariencia.modo.esOscuro() || BarrasSistema.pantallasOscuras > 0)
            val notificaciones = koinInject<NotificationManager>()
            val haze = rememberHazeState()
            val velo = remember { VeloModal() }
            // Color de contenido por defecto del tema: ningún texto queda negro en oscuro aunque no esté dentro de una Surface.
            CompositionLocalProvider(LocalVeloModal provides velo, LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
                Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                    Box(Modifier.fillMaxSize().fuenteVidrio(haze).desenfoqueModal(velo)) {
                        AppNavGraph(notificaciones)
                    }
                    CompositionLocalProvider(LocalHazeState provides haze) {
                        GtNotificationHost(notificaciones, Modifier.align(Alignment.TopCenter))
                    }
                }
            }
        }
    }
}
