package com.greentech.app.platform

import android.content.Context
import com.greentech.app.notifications.AlertasCultivo
import org.koin.mp.KoinPlatform

actual fun mostrarAlertaSensor(lote: String, mensaje: String) {
    AlertasCultivo.mostrar(KoinPlatform.getKoin().get<Context>(), lote, mensaje)
}
