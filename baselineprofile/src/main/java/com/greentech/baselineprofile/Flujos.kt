package com.greentech.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

/**
 * Recorridos de la app que comparten el generador de perfiles y los Macrobenchmarks.
 * Se navega por texto visible (los mismos textos de Textos.kt): si cambia un texto, cambia aquí.
 */
const val PAQUETE = "com.greentech.app"

const val CORREO_EMPLEADO = "empleado@gmail.com"
const val PASSWORD_EMPLEADO = "empleado123"
const val CORREO_ADMIN = "admin@gmail.com"
const val PASSWORD_ADMIN = "admin123"

/** Lote de los datos Fake usado en la consulta por código. */
const val CODIGO_LOTE = "GT-8F2A91C0D4"

private const val ESPERA_MS = 10_000L

fun MacrobenchmarkScope.esperar(selector: BySelector, ms: Long = ESPERA_MS): UiObject2 =
    device.wait(Until.findObject(selector), ms) ?: error("No apareció en pantalla: $selector")

fun MacrobenchmarkScope.existe(selector: BySelector, ms: Long = 1_500L): Boolean =
    device.wait(Until.hasObject(selector), ms) == true

/** Concede el permiso de notificaciones para que su diálogo no tape los recorridos. */
fun MacrobenchmarkScope.concederPermisos() {
    device.executeShellCommand("pm grant $packageName android.permission.POST_NOTIFICATIONS")
    device.executeShellCommand("pm grant $packageName android.permission.CAMERA")
}

/** Espera el Login (después del splash). */
fun MacrobenchmarkScope.esperarLogin() {
    esperar(By.text("Iniciar sesión"), 15_000)
}

/** Cierra el teclado si está abierto (el botón de enviar puede quedar debajo y UiAutomator no lo ve). */
fun MacrobenchmarkScope.ocultarTeclado() {
    val ime = device.executeShellCommand("dumpsys input_method")
    if (ime.contains("mInputShown=true") || ime.contains("isInputViewShown=true")) {
        device.pressBack()
        device.waitForIdle()
    }
}

/** Escribe las credenciales y entra. Cierra la introducción de primer uso y el aviso de notificaciones si aparecen. */
fun MacrobenchmarkScope.iniciarSesion(correo: String = CORREO_EMPLEADO, password: String = PASSWORD_EMPLEADO) {
    esperarLogin()
    val campos = device.findObjects(By.clazz("android.widget.EditText"))
    campos[0].text = correo
    campos[1].text = password
    ocultarTeclado()
    esperar(By.text("Iniciar sesión")).click()
    cerrarAvisosDePrimerUso()
}

fun MacrobenchmarkScope.cerrarAvisosDePrimerUso() {
    if (existe(By.text("Omitir"), 5_000)) device.findObject(By.text("Omitir")).click()
    if (existe(By.text("Ahora no"), 1_500)) device.findObject(By.text("Ahora no")).click()
    device.waitForIdle()
}

fun MacrobenchmarkScope.esperarInicioEmpleado() {
    esperar(By.text("Escanear"))
}

fun MacrobenchmarkScope.irAPestana(nombre: String) {
    // La barra inferior es el último elemento con ese texto (los mosaicos del inicio pueden repetirlo).
    device.findObjects(By.text(nombre)).last().click()
    device.waitForIdle()
}

/** Abre la lista de lotes y espera a que carguen (los datos Fake tardan 600–900 ms). */
fun MacrobenchmarkScope.abrirListaLotes() {
    irAPestana("Lotes")
    esperar(By.textContains("GT-"))
}

fun MacrobenchmarkScope.desplazarLista() {
    val lista = device.findObject(By.scrollable(true)) ?: return
    // Margen para no iniciar el gesto sobre la barra de gestos del sistema.
    lista.setGestureMargin(device.displayWidth / 5)
    repeat(2) {
        lista.fling(Direction.DOWN)
        device.waitForIdle()
    }
    lista.fling(Direction.UP)
    device.waitForIdle()
}

fun MacrobenchmarkScope.abrirDetalleDesdeLista() {
    esperar(By.textContains("GT-")).click()
    esperar(By.text("Plantas disponibles"))
}

fun MacrobenchmarkScope.volver() {
    device.pressBack()
    device.waitForIdle()
}

/** Abre Escanear y espera a que la pantalla (cámara o modo manual) esté lista. */
fun MacrobenchmarkScope.abrirEscaner() {
    irAPestana("Escanear")
    esperar(By.text("Escribir código del lote"))
}

/** Consulta por código escrito: el mismo camino que sigue un QR leído a partir de T0. */
fun MacrobenchmarkScope.consultarPorCodigo(codigo: String = CODIGO_LOTE) {
    device.findObject(By.text("Escribir código del lote")).click()
    val campo = esperar(By.clazz("android.widget.EditText"))
    campo.text = codigo
    ocultarTeclado()
    esperar(By.text("Abrir lote")).click()
    esperar(By.text("Plantas disponibles"))
}
