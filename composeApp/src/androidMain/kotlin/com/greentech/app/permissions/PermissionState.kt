package com.greentech.app.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

/**
 * Estado de un permiso de Android para Compose, sin Accompanist.
 *
 * - [isGranted] se vuelve a revisar en cada ON_RESUME (al volver de Ajustes, por ejemplo).
 * - [isPermanentlyDenied]: ya se pidió, se negó y el sistema no volverá a mostrar el diálogo
 *   (shouldShowRequestPermissionRationale = false). Solo queda [openAppSettings].
 * - [permission] = null cuando el permiso no existe en esta versión de Android (notificaciones
 *   antes de Android 13): ahí solo se pueden activar desde Ajustes.
 */
@Stable
class PermissionState internal constructor(
    val permission: String?,
    private val context: Context,
    private val isGrantedCheck: (Context) -> Boolean,
) {
    var isGranted by mutableStateOf(isGrantedCheck(context))
        private set

    var isPermanentlyDenied by mutableStateOf(false)
        private set

    internal var activity: Activity? = null
    internal var launcher: ActivityResultLauncher<String>? = null

    private val prefs get() = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private val wasRequested: Boolean
        get() = permission != null && prefs.getBoolean(KEY_REQUESTED + permission, false)

    fun refresh() {
        isGranted = isGrantedCheck(context)
        isPermanentlyDenied = when {
            isGranted -> false
            permission == null -> true
            else -> wasRequested && activity?.let { !ActivityCompat.shouldShowRequestPermissionRationale(it, permission) } == true
        }
    }

    /** Muestra el diálogo del sistema; si ya no se puede, abre Ajustes. */
    fun launchRequest() {
        val launcher = launcher
        if (permission == null || launcher == null || isPermanentlyDenied) {
            openAppSettings()
            return
        }
        prefs.edit { putBoolean(KEY_REQUESTED + permission, true) }
        launcher.launch(permission)
    }

    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
        if (activity == null) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { (activity ?: context).startActivity(intent) }
    }

    private companion object {
        const val PREFS = "permisos"
        const val KEY_REQUESTED = "pedido."
    }
}

/** Permiso de cámara: escanear QR, linterna y fotos. */
fun cameraGranted(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

/** Notificaciones: en Android 13+ depende de POST_NOTIFICATIONS; antes, de que no estén apagadas en Ajustes. */
fun notificationsGranted(context: Context): Boolean = NotificationManagerCompat.from(context).areNotificationsEnabled()

/** Permiso de notificaciones de esta versión de Android (null antes de Android 13). */
val notificationsPermission: String?
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS else null

@Composable
fun rememberPermissionState(
    permission: String?,
    isGrantedCheck: (Context) -> Boolean = { ctx ->
        permission == null || ContextCompat.checkSelfPermission(ctx, permission) == PackageManager.PERMISSION_GRANTED
    },
    onResult: (Boolean) -> Unit = {},
): PermissionState {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val state = remember(permission) {
        PermissionState(permission, context.applicationContext, isGrantedCheck).also {
            it.activity = activity
            it.refresh()
        }
    }
    state.activity = activity
    val currentOnResult by rememberUpdatedState(onResult)
    state.launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        state.refresh()
        currentOnResult(granted)
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { state.refresh() }
    return state
}
