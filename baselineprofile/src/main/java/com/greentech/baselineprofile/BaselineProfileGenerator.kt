package com.greentech.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Genera el Baseline Profile y el Startup Profile de GreenTech con los recorridos principales:
 * arranque → Login → Inicio → Lotes (scroll) → Detalle → Escanear (consulta por código) y el
 * Dashboard del administrador.
 *
 *   ./gradlew :composeApp:generateBaselineProfile
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val regla = BaselineProfileRule()

    @Test
    fun generar() = regla.collect(
        packageName = PAQUETE,
        // El arranque hasta el Login forma el Startup Profile.
        includeInStartupProfile = true,
    ) {
        pressHome()
        startActivityAndWait()
        concederPermisos()
        esperarLogin()

        iniciarSesion()
        esperarInicioEmpleado()
        abrirListaLotes()
        desplazarLista()
        abrirDetalleDesdeLista()
        volver()
        abrirEscaner()
        consultarPorCodigo()
        volver()

        // Panel del administrador (Dashboard con gráficas): se reinicia la app para volver al Login.
        killProcess()
        startActivityAndWait()
        iniciarSesion(CORREO_ADMIN, PASSWORD_ADMIN)
        esperar(androidx.test.uiautomator.By.text("Personal"))
        desplazarLista()
    }
}
