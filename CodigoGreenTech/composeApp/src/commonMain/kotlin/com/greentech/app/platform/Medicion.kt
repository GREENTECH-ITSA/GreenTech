package com.greentech.app.platform

import androidx.compose.runtime.Composable

/**
 * Marcas de rendimiento (docs/RENDIMIENTO.md). En Android son secciones async de Perfetto que leen los
 * Macrobenchmarks (TraceSectionMetric) y además se escriben en logcat (etiqueta "GtMedicion") para medir
 * en un teléfono real con la cámara.
 *
 * Consulta por QR (RNF 3.3.1):
 * - [QR_CONSULTA]: T0 (código leído o confirmado) → T2 (primer frame del Detalle con datos).
 * - [QR_BUSQUEDA]: T0 → T1 (el repositorio devuelve el lote).
 * - [QR_ABRIR_A_DETALLE]: se abre Escanear → T2.
 * - [QR_CAMARA_LISTA]: se liga la cámara → primer cuadro entregado al analizador.
 */
expect object Medicion {
    fun iniciar(seccion: String)

    /** Cierra [seccion] si estaba abierta (si no, no hace nada). */
    fun terminar(seccion: String)

    val QR_CONSULTA: String
    val QR_BUSQUEDA: String
    val QR_ABRIR_A_DETALLE: String
    val QR_CAMARA_LISTA: String
    val LOGIN_A_INICIO: String
}

/**
 * Cierra [secciones] cuando se dibuja el primer frame con [listo] = true y avisa a la plataforma que la
 * pantalla terminó de dibujarse (reportFullyDrawn en Android, para el arranque).
 */
@Composable
expect fun MarcarDibujado(listo: Boolean, vararg secciones: String)

/** Anota la pantalla visible para los frames lentos que registra JankStats (solo debug). */
@Composable
expect fun AnotarPantalla(nombre: String?)
