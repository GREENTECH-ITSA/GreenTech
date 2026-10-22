package com.greentech.app.platform

import android.os.SystemClock
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.tracing.Trace
import com.greentech.app.BuildConfig
import com.greentech.app.diagnostico.Diagnostico
import java.util.concurrent.ConcurrentHashMap

actual object Medicion {
    actual val QR_CONSULTA = "qr_consulta"
    actual val QR_BUSQUEDA = "qr_busqueda"
    actual val QR_ABRIR_A_DETALLE = "qr_abrir_a_detalle"
    actual val QR_CAMARA_LISTA = "qr_camara_lista"
    actual val LOGIN_A_INICIO = "login_a_inicio"

    private const val ETIQUETA = "GtMedicion"
    private val abiertas = ConcurrentHashMap<String, Long>()

    actual fun iniciar(seccion: String) {
        // Si quedó una abierta (p. ej. se salió antes de ver el detalle) se reemplaza: cuenta la última.
        if (abiertas.put(seccion, SystemClock.elapsedRealtime()) != null) Trace.endAsyncSection(seccion, 0)
        Trace.beginAsyncSection(seccion, 0)
    }

    actual fun terminar(seccion: String) {
        val inicio = abiertas.remove(seccion) ?: return
        Trace.endAsyncSection(seccion, 0)
        Log.i(ETIQUETA, "$seccion=${SystemClock.elapsedRealtime() - inicio} ms")
    }
}

@Composable
actual fun MarcarDibujado(listo: Boolean, vararg secciones: String) {
    ReportDrawnWhen { listo }
    if (!listo) return
    LaunchedEffect(Unit) {
        // Este bloque corre después de aplicar la composición con datos; el siguiente frame ya la dibujó.
        withFrameNanos { }
        secciones.forEach(Medicion::terminar)
    }
}

@Composable
actual fun AnotarPantalla(nombre: String?) {
    if (!BuildConfig.DEBUG || nombre == null) return
    val actividad = LocalActivity.current ?: return
    LaunchedEffect(nombre) { Diagnostico.pantalla(actividad, nombre) }
}
