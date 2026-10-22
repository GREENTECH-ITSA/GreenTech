package com.greentech.app.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.SystemClock
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
 * - [launchRequest] siempre intenta el diálogo del sistema: el usuario permite con un toque, sin ir a
 *   Ajustes. Solo si Android ya no deja mostrarlo (se negó dos veces o "No volver a preguntar") se
 *   abren los Ajustes de la app automáticamente, sin pasos intermedios.
 * - [isPermanentlyDenied] se decide por la respuesta del sistema (el diálogo no apareció), no por
 *   suposición; se borra en cuanto el permiso se concede, así quitarlo después desde Ajustes vuelve a
 *   permitir el diálogo.
 * - [permission] = null cuando el permiso no existe en esta versión de Android (notificaciones antes
 *   de Android 13): ahí se abre directo la pantalla de notificaciones de la app.
 */
@Stable
class PermissionState internal constructor(
    val permission: String?,
    private val context: Context,
    private val isGrantedCheck: (Context) -> Boolean,
    private val settingsIntent: (Context) -> Intent = ::appDetailsIntent,
) {
    var isGranted by mutableStateOf(isGrantedCheck(context))
        private set

    var isPermanentlyDenied by mutableStateOf(false)
        private set

    internal var activity: Activity? = null
    internal var launcher: ActivityResultLauncher<String>? = null

    private val prefs get() = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** El sistema ya no muestra el diálogo para este permiso (lo dijo su respuesta). */
    private var blocked: Boolean
        get() = permission != null && prefs.getBoolean(KEY_BLOCKED + permission, false)
        set(value) {
            if (permission != null) prefs.edit { putBoolean(KEY_BLOCKED + permission, value) }
        }

    private var requestedAt = 0L

    fun refresh() {
        isGranted = isGrantedCheck(context)
        if (isGranted) blocked = false
        isPermanentlyDenied = !isGranted && (permission == null || blocked)
    }

    /**
     * Muestra el diálogo del sistema. Si Android ya no lo deja mostrar, abre los Ajustes de la app
     * (o la pantalla de notificaciones si el permiso no existe en esta versión).
     */
    fun launchRequest() {
        val launcher = launcher
        if (permission == null || launcher == null) {
            openAppSettings()
            return
        }
        requestedAt = SystemClock.elapsedRealtime()
        launcher.launch(permission)
    }

    /** Respuesta del diálogo. Una negativa inmediata, sin diálogo visible, significa que Android lo bloqueó. */
    internal fun onResult(granted: Boolean) {
        val activity = activity
        if (!granted && permission != null && activity != null &&
            !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        ) {
            val sinDialogo = SystemClock.elapsedRealtime() - requestedAt < UMBRAL_SIN_DIALOGO_MS
            blocked = true
            refresh()
            if (sinDialogo) openAppSettings()
            return
        }
        refresh()
    }

    fun openAppSettings() {
        val origen = activity ?: context
        val intent = settingsIntent(context).apply { if (activity == null) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        runCatching { origen.startActivity(intent) }.onFailure {
            runCatching { origen.startActivity(appDetailsIntent(context).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
        }
    }

    private companion object {
        const val PREFS = "permisos"
        const val KEY_BLOCKED = "bloqueado."

        /** Si el sistema responde en menos de esto, no llegó a mostrar el diálogo. */
        const val UMBRAL_SIN_DIALOGO_MS = 400L
    }
}

/** Ajustes de la app (pantalla de permisos). */
fun appDetailsIntent(context: Context): Intent =
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))

/** Pantalla de notificaciones de la app: un solo interruptor, sin buscar en Ajustes. */
fun notificationSettingsIntent(context: Context): Intent =
    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)

/** Permiso de cámara: escanear QR, linterna y fotos. */
fun cameraGranted(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

/** Notificaciones: en Android 13+ depende de POST_NOTIFICATIONS; antes, de que no estén apagadas en Ajustes. */
fun notificationsGranted(context: Context): Boolean = NotificationManagerCompat.from(context).areNotificationsEnabled()

/** Permiso de notificaciones de esta versión de Android (null antes de Android 13). */
val notificationsPermission: String?
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS else null

/** Escritura en almacenamiento compartido: solo hace falta en Android 8–9 (null en 10+). */
val writeStoragePermission: String?
    get() = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) Manifest.permission.WRITE_EXTERNAL_STORAGE else null

@Composable
fun rememberPermissionState(
    permission: String?,
    isGrantedCheck: (Context) -> Boolean = { ctx ->
        permission == null || ContextCompat.checkSelfPermission(ctx, permission) == PackageManager.PERMISSION_GRANTED
    },
    onResult: (Boolean) -> Unit = {},
    settingsIntent: (Context) -> Intent = ::appDetailsIntent,
): PermissionState {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val state = remember(permission) {
        PermissionState(permission, context.applicationContext, isGrantedCheck, settingsIntent).also {
            it.activity = activity
            it.refresh()
        }
    }
    state.activity = activity
    val currentOnResult by rememberUpdatedState(onResult)
    state.launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        state.onResult(granted)
        currentOnResult(granted)
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { state.refresh() }
    return state
}
