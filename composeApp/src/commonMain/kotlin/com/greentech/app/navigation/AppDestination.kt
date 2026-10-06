package com.greentech.app.navigation

import kotlinx.serialization.Serializable

/** Rutas type-safe de la app. Cada panel (empleado/administrador) tiene su propio grafo. */
sealed interface AppDestination {
    // ---------- Raíz ----------
    @Serializable data object Splash : AppDestination
    @Serializable data object Login : AppDestination
    @Serializable data object PanelEmpleado : AppDestination
    @Serializable data object PanelAdministrador : AppDestination

    // ---------- Comunes ----------
    @Serializable data object Perfil : AppDestination
    @Serializable data object CambiarPassword : AppDestination
    @Serializable data object Ayuda : AppDestination

    // ---------- Empleado ----------
    @Serializable data object InicioEmpleado : AppDestination
    @Serializable data object ListaLotes : AppDestination
    @Serializable data object RegistrarLote : AppDestination
    /** [recienCreado]: se abre justo después de registrar el lote y ofrece los siguientes pasos. */
    @Serializable data class QrLote(val loteId: String, val recienCreado: Boolean = false) : AppDestination
    @Serializable data object EscanerQr : AppDestination
    @Serializable data class DetalleLote(val loteId: String, val soloLectura: Boolean = false) : AppDestination
    @Serializable data class CambiarEtapa(val loteId: String) : AppDestination
    @Serializable data class NuevaObservacion(val loteId: String) : AppDestination
    @Serializable data class RegistrarActividad(val loteId: String) : AppDestination
    @Serializable data class Condiciones(val loteId: String) : AppDestination
    @Serializable data class ReportarAnomalia(val loteId: String) : AppDestination
    @Serializable data class Recomendaciones(val loteId: String) : AppDestination
    @Serializable data class Bitacora(val loteId: String, val soloLectura: Boolean = false) : AppDestination
    @Serializable data class RegistrarSalida(val loteId: String) : AppDestination

    // ---------- Administrador ----------
    @Serializable data object Dashboard : AppDestination
    @Serializable data object Trabajadores : AppDestination
    @Serializable data object AltaTrabajador : AppDestination
    @Serializable data object SupervisionLotes : AppDestination
    @Serializable data object Auditoria : AppDestination
}
