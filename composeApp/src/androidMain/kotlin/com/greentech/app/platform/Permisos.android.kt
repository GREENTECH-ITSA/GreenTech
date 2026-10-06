package com.greentech.app.platform

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.greentech.app.permissions.PermissionState
import com.greentech.app.permissions.cameraGranted
import com.greentech.app.permissions.notificationsGranted
import com.greentech.app.permissions.notificationsPermission
import com.greentech.app.permissions.rememberPermissionState

@Composable
actual fun rememberEstadoPermiso(permiso: Permiso, alResponder: (Boolean) -> Unit): EstadoPermiso {
    val estado = when (permiso) {
        Permiso.CAMARA -> rememberPermissionState(Manifest.permission.CAMERA, ::cameraGranted, alResponder)
        Permiso.NOTIFICACIONES -> rememberPermissionState(notificationsPermission, ::notificationsGranted, alResponder)
    }
    return remember(estado) { EstadoPermisoAndroid(estado) }
}

private class EstadoPermisoAndroid(private val estado: PermissionState) : EstadoPermiso {
    override val concedido: Boolean get() = estado.isGranted
    override val negadoPermanente: Boolean get() = estado.isPermanentlyDenied
    override fun solicitar() = estado.launchRequest()
    override fun abrirAjustes() = estado.openAppSettings()
}
