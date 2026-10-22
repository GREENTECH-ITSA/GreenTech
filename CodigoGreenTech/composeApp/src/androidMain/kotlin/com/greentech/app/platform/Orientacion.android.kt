package com.greentech.app.platform

import android.content.pm.ActivityInfo
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalConfiguration

@Composable
actual fun FijarVerticalEnTelefono() {
    val actividad = LocalActivity.current ?: return
    val telefono = LocalConfiguration.current.smallestScreenWidthDp < 600
    DisposableEffect(actividad, telefono) {
        if (telefono) actividad.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onDispose { if (telefono) actividad.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
    }
}
