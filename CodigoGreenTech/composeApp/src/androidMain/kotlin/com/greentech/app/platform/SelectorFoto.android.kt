package com.greentech.app.platform

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * "Tomar foto": ActivityResultContracts.TakePicture con una URI del FileProvider (files/fotos/).
 * "Elegir de la galería": Photo Picker (PickVisualMedia), sin permisos de almacenamiento.
 */
@Composable
actual fun rememberSelectorFoto(alElegir: (String) -> Unit): SelectorFoto {
    val contexto = LocalContext.current
    val alElegirActual by rememberUpdatedState(alElegir)
    // Se guarda por si el sistema cierra la app mientras la cámara está abierta.
    var fotoPendiente by rememberSaveable { mutableStateOf<String?>(null) }
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        val uri = fotoPendiente
        fotoPendiente = null
        if (ok && uri != null) alElegirActual(uri)
    }
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) alElegirActual(uri.toString())
    }
    return remember(contexto) {
        object : SelectorFoto {
            override val puedeTomarFoto: Boolean =
                contexto.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

            override fun tomarFoto() {
                val uri = nuevaUriFoto(contexto)
                fotoPendiente = uri.toString()
                runCatching { camara.launch(uri) }.onFailure { fotoPendiente = null }
            }

            override fun elegirDeGaleria() {
                galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }
}

private fun nuevaUriFoto(contexto: Context): Uri {
    val carpeta = File(contexto.filesDir, "fotos").apply { mkdirs() }
    val archivo = File(carpeta, "observacion_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(contexto, "${contexto.packageName}.fileprovider", archivo)
}

@Composable
actual fun MiniaturaFoto(uri: String, descripcion: String?, modifier: Modifier) {
    val contexto = LocalContext.current
    val imagen by produceState<ImageBitmap?>(null, uri) {
        value = withContext(Dispatchers.IO) { runCatching { decodificar(contexto, Uri.parse(uri)) }.getOrNull() }
    }
    val bitmap = imagen
    if (bitmap != null) {
        Image(bitmap, descripcion, modifier, contentScale = ContentScale.Crop)
    } else {
        Box(modifier)
    }
}

/** Decodifica reducida (lado mayor ≈ [LADO_MINIATURA] px) para no cargar la foto completa en memoria. */
private fun decodificar(contexto: Context, uri: Uri): ImageBitmap? {
    val resolver = contexto.contentResolver
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val fuente = ImageDecoder.createSource(resolver, uri)
        return ImageDecoder.decodeBitmap(fuente) { decodificador, info, _ ->
            val lado = maxOf(info.size.width, info.size.height)
            decodificador.setTargetSampleSize(maxOf(1, lado / LADO_MINIATURA))
            decodificador.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }.asImageBitmap()
    }
    val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, limites) }
    val lado = maxOf(limites.outWidth, limites.outHeight)
    val opciones = BitmapFactory.Options().apply {
        inSampleSize = maxOf(1, lado / LADO_MINIATURA)
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }
    return resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opciones) }?.asImageBitmap()
}

private const val LADO_MINIATURA = 512
