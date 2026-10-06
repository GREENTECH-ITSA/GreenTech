package com.greentech.app.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.theme.Motion

/**
 * Cambia con AnimatedContent entre carga (skeleton), contenido, vacío y error.
 * La animación solo ocurre cuando cambia el tipo de estado, no con cada dato nuevo.
 */
@Composable
fun <T> EstadoContenido(
    estado: EstadoCarga<T>,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier,
    esVacio: (T) -> Boolean = { false },
    vacio: @Composable () -> Unit = {},
    cargando: @Composable () -> Unit = { LoadingSkeleton() },
    contenido: @Composable (T) -> Unit,
) {
    AnimatedContent(
        targetState = estado,
        modifier = modifier,
        contentKey = {
            when (it) {
                EstadoCarga.Cargando -> 0
                is EstadoCarga.Error -> 1
                is EstadoCarga.Listo -> if (esVacio(it.datos)) 2 else 3
            }
        },
        transitionSpec = { fadeIn(Motion.media()) togetherWith fadeOut(Motion.rapida()) },
        label = "estadoContenido",
    ) { e ->
        when (e) {
            EstadoCarga.Cargando -> cargando()
            is EstadoCarga.Error -> ErrorState(e.mensaje, onReintentar)
            is EstadoCarga.Listo -> if (esVacio(e.datos)) vacio() else contenido(e.datos)
        }
    }
}
