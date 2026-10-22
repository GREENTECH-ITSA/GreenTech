package com.greentech.shared.data.repository

import com.greentech.shared.domain.model.Usuario
import com.greentech.shared.domain.repository.AuthRepository
import com.greentech.shared.domain.result.AppResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/*
 * TODO(Supabase): implementaciones reales de los repositorios.
 *
 * Cada clase recibirá las fuentes remotas (data/remote/source), el DAO local (data/local/dao)
 * y los mappers (data/mapper). Mientras USE_FAKE_DATA = true en SharedModule, no se usan.
 *
 * Pendientes:
 * - SupabaseAuthRepository      → Supabase Auth (signInWith(Email)), tabla usuarios para rol/activo.
 * - SupabaseLoteRepository      → tablas lotes, especies, zonas.
 * - SupabaseMonitoreoRepository → bitacora, lecturas_sensor, anomalias, recomendaciones.
 * - SupabaseSalidaRepository    → salidas (con trigger que descuenta disponibles y registra auditoría).
 * - SupabaseUsuarioRepository   → Edge Function para crear/desactivar usuarios de Auth.
 * - SupabaseReporteRepository   → vistas o RPC para indicadores del dashboard.
 */

/** Esqueleto de ejemplo: muestra la forma que tendrá cada implementación real. */
class SupabaseAuthRepository : AuthRepository {
    override val sesion: StateFlow<Usuario?> = MutableStateFlow(null)

    override suspend fun login(correo: String, password: String): AppResult<Usuario> =
        TODO("Conectar con Supabase Auth")

    override suspend fun cambiarPassword(actual: String, nueva: String): AppResult<Unit> =
        TODO("Conectar con Supabase Auth (updateUser)")

    override suspend fun logout() = TODO("Conectar con Supabase Auth (signOut)")
}
