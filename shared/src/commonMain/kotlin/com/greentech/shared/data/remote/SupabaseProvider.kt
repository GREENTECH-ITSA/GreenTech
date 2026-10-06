package com.greentech.shared.data.remote

/**
 * TODO(Supabase): crear aquí el cliente único de Supabase.
 *
 * Pasos cuando se conecte el backend:
 * 1. Agregar a libs.versions.toml: io.github.jan-tennert.supabase (bom, auth-kt, postgrest-kt, realtime-kt, storage-kt)
 *    y un motor de Ktor por plataforma (ktor-client-okhttp en Android, ktor-client-darwin en iOS).
 * 2. Leer SUPABASE_URL y SUPABASE_ANON_KEY desde BuildConfig / xcconfig (nunca escribirlas en el código).
 * 3. Exponer el cliente con Koin en SharedModule cuando USE_FAKE_DATA = false.
 *
 * Ejemplo esperado:
 * ```
 * val client = createSupabaseClient(url, anonKey) {
 *     install(Auth); install(Postgrest); install(Realtime); install(Storage)
 * }
 * ```
 */
object SupabaseProvider {
    const val TABLA_LOTES = "lotes"
    const val TABLA_BITACORA = "bitacora"
    const val TABLA_SALIDAS = "salidas"
    const val TABLA_USUARIOS = "usuarios"
    const val TABLA_AUDITORIA = "auditoria"
    const val TABLA_LECTURAS = "lecturas_sensor"
    const val TABLA_ANOMALIAS = "anomalias"
}
