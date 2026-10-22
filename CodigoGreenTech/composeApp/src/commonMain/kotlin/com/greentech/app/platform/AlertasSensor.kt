package com.greentech.app.platform

/**
 * Notificación del sistema en el canal "Alertas de cultivo". No hace nada si el usuario
 * no permitió las notificaciones.
 *
 * TODO(Alertas): llamarla desde la sincronización con los sensores reales (Supabase Realtime o FCM)
 * cuando una lectura salga del rango ideal de la especie.
 */
expect fun mostrarAlertaSensor(lote: String, mensaje: String)
