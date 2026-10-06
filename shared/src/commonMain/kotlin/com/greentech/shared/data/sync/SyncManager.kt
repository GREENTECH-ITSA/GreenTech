package com.greentech.shared.data.sync

/**
 * TODO(Offline): sincronización entre la base local (SQLDelight) y Supabase.
 *
 * Responsabilidades previstas:
 * - Guardar en local cada registro hecho sin internet y marcarlo como pendiente.
 * - Al recuperar la conexión, subir los pendientes en orden y resolver conflictos (gana el más reciente).
 * - Exponer un StateFlow<EstadoSync> para el banner de sincronización de la UI.
 */
class SyncManager {
    // TODO: inyectar LoteDao, fuentes remotas y un monitor de conectividad.
}
