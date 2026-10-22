package com.greentech.app.ui.components

import androidx.compose.runtime.Composable
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationEventHandler
import androidx.navigationevent.compose.rememberNavigationEventState

/** Intercepta el "atrás" del sistema (botón o gesto) mientras [activo] sea true. */
@Composable
fun ManejarAtras(activo: Boolean, onAtras: () -> Unit) {
    NavigationEventHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = activo,
        onBackCompleted = onAtras,
    )
}
