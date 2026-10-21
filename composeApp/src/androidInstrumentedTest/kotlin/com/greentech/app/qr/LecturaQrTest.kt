package com.greentech.app.qr

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.os.SystemClock
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.common.sdkinternal.MlKitContext
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.greentech.app.platform.QrSaver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

/**
 * RNF 3.3.6 (docs/RENDIMIENTO.md, sección 4B): el QR que exporta la app se lee con el mismo escáner de
 * ML Kit que usa la cámara (solo FORMAT_QR_CODE) en condiciones simuladas de campo. Cada caso coloca el QR
 * dentro de un cuadro de 1280×720 (la resolución de análisis) con el tamaño aparente que tendría a ~25 cm.
 *
 * No sustituye la prueba física con la cámara (pendiente, ver RENDIMIENTO.md): no modela desenfoque de
 * movimiento ni el ruido del sensor real.
 *
 *   ./gradlew :composeApp:connectedDebugAndroidTest
 *   adb logcat -s GtLecturaQr
 */
@RunWith(AndroidJUnit4::class)
class LecturaQrTest {

    private val codigo = "GT-8F2A91C0D4"
    private val lector by lazy {
        MlKitContext.initializeIfNeeded(InstrumentationRegistry.getInstrumentation().targetContext)
        BarcodeScanning.getClient(BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build())
    }
    private val qr: Bitmap by lazy { QrSaver().renderizarParaPrueba(codigo) }

    /** Lado aparente del QR en el cuadro de análisis para cada tamaño impreso (≈ 25 cm de distancia). */
    private val ladoPorTamano = mapOf("3 cm" to 150, "5 cm" to 250, "8 cm" to 400)

    @Test
    fun imagenExportadaEsImprimible() {
        assertTrue("ancho ${qr.width} px < 1024", qr.width >= 1024)
        // Esquinas de la zona de silencio: blanco puro.
        assertEquals(Color.WHITE, qr.getPixel(5, 5))
        assertEquals(Color.WHITE, qr.getPixel(qr.width - 5, 5))
    }

    @Test
    fun seLeeEnCondicionesDeCampo() {
        val casos = buildList {
            ladoPorTamano.forEach { (tamano, lado) -> add("tamaño $tamano" to cuadro(lado)) }
            add("poca luz con linterna" to cuadro(250, oscurecer = 0.45f, ruido = 18))
            add("reflejo de luz" to cuadro(250, reflejo = true))
            add("giro 12°" to cuadro(250, giro = 12f))
            add("inclinación leve (perspectiva 4 %)" to cuadro(250, perspectiva = 0.04f))
            add("etiqueta inclinada 30°" to cuadro(250, inclinacion = 30f))
            add("etiqueta inclinada 30° + giro 12°" to cuadro(250, inclinacion = 30f, giro = 12f))
            add("inclinación leve + giro 8°" to cuadro(250, giro = 8f, perspectiva = 0.04f))
            add("mancha parcial (≈8 % del código)" to cuadro(250, mancha = true))
            add("3 cm + poca luz" to cuadro(150, oscurecer = 0.5f, ruido = 12))
        }
        val resultados = casos.map { (nombre, imagen) ->
            val inicio = SystemClock.elapsedRealtime()
            val leido = runCatching {
                Tasks.await(lector.process(InputImage.fromBitmap(imagen, 0))).firstOrNull()?.rawValue
            }.getOrNull()
            val ms = SystemClock.elapsedRealtime() - inicio
            val ok = leido == codigo
            Log.i(ETIQUETA, "${if (ok) "LEE" else "FALLA"} · $nombre · ${ms} ms")
            nombre to ok
        }
        val fallos = resultados.filterNot { it.second }.map { it.first }
        assertTrue("No se leyó: $fallos", fallos.isEmpty())
    }

    /** Casos más duros que solo se registran (para conocer el límite), sin hacer fallar la prueba. */
    @Test
    fun limitesDeInclinacion() {
        listOf(150, 250, 400).forEach { lado ->
            listOf(20f, 35f, 45f, 55f, 65f).forEach { grados ->
                val leido = runCatching {
                    Tasks.await(lector.process(InputImage.fromBitmap(cuadro(lado, inclinacion = grados), 0))).firstOrNull()?.rawValue
                }.getOrNull()
                Log.i(ETIQUETA, "${if (leido == codigo) "LEE" else "FALLA"} · límite: etiqueta inclinada ${grados.toInt()}° · QR de $lado px")
            }
        }
        // Distorsión sintética combinada (cizalla + trapecio), más dura que una inclinación física.
        listOf(0.06f, 0.08f, 0.10f).forEach { p ->
            listOf(0f, 12f).forEach { g ->
                val leido = runCatching {
                    Tasks.await(lector.process(InputImage.fromBitmap(cuadro(250, giro = g, perspectiva = p), 0))).firstOrNull()?.rawValue
                }.getOrNull()
                Log.i(ETIQUETA, "${if (leido == codigo) "LEE" else "FALLA"} · límite: distorsión combinada ${(p * 100).toInt()} % + giro ${g.toInt()}°")
            }
        }
    }

