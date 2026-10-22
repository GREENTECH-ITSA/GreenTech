package com.greentech.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.greentech.app.MainActivity
import com.greentech.app.R
import com.greentech.app.permissions.notificationsGranted

/** Canal "Alertas de cultivo": humedad o temperatura de un lote fuera de rango. */
object AlertasCultivo {
    const val CANAL_ID = "alertas_cultivo"
    private const val CANAL_NOMBRE = "Alertas de cultivo"
    private const val CANAL_DESCRIPCION = "Avisos cuando la humedad o la temperatura de un lote salen de rango."

    /** Se llama una vez al iniciar la app (GreenTechApplication). Crear el canal de nuevo no tiene efecto. */
    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val canal = NotificationChannel(CANAL_ID, CANAL_NOMBRE, NotificationManager.IMPORTANCE_HIGH).apply {
            description = CANAL_DESCRIPCION
            enableVibration(true)
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    fun mostrar(context: Context, lote: String, mensaje: String) {
        if (!notificationsGranted(context)) return
        val abrirApp = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(lote)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setContentIntent(abrirApp)
            .setAutoCancel(true)
            .build()
        try {
            // Un id por lote: una alerta nueva del mismo lote reemplaza a la anterior.
            NotificationManagerCompat.from(context).notify(lote.hashCode(), notificacion)
        } catch (_: SecurityException) {
            // El permiso se retiró justo ahora: no se muestra.
        }
    }
}
