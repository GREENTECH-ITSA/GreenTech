package com.greentech.app.platform

import android.content.ClipData
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Typeface
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import com.greentech.app.ui.components.QrEstandar
import io.github.alexzhirkevich.qrose.QrCodePainter
import io.github.alexzhirkevich.qrose.toImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.mp.KoinPlatform
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Imagen del QR del lote para imprimir: PNG ≥ 1024 px, QR estándar negro sobre blanco con zona de
 * silencio (ver [QrEstandar]) y el código del lote debajo.
 *
 * - Guardar: MediaStore en Pictures/GreenTech (Android 10+, sin permiso). En Android 8–9 se escribe
 *   en la carpeta pública de imágenes; la pantalla pide WRITE_EXTERNAL_STORAGE antes de llamar.
 * - Compartir: PNG en caché + Intent.ACTION_SEND con una URI del FileProvider.
 */
actual class QrSaver actual constructor() {

    private val contexto: Context by lazy { KoinPlatform.getKoin().get<Context>() }

    actual suspend fun guardarImagen(codigo: String): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val png = renderizarPng(codigo)
            val nombre = nombreArchivo(codigo)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) guardarEnMediaStore(nombre, png) else guardarEnCarpetaPublica(nombre, png)
        }.getOrDefault(false)
    }

    actual suspend fun compartir(codigo: String): Boolean = runCatching {
        val uri = withContext(Dispatchers.IO) {
            val carpeta = File(contexto.cacheDir, CARPETA_CACHE).apply { mkdirs() }
            val archivo = File(carpeta, nombreArchivo(codigo)).apply { writeBytes(renderizarPng(codigo)) }
            FileProvider.getUriForFile(contexto, "${contexto.packageName}.fileprovider", archivo)
        }
        val envio = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Código del lote: $codigo")
            clipData = ClipData.newRawUri(codigo, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val selector = Intent.createChooser(envio, "Compartir QR").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        contexto.startActivity(selector)
        true
    }.getOrDefault(false)

    private fun nombreArchivo(codigo: String) = "QR-${codigo.replace(Regex("[^A-Za-z0-9-]"), "")}.png"

    private fun guardarEnMediaStore(nombre: String, png: ByteArray): Boolean {
        val resolver = contexto.contentResolver
        val valores = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, nombre)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$CARPETA_GALERIA")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val coleccion = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = resolver.insert(coleccion, valores) ?: return false
        return try {
            resolver.openOutputStream(uri)?.use { it.write(png) } ?: error("Sin flujo de salida")
            valores.clear()
            valores.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, valores, null, null)
            true
        } catch (e: Exception) {
            resolver.delete(uri, null, null)
            false
        }
    }

    /** Android 8–9 (requiere WRITE_EXTERNAL_STORAGE). */
    @Suppress("DEPRECATION")
    private fun guardarEnCarpetaPublica(nombre: String, png: ByteArray): Boolean {
        val carpeta = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), CARPETA_GALERIA)
        if (!carpeta.exists() && !carpeta.mkdirs()) return false
        val archivo = File(carpeta, nombre)
        archivo.writeBytes(png)
        MediaScannerConnection.scanFile(contexto, arrayOf(archivo.absolutePath), arrayOf("image/png"), null)
        return true
    }

    /**
     * PNG para imprimir (sin compresión con pérdida): QR estándar de [QrEstandar] con módulos de
     * [PX_MODULO] px enteros (sin bordes borrosos), zona de silencio de 4 módulos y el código debajo por si
     * el QR se daña. Un código de 21 módulos queda en 1160 px de ancho: ≈ 370 dpi impreso a 8 cm.
     * El bitmap se libera en cuanto se comprime.
     */
    private fun renderizarPng(codigo: String): ByteArray {
        val painter = QrCodePainter(codigo, QrEstandar.opciones)
        val modulos = QrEstandar.modulos(painter)
        val ladoQr = modulos * PX_MODULO
        val silencio = QrEstandar.MODULOS_SILENCIO * PX_MODULO
        val ancho = ladoQr + 2 * silencio
        val alto = ancho + ALTO_TEXTO
        val qr = painter.toImageBitmap(ladoQr, ladoQr).asAndroidBitmap()

        val imagen = Bitmap.createBitmap(ancho, alto, Bitmap.Config.ARGB_8888)
        try {
            val lienzo = android.graphics.Canvas(imagen)
            lienzo.drawColor(android.graphics.Color.WHITE)
            lienzo.drawBitmap(qr, silencio.toFloat(), silencio.toFloat(), null)
            val texto = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.BLACK
                textSize = 84f
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                fontFeatureSettings = "tnum"
            }
            val baseTexto = ancho - silencio / 2f + ALTO_TEXTO / 2f - (texto.descent() + texto.ascent()) / 2f
            lienzo.drawText(codigo, ancho / 2f, baseTexto, texto)
            return ByteArrayOutputStream().use { salida ->
                imagen.compress(Bitmap.CompressFormat.PNG, 100, salida)
                salida.toByteArray()
            }
        } finally {
            imagen.recycle()
            qr.recycle()
        }
    }

    /** Solo para pruebas instrumentadas: el mismo bitmap que se exporta. */
    internal fun renderizarParaPrueba(codigo: String): Bitmap = renderizarPng(codigo).let { BitmapFactory.decodeByteArray(it, 0, it.size) }

    private companion object {
        /** 40 px por módulo: ≥ 1024 px de ancho para un QR de versión 1. */
        const val PX_MODULO = 40
        const val ALTO_TEXTO = 120
        const val CARPETA_GALERIA = "GreenTech"
        const val CARPETA_CACHE = "qr"
    }
}
