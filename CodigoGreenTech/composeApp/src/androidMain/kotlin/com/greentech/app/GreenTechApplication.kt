package com.greentech.app

import android.app.Application
import com.greentech.app.di.iniciarKoin
import com.greentech.app.diagnostico.Diagnostico
import com.greentech.app.notifications.AlertasCultivo
import org.koin.android.ext.koin.androidContext
import kotlin.concurrent.thread

class GreenTechApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Diagnostico.activarStrictMode()
        iniciarKoin { androidContext(this@GreenTechApplication) }
        // Empieza a leer del disco las preferencias (tema, "Reducir animaciones"): SharedPreferences carga el
        // archivo en su propio hilo, así que cuando App() las pida en el primer frame ya están en memoria.
        getSharedPreferences("${packageName}_preferences", MODE_PRIVATE)
        // El canal de notificaciones no hace falta para el primer frame: se crea fuera del hilo principal.
        thread(name = "gt-arranque", priority = Thread.MIN_PRIORITY) { AlertasCultivo.crearCanal(this) }
    }
}
