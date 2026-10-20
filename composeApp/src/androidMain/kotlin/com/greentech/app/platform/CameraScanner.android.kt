package com.greentech.app.platform

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.SystemClock
import android.util.Size
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.common.sdkinternal.MlKitContext
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.mp.KoinPlatform
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Escáner de QR con CameraX (Preview + ImageAnalysis) y ML Kit Barcode Scanning (modelo empaquetado).
 *
 * - Solo QR (FORMAT_QR_CODE) y STRATEGY_KEEP_ONLY_LATEST: se analiza siempre el cuadro más reciente.
 * - Entre una lectura y la siguiente pasan al menos [INTERVALO_LECTURAS_MS] (evita lecturas dobles).
 * - La cámara se liga al ciclo de vida de la pantalla: se detiene en segundo plano y se libera al salir;
 *   la linterna se apaga en ambos casos.
 */
actual class CameraScanner actual constructor() {

    private val contexto: Context by lazy { KoinPlatform.getKoin().get<Context>() }

    actual val disponible: Boolean
        get() = contexto.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

    private val _linternaDisponible = MutableStateFlow(false)
    actual val linternaDisponible: StateFlow<Boolean> = _linternaDisponible.asStateFlow()

    private val _linternaEncendida = MutableStateFlow(false)
    actual val linternaEncendida: StateFlow<Boolean> = _linternaEncendida.asStateFlow()

    private val _zoom = MutableStateFlow(1f)
    actual val zoom: StateFlow<Float> = _zoom.asStateFlow()

    private val lector by lazy {
        // El MlKitInitProvider está quitado del manifest: ML Kit se inicializa aquí, al abrir Escanear.
        MlKitContext.initializeIfNeeded(contexto)
        BarcodeScanning.getClient(BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build())
    }
    private val hiloAnalisis: ExecutorService by lazy { Executors.newSingleThreadExecutor() }

    @Volatile
    private var onCodigo: ((String) -> Unit)? = null
    private var ultimaLectura = 0L

    private var proveedor: ProcessCameraProvider? = null
    private var camara: Camera? = null
    private var casosDeUso: Array<androidx.camera.core.UseCase> = emptyArray()
    private var ciclo: LifecycleOwner? = null
    private var observadorCiclo: LifecycleEventObserver? = null

    /** El siguiente cuadro analizado es el primero desde que se ligó la cámara (marca qr_camara_lista). */
    @Volatile
    private var primerCuadro = false

    /** Sube con cada ligar/liberar: descarta un ligado que termine después de salir de la pantalla. */
    private var generacion = 0

    actual fun iniciar(onCodigo: (String) -> Unit) {
        this.onCodigo = onCodigo
    }

    /** Deja de entregar códigos. La cámara la libera [VistaCamara] al salir de la pantalla. */
    actual fun detener() {
        onCodigo = null
    }

    actual fun alternarLinterna(encendida: Boolean) {
        val camara = camara ?: return
        if (camara.cameraInfo.hasFlashUnit()) camara.cameraControl.enableTorch(encendida)
    }

    actual fun cambiarZoom(factor: Float) {
        val camara = camara ?: return
        val estado = camara.cameraInfo.zoomState.value
        val limite = factor.coerceIn(estado?.minZoomRatio ?: 1f, estado?.maxZoomRatio ?: 1f)
        camara.cameraControl.setZoomRatio(limite)
    }

    internal fun ligar(contexto: Context, ciclo: LifecycleOwner, vista: PreviewView) {
        val miGeneracion = ++generacion
        primerCuadro = true
        Medicion.iniciar(Medicion.QR_CAMARA_LISTA)
        val futuro = ProcessCameraProvider.getInstance(contexto)
        futuro.addListener({
            if (miGeneracion != generacion) return@addListener
            val proveedor = runCatching { futuro.get() }.getOrNull() ?: return@addListener
            ligarCasosDeUso(proveedor, ciclo, vista)
        }, ContextCompat.getMainExecutor(contexto))
    }

    private fun ligarCasosDeUso(proveedor: ProcessCameraProvider, ciclo: LifecycleOwner, vista: PreviewView) {
        val vistaPrevia = Preview.Builder().build().also { it.setSurfaceProvider(vista.surfaceProvider) }
        val analisis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            // Resolución por rango: 1280×720 como objetivo (suficiente para un QR de 3 cm a 20–30 cm sin
            // saturar el procesador) y nunca menos de 640×480, así funciona en cámaras modestas de 5 MP.
            .setResolutionSelector(
                ResolutionSelector.Builder()
                    .setResolutionStrategy(
                        ResolutionStrategy(Size(1280, 720), ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER),
                    )
                    .setResolutionFilter { tamanos, _ ->
                        tamanos.filter { it.width * it.height >= MINIMO_ANALISIS }.ifEmpty { tamanos }
                    }
                    .build(),
            )
            .build()
            .also { it.setAnalyzer(hiloAnalisis, ::analizar) }
        val selector = when {
            proveedor.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) -> CameraSelector.DEFAULT_BACK_CAMERA
            proveedor.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) -> CameraSelector.DEFAULT_FRONT_CAMERA
            else -> return
        }
        val camara = runCatching {
            proveedor.unbindAll()
            proveedor.bindToLifecycle(ciclo, selector, vistaPrevia, analisis)
        }.getOrNull() ?: return

        this.proveedor = proveedor
        this.camara = camara
        this.casosDeUso = arrayOf(vistaPrevia, analisis)
        this.ciclo = ciclo
        _linternaDisponible.value = camara.cameraInfo.hasFlashUnit()
        camara.cameraInfo.torchState.observe(ciclo) { _linternaEncendida.value = it == TorchState.ON }
        camara.cameraInfo.zoomState.observe(ciclo) { _zoom.value = it.zoomRatio }
        // En segundo plano CameraX cierra la cámara; además se apaga la linterna de forma explícita.
        observadorCiclo = LifecycleEventObserver { _, evento ->
            if (evento == Lifecycle.Event.ON_STOP) {
                camara.cameraControl.enableTorch(false)
                _linternaEncendida.value = false
            }
        }.also { ciclo.lifecycle.addObserver(it) }
        habilitarPellizco(vista)
    }

    /** Libera la cámara (al salir de la pantalla). */
    internal fun liberar() {
        generacion++
        camara?.let { c ->
            if (c.cameraInfo.hasFlashUnit()) c.cameraControl.enableTorch(false)
            ciclo?.let { dueno ->
                c.cameraInfo.torchState.removeObservers(dueno)
                c.cameraInfo.zoomState.removeObservers(dueno)
            }
        }
        observadorCiclo?.let { obs -> ciclo?.lifecycle?.removeObserver(obs) }
        if (casosDeUso.isNotEmpty()) runCatching { proveedor?.unbind(*casosDeUso) }
        camara = null
        casosDeUso = emptyArray()
        ciclo = null
        observadorCiclo = null
        _linternaEncendida.value = false
        _linternaDisponible.value = false
        _zoom.value = 1f
    }

    /**
     * Pellizco para zoom y toque para enfocar. El autoenfoque continuo ya viene activo en la vista previa
     * de CameraX; el toque fija enfoque y exposición en ese punto (útil con códigos pequeños o reflejos).
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun habilitarPellizco(vista: PreviewView) {
        val detector = ScaleGestureDetector(vista.context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val actual = camara?.cameraInfo?.zoomState?.value?.zoomRatio ?: return false
                cambiarZoom(actual * detector.scaleFactor)
                return true
            }
        })
        vista.setOnTouchListener { v, evento ->
            detector.onTouchEvent(evento)
            if (evento.action == MotionEvent.ACTION_UP) {
                if (!detector.isInProgress && evento.eventTime - evento.downTime < TOQUE_MAX_MS) enfocarEn(vista, evento.x, evento.y)
                v.performClick()
            }
            true
        }
    }

    private fun enfocarEn(vista: PreviewView, x: Float, y: Float) {
        val camara = camara ?: return
        val punto = vista.meteringPointFactory.createPoint(x, y)
        val accion = FocusMeteringAction.Builder(punto, FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE)
            .setAutoCancelDuration(ENFOQUE_MANUAL_S, TimeUnit.SECONDS)
            .build()
        // Si la cámara no admite enfoque por punto, sigue con el autoenfoque continuo.
        runCatching { camara.cameraControl.startFocusAndMetering(accion) }
    }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    private fun analizar(imagen: ImageProxy) {
        if (primerCuadro) {
            primerCuadro = false
            Medicion.terminar(Medicion.QR_CAMARA_LISTA)
        }
        val cuadro = imagen.image
        if (cuadro == null || onCodigo == null) {
            imagen.close()
            return
        }
        val entrada = InputImage.fromMediaImage(cuadro, imagen.imageInfo.rotationDegrees)
        lector.process(entrada)
            .addOnSuccessListener { codigos -> codigos.firstNotNullOfOrNull { it.rawValue?.takeIf(String::isNotBlank) }?.let(::entregar) }
            .addOnCompleteListener { imagen.close() }
    }

    /** Hilo principal (listeners de ML Kit). */
    private fun entregar(codigo: String) {
        val ahora = SystemClock.elapsedRealtime()
        if (ahora - ultimaLectura < INTERVALO_LECTURAS_MS) return
        ultimaLectura = ahora
        onCodigo?.invoke(codigo)
    }

    private companion object {
        const val INTERVALO_LECTURAS_MS = 1_500L
        const val MINIMO_ANALISIS = 640 * 480
        const val TOQUE_MAX_MS = 250L
        const val ENFOQUE_MANUAL_S = 3L
    }
}

@Composable
actual fun VistaCamara(scanner: CameraScanner, modifier: Modifier) {
    val contexto = LocalContext.current
    val ciclo = LocalLifecycleOwner.current
    // TextureView (COMPATIBLE): respeta las esquinas redondeadas y el vidrio que va encima.
    val vista = remember(contexto) {
        PreviewView(contexto).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }
    DisposableEffect(ciclo, vista) {
        scanner.ligar(contexto, ciclo, vista)
        onDispose { scanner.liberar() }
    }
    AndroidView(factory = { vista }, modifier = modifier)
}