    /** Tiempo de decodificación de un cuadro de 1280×720 con el QR de 5 cm (40 repeticiones). */
    @Test
    fun tiempoDeDecodificacion() {
        val imagen = cuadro(250)
        repeat(3) { Tasks.await(lector.process(InputImage.fromBitmap(imagen, 0))) } // calentamiento
        val tiempos = (1..40).map {
            val inicio = SystemClock.elapsedRealtimeNanos()
            Tasks.await(lector.process(InputImage.fromBitmap(imagen, 0)))
            (SystemClock.elapsedRealtimeNanos() - inicio) / 1_000_000.0
        }.sorted()
        val p95 = tiempos[(tiempos.size * 0.95).toInt().coerceAtMost(tiempos.lastIndex)]
        Log.i(ETIQUETA, "decodificación 1280x720: mediana=${"%.1f".format(tiempos[tiempos.size / 2])} ms p95=${"%.1f".format(p95)} ms max=${"%.1f".format(tiempos.last())} ms")
    }

    private fun cuadro(
        lado: Int,
        oscurecer: Float = 1f,
        ruido: Int = 0,
        reflejo: Boolean = false,
        giro: Float = 0f,
        perspectiva: Float = 0f,
        inclinacion: Float = 0f,
        mancha: Boolean = false,
    ): Bitmap {
        val fondo = Bitmap.createBitmap(1280, 720, Bitmap.Config.ARGB_8888)
        val lienzo = Canvas(fondo)
        lienzo.drawColor(Color.rgb(150, 140, 120)) // maceta / mesa de trabajo
        val escalado = Bitmap.createScaledBitmap(qr, lado, lado * qr.height / qr.width, true)
        val m = Matrix()
        if (perspectiva > 0f) {
            val w = escalado.width.toFloat()
            val h = escalado.height.toFloat()
            val d = w * perspectiva
            m.setPolyToPoly(floatArrayOf(0f, 0f, w, 0f, w, h, 0f, h), 0, floatArrayOf(d, 0f, w - d, d, w, h, 0f, h - d), 0, 4)
        }
        if (inclinacion != 0f) {
            // Etiqueta inclinada frente a la cámara: rotación real en el eje X con perspectiva.
            val camara = android.graphics.Camera()
            val proyeccion = Matrix()
            camara.save()
            // Cámara a ~5 veces el lado del QR (una etiqueta de 5 cm vista a 25 cm). Camera usa pulgadas (72 px).
            camara.setLocation(0f, 0f, -5f * escalado.width / 72f)
            camara.rotateX(inclinacion)
            camara.getMatrix(proyeccion)
            camara.restore()
            proyeccion.preTranslate(-escalado.width / 2f, -escalado.height / 2f)
            proyeccion.postTranslate(escalado.width / 2f, escalado.height / 2f)
            m.postConcat(proyeccion)
        }
        m.postRotate(giro, escalado.width / 2f, escalado.height / 2f)
        m.postTranslate((1280 - escalado.width) / 2f, (720 - escalado.height) / 2f)
        lienzo.drawBitmap(escalado, m, Paint(Paint.FILTER_BITMAP_FLAG))
        if (mancha) {
            // Barro sobre una zona de datos (no sobre los patrones de posición).
            val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(70, 50, 30) }
            lienzo.drawOval(640f - lado * 0.08f, 360f - lado * 0.12f, 640f + lado * 0.16f, 360f + lado * 0.1f, p)
        }
        if (reflejo) {
            val p = Paint().apply {
                shader = RadialGradient(640f + lado * 0.2f, 360f - lado * 0.15f, lado * 0.3f, Color.argb(210, 255, 255, 255), Color.TRANSPARENT, Shader.TileMode.CLAMP)
            }
            lienzo.drawRect(0f, 0f, 1280f, 720f, p)
        }
        if (oscurecer < 1f) {
            val filtro = Paint().apply { colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setScale(oscurecer, oscurecer, oscurecer, 1f) }) }
            val copia = fondo.copy(Bitmap.Config.ARGB_8888, false)
            lienzo.drawBitmap(copia, 0f, 0f, filtro)
            copia.recycle()
        }
        if (ruido > 0) {
            val r = Random(7)
            val px = IntArray(1280 * 720)
            fondo.getPixels(px, 0, 1280, 0, 0, 1280, 720)
            for (i in px.indices) {
                val n = r.nextInt(-ruido, ruido + 1)
                val c = px[i]
                px[i] = Color.rgb(((c shr 16 and 0xFF) + n).coerceIn(0, 255), ((c shr 8 and 0xFF) + n).coerceIn(0, 255), ((c and 0xFF) + n).coerceIn(0, 255))
            }
            fondo.setPixels(px, 0, 1280, 0, 0, 1280, 720)
        }
        escalado.recycle()
        return fondo
    }

    private companion object {
        const val ETIQUETA = "GtLecturaQr"
    }
}
