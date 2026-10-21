@file:OptIn(ExperimentalMetricApi::class, ExperimentalMacrobenchmarkApi::class)

package com.greentech.baselineprofile

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMacrobenchmarkApi
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.MemoryUsageMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import org.junit.BeforeClass
import org.junit.Rule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Macrobenchmarks de GreenTech (build benchmarkRelease: R8 + Baseline Profile si existe).
 * Se mide con la compilación por defecto de una instalación real (perfiles de las bibliotecas y de la
 * app si están en la APK). Como MIUI bloquea la reinstalación por USB que hace CompilationMode.Partial,
 * la compilación se prepara a mano una vez por clase ([prepararCompilacion]: ProfileInstaller instala el
 * perfil y se compila con speed-profile, igual que tras una instalación desde Play) y se mide con
 * CompilationMode.Ignore().
 *
 *   ./gradlew :baselineprofile:connectedBenchmarkReleaseAndroidTest
 *   (una sola clase: -Pandroid.testInstrumentationRunnerArguments.class=com.greentech.baselineprofile.ArranqueBenchmark)
 *
 * Los resultados (JSON con todas las iteraciones) quedan en
 * baselineprofile/build/outputs/connected_android_test_additional_output/.
 */
private val compilacion = CompilationMode.Ignore()

/**
 * Deja la app compilada como en una instalación real: se borra la compilación previa, ProfileInstaller
 * instala el Baseline Profile de la APK (si lo trae) y ART compila con speed-profile.
 */
fun prepararCompilacion() {
    val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    device.executeShellCommand("am force-stop $PAQUETE")
    device.executeShellCommand("cmd package compile --reset $PAQUETE")
    device.executeShellCommand("am start -W -n $PAQUETE/.MainActivity")
    Thread.sleep(3_000)
    device.executeShellCommand("am broadcast -a androidx.profileinstaller.action.INSTALL_PROFILE $PAQUETE/androidx.profileinstaller.ProfileInstallReceiver")
    Thread.sleep(1_000)
    device.executeShellCommand("am force-stop $PAQUETE")
    device.executeShellCommand("cmd package compile -f -m speed-profile $PAQUETE")
}

/** Proceso en el que ya se inició sesión (la sesión de los Fakes vive en memoria) y con qué correo. */
private var pidConSesion: String? = null
private var correoConSesion: String? = null

private fun MacrobenchmarkScope.pid(): String = device.executeShellCommand("pidof $PAQUETE").trim()

/**
 * Deja la app abierta, con sesión y en una pestaña principal. Si el proceso es el mismo en el que ya se
 * entró, solo regresa con "atrás" hasta ver la barra; si no, arranca limpio (force-stop borra la pila
 * guardada, así no se restaura una pantalla sin sesión) y entra.
 */
private fun MacrobenchmarkScope.asegurarSesion(correo: String = CORREO_EMPLEADO, password: String = PASSWORD_EMPLEADO) {
    concederPermisos()
    val pestana = By.text(if (correo == CORREO_ADMIN) "Personal" else "Inicio")
    if (pid().isNotEmpty() && pid() == pidConSesion && correo == correoConSesion) {
        repeat(4) {
            if (existe(pestana, 500)) return
            device.pressBack()
            device.waitForIdle()
        }
    }
    arrancarLimpio()
    iniciarSesion(correo, password)
    esperar(pestana)
    pidConSesion = pid()
    correoConSesion = correo
}

private fun MacrobenchmarkScope.arrancarLimpio() {
    device.executeShellCommand("am force-stop $PAQUETE")
    startActivityAndWait()
}

@RunWith(AndroidJUnit4::class)
@LargeTest
class ArranqueBenchmark {
    @get:Rule val regla = MacrobenchmarkRule()

    companion object {
        @JvmStatic @BeforeClass fun compilar() = prepararCompilacion()
    }

    /** Arranque en frío. timeToInitialDisplay = primer frame; timeToFullDisplay = Login dibujado (reportFullyDrawn). */
    @Test fun arranqueFrio() = arranque(StartupMode.COLD)

    @Test fun arranqueCaliente() = arranque(StartupMode.WARM)

    private fun arranque(modo: StartupMode) = regla.measureRepeated(
        packageName = PAQUETE,
        metrics = listOf(StartupTimingMetric()),
        compilationMode = compilacion,
        startupMode = modo,
        iterations = 10,
        setupBlock = {
            pressHome()
            if (modo == StartupMode.COLD) device.executeShellCommand("am force-stop $PAQUETE")
        },
    ) {
        startActivityAndWait()
        esperarLogin()
        // La traza debe incluir el frame que sigue a reportFullyDrawn (Login dibujado).
        Thread.sleep(1_000)
    }
}

@RunWith(AndroidJUnit4::class)
@LargeTest
class FlujosBenchmark {
    @get:Rule val regla = MacrobenchmarkRule()

