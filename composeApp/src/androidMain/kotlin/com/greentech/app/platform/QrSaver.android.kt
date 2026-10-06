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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.FileProvider
import com.greentech.app.resources.Res
import com.greentech.app.resources.logo_icon
import com.greentech.app.ui.theme.GreenTechLightExtended
import io.github.alexzhirkevich.qrose.QrCodePainter
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrLogoPadding
import io.github.alexzhirkevich.qrose.options.QrLogoShape
import io.github.alexzhirkevich.qrose.options.QrOptions
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.toImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getDrawableResourceBytes
import org.jetbrains.compose.resources.getSystemResourceEnvironment
import org.koin.mp.KoinPlatform
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Imagen del QR del lote: PNG de 1024 px de ancho, fondo blanco, el mismo estilo que la tarjeta
 * de la pantalla (esquinas redondeadas, acento verde y logo) y el código del lote debajo.
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

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun renderizarPng(codigo: String): ByteArray {
        val colores = GreenTechLightExtended
        val logoBytes = getDrawableResourceBytes(getSystemResourceEnvironment(), Res.drawable.logo_icon)
        val logo = BitmapPainter(BitmapFactory.decodeByteArray(logoBytes, 0, logoBytes.size).asImageBitmap())
        val opciones = QrOptions {
            shapes {
                darkPixel = QrPixelShape.roundCorners()
                ball = QrBallShape.roundCorners(.3f)
                frame = QrFrameShape.roundCorners(.3f)
            }
            colors {
                dark = QrBrush.solid(colores.qrTinta)
                ball = QrBrush.solid(colores.qrAcento)
                frame = QrBrush.solid(colores.qrAcento)
            }
            logo {
                painter = logo
                size = .22f
                padding = QrLogoPadding.Natural(.1f)
                shape = QrLogoShape.circle()
            }
        }
        val qr = QrCodePainter(codigo, opciones).toImageBitmap(LADO_QR, LADO_QR).asAndroidBitmap()

        val imagen = Bitmap.createBitmap(ANCHO, ALTO, Bitmap.Config.ARGB_8888)
        val lienzo = android.graphics.Canvas(imagen)
        lienzo.drawColor(android.graphics.Color.WHITE)
        val margen = (ANCHO - LADO_QR) / 2f
        lienzo.drawBitmap(qr, margen, margen, null)
        val texto = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = colores.qrTinta.toArgb()
            textSize = 84f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            fontFeatureSettings = "tnum"
        }
        val baseTexto = margen + LADO_QR + (ALTO - margen - LADO_QR) / 2f - (texto.descent() + texto.ascent()) / 2f
        lienzo.drawText(codigo, ANCHO / 2f, baseTexto, texto)

        return ByteArrayOutputStream().use { salida ->
            imagen.compress(Bitmap.CompressFormat.PNG, 100, salida)
            imagen.recycle()
            salida.toByteArray()
        }
    }

    private companion object {
        const val ANCHO = 1024
        const val LADO_QR = 880
        const val ALTO = 1200
        const val CARPETA_GALERIA = "GreenTech"
        const val CARPETA_CACHE = "qr"
    }
}
