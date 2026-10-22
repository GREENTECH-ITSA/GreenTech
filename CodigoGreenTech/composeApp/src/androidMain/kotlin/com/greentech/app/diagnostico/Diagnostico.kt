package com.greentech.app.diagnostico

import android.app.Activity
import android.os.StrictMode
import android.util.Log
import androidx.metrics.performance.JankStats
import androidx.metrics.performance.PerformanceMetricsState
import com.greentech.app.BuildConfig

/**
 * Herramientas de diagnóstico que solo corren en debug (BuildConfig.DEBUG es constante: R8 quita
 * todo esto del release).
 *
 * - StrictMode: registra en logcat ("StrictMode") lecturas/escrituras de disco y red en el hilo
 *   principal, y fugas de objetos cerrables, de actividades y de registros.
 * - JankStats: registra cada frame lento (etiqueta "GtJank") con la pantalla en la que ocurrió.
 *   Revisar con: adb logcat -s GtJank
 */
object Diagnostico {

    fun activarStrictMode() {
        if (!BuildConfig.DEBUG) return
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build(),
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedClosableObjects()
                .detectActivityLeaks()
                .detectLeakedRegistrationObjects()
                .penaltyLog()
                .build(),
        )
    }

    private var jank: JankStats? = null

    fun activarJankStats(actividad: Activity) {
        if (!BuildConfig.DEBUG) return
        jank = JankStats.createAndTrack(actividad.window) { frame ->
            if (frame.isJank) {
                val ms = frame.frameDurationUiNanos / 1_000_000
                Log.w(ETIQUETA, "frame lento ${ms} ms ${frame.states.joinToString { "${it.key}=${it.value}" }}")
            }
        }
    }

    /** Anota la pantalla actual en los frames que registre JankStats. */
    fun pantalla(actividad: Activity, nombre: String) {
        if (!BuildConfig.DEBUG) return
        PerformanceMetricsState.getHolderForHierarchy(actividad.window.decorView).state?.putState("pantalla", nombre)
    }

    private const val ETIQUETA = "GtJank"
}
