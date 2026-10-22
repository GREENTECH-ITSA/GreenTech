package com.greentech.app.platform

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import androidx.core.performance.DefaultDevicePerformance
import org.koin.mp.KoinPlatform

/**
 * - BAJO: el sistema lo marca como de poca RAM (Android Go), tiene menos de 3 GB, 4 núcleos o menos,
 *   o es Android 8–11 sin desenfoque por GPU (RenderEffect llega en Android 12) con menos de 4 GB.
 * - ALTO: clase de rendimiento multimedia 12 o superior (androidx.core:core-performance), u 8 GB y 8 núcleos.
 * - MEDIO: el resto.
 */
actual fun detectarNivelRendimiento(): NivelRendimiento {
    val contexto = KoinPlatform.getKoin().get<Context>()
    val am = contexto.getSystemService(ActivityManager::class.java)
    val memoria = ActivityManager.MemoryInfo().also { am.getMemoryInfo(it) }.totalMem
    val gb = memoria / (1024.0 * 1024 * 1024)
    val nucleos = Runtime.getRuntime().availableProcessors()
    val clase = DefaultDevicePerformance().mediaPerformanceClass
    return when {
        am.isLowRamDevice || gb < 3.0 || nucleos <= 4 -> NivelRendimiento.BAJO
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S && gb < 4.0 -> NivelRendimiento.BAJO
        clase >= Build.VERSION_CODES.S || (gb >= 7.0 && nucleos >= 8) -> NivelRendimiento.ALTO
        else -> NivelRendimiento.MEDIO
    }
}

actual fun animacionesDelSistemaDesactivadas(): Boolean = !android.animation.ValueAnimator.areAnimatorsEnabled()
