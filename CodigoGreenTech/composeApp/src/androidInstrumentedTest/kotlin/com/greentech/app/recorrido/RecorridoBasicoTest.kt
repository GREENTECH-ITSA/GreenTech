package com.greentech.app.recorrido

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Verificación funcional en cualquier versión (incluido Android 8.0): la app abre, inicia sesión,
 * abre Escanear con la cámara y muestra el Detalle de un lote. El texto se escribe directo en los
 * campos (UiObject2.setText), sin teclado.
 *
 *   ./gradlew :composeApp:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.greentech.app.recorrido.RecorridoBasicoTest
 */
@RunWith(AndroidJUnit4::class)
class RecorridoBasicoTest {

    private val instrumentacion = InstrumentationRegistry.getInstrumentation()
    private val device = UiDevice.getInstance(instrumentacion)
    private val paquete = instrumentacion.targetContext.packageName

    private fun esperar(selector: BySelector, ms: Long = 20_000): UiObject2 =
        device.wait(Until.findObject(selector), ms) ?: error(
            "No apareció: $selector. Visible: " +
                device.findObjects(By.pkg(paquete)).mapNotNull { it.text ?: it.contentDescription }.distinct().take(20),
        )

    /** Enfoca el campo [indice] y le pone [texto] por accesibilidad (sin teclado); reintenta si no quedó. */
    private fun escribir(indice: Int, texto: String) {
        repeat(3) {
            device.findObjects(By.clazz("android.widget.EditText"))[indice].click()
            device.waitForIdle()
            device.findObjects(By.clazz("android.widget.EditText"))[indice].text = texto
            device.waitForIdle()
            val actual = device.findObjects(By.clazz("android.widget.EditText"))[indice].text.orEmpty()
            if (actual == texto || actual.isNotEmpty() && texto.startsWith("empleado1")) return
        }
    }

    @Test
    fun abreEntraEscaneaYMuestraDetalle() {
        device.executeShellCommand("pm grant $paquete android.permission.CAMERA")
        val abrir = instrumentacion.targetContext.packageManager.getLaunchIntentForPackage(paquete)!!
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        instrumentacion.targetContext.startActivity(abrir)

        // Login
        esperar(By.text("Iniciar sesión"), 30_000)
        escribir(0, "empleado@gmail.com")
        escribir(1, "empleado123")
        esperar(By.text("Iniciar sesión")).click()

        // Introducción de primer uso (si aparece) e Inicio
        device.wait(Until.findObject(By.text("Omitir")), 15_000)?.click()
        esperar(By.text("Registrar lote"))

        // Escanear: la cámara se liga y la pantalla ofrece la lectura y el código escrito
        device.findObjects(By.text("Escanear")).last().click()
        esperar(By.text("Apunta la cámara al código QR del lote"))

        // Detalle de un lote desde la lista
        device.findObjects(By.text("Lotes")).last().click()
        esperar(By.textContains("GT-")).click()
        esperar(By.text("Plantas disponibles"))
    }
}
