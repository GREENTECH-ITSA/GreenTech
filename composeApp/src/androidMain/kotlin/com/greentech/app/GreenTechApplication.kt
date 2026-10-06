package com.greentech.app

import android.app.Application
import com.greentech.app.di.iniciarKoin
import com.greentech.app.notifications.AlertasCultivo
import org.koin.android.ext.koin.androidContext

class GreenTechApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        iniciarKoin { androidContext(this@GreenTechApplication) }
        AlertasCultivo.crearCanal(this)
    }
}