    companion object {
        @JvmStatic @BeforeClass fun compilar() = prepararCompilacion()
    }

    @Test
    fun loginAInicio() = regla.measureRepeated(
        packageName = PAQUETE,
        metrics = listOf(FrameTimingMetric(), TraceSectionMetric("login_a_inicio")),
        compilationMode = compilacion,
        startupMode = StartupMode.COLD,
        iterations = 8,
        setupBlock = {
            pressHome()
            arrancarLimpio()
            concederPermisos()
            esperarLogin()
            val campos = device.findObjects(By.clazz("android.widget.EditText"))
            campos[0].text = CORREO_EMPLEADO
            campos[1].text = PASSWORD_EMPLEADO
            ocultarTeclado()
        },
    ) {
        esperar(By.text("Iniciar sesión")).click()
        cerrarAvisosDePrimerUso()
        esperarInicioEmpleado()
    }

    @Test
    fun scrollListaLotes() = regla.measureRepeated(
        packageName = PAQUETE,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = compilacion,
        iterations = 8,
        setupBlock = {
            asegurarSesion()
            if (!existe(By.desc("Buscar por especie o código"), 300)) abrirListaLotes()
        },
    ) {
        desplazarLista()
    }

    @Test
    fun detalleLoteYVolver() = regla.measureRepeated(
        packageName = PAQUETE,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = compilacion,
        iterations = 8,
        setupBlock = {
            asegurarSesion()
            if (!existe(By.desc("Buscar por especie o código"), 300)) abrirListaLotes()
        },
    ) {
        abrirDetalleDesdeLista()
        volver()
        esperar(By.textContains("GT-"))
    }

    @Test
    fun inicioEnReposo() = regla.measureRepeated(
        packageName = PAQUETE,
        metrics = listOf(MemoryUsageMetric(MemoryUsageMetric.Mode.Last)),
        compilationMode = compilacion,
        iterations = 5,
        setupBlock = { asegurarSesion() },
    ) {
        irAPestana("Inicio")
        esperarInicioEmpleado()
        Thread.sleep(3_000)
    }

    @Test
    fun dashboardAdministrador() = regla.measureRepeated(
        packageName = PAQUETE,
        metrics = listOf(FrameTimingMetric(), MemoryUsageMetric(MemoryUsageMetric.Mode.Last)),
        compilationMode = compilacion,
        startupMode = StartupMode.COLD,
        iterations = 6,
        setupBlock = {
            pressHome()
            arrancarLimpio()
            concederPermisos()
            iniciarSesion(CORREO_ADMIN, PASSWORD_ADMIN)
        },
    ) {
        esperar(By.text("Personal"))
        // Espera a que carguen las gráficas (sale el esqueleto) y recorre el bento.
        Thread.sleep(1_500)
        desplazarLista()
    }
}

@RunWith(AndroidJUnit4::class)
@LargeTest
class ConsultaQrBenchmark {
    @get:Rule val regla = MacrobenchmarkRule()

    companion object {
        @JvmStatic @BeforeClass fun compilar() = prepararCompilacion()
    }

    /** Inicio de la cámara: abrir Escanear → primer cuadro analizado (qr_camara_lista). */
    @Test
    fun abrirEscaner() = regla.measureRepeated(
        packageName = PAQUETE,
        metrics = listOf(FrameTimingMetric(), TraceSectionMetric("qr_camara_lista")),
        compilationMode = compilacion,
        iterations = 10,
        setupBlock = {
            asegurarSesion()
            if (existe(By.text("Escribir código del lote"), 300)) irAPestana("Inicio")
            esperarInicioEmpleado()
        },
    ) {
        abrirEscaner()
        // Deja tiempo a que la cámara entregue el primer cuadro.
        Thread.sleep(1_500)
    }

    /**
     * RNF 3.3.1: consulta por código (40 repeticiones). Mide T0 → T1 (qr_busqueda: el repositorio
     * devuelve el lote) y T0 → T2 (qr_consulta: primer frame del Detalle con datos).
     */
    @Test
    fun consultaPorCodigo() = regla.measureRepeated(
        packageName = PAQUETE,
        metrics = listOf(
            TraceSectionMetric("qr_consulta", TraceSectionMetric.Mode.First),
            TraceSectionMetric("qr_busqueda", TraceSectionMetric.Mode.First),
            TraceSectionMetric("qr_abrir_a_detalle", TraceSectionMetric.Mode.First),
        ),
        compilationMode = compilacion,
        iterations = 40,
        setupBlock = {
            asegurarSesion()
            if (existe(By.text("Plantas disponibles"), 300)) volver()
            if (!existe(By.text("Escribir código del lote"), 300)) {
                irAPestana("Inicio")
                esperarInicioEmpleado()
            }
        },
    ) {
        if (!existe(By.text("Escribir código del lote"), 300)) abrirEscaner()
        consultarPorCodigo()
    }
}
