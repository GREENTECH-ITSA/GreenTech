package com.greentech.app.util

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToLong

/** 12500 → "12,500" */
fun Int.formatoMiles(): String {
    val negativo = this < 0
    val digitos = abs(this.toLong()).toString()
    val conComas = digitos.reversed().chunked(3).joinToString(",").reversed()
    return if (negativo) "-$conComas" else conComas
}

/** 12.345 con 1 decimal → "12.3" (sin depender de la configuración regional). */
fun Double.formatoDecimal(decimales: Int): String {
    if (decimales <= 0) return this.roundToLong().toString()
    val factor = 10.0.pow(decimales)
    val redondeado = (this * factor).roundToLong()
    val entero = redondeado / factor.toLong()
    val fraccion = abs(redondeado % factor.toLong()).toString().padStart(decimales, '0')
    val signo = if (this < 0 && entero == 0L) "-" else ""
    return "$signo$entero.$fraccion"
}
