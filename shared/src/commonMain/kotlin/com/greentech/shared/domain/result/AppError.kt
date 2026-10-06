package com.greentech.shared.domain.result

/**
 * Errores tipados de la app. Cada uno trae el mensaje que verá el usuario,
 * así los textos de error están centralizados en un solo lugar.
 */
sealed class AppError(val mensaje: String) {
    // Sesión
    data object CredencialesInvalidas : AppError("Correo o contraseña incorrectos")
    data object CuentaDesactivada : AppError("Tu cuenta está desactivada. Contacta al administrador")
    data object SinConexion : AppError("Sin conexión a internet")
    data object SesionExpirada : AppError("Tu sesión terminó. Inicia sesión de nuevo")

    // Contraseña
    data object PasswordActualIncorrecta : AppError("La contraseña actual es incorrecta")
    data object PasswordNoCumple : AppError("La contraseña no cumple con los requisitos")
    data object PasswordsNoCoinciden : AppError("Las contraseñas no coinciden")

    // Personal
    data object TrabajadorDuplicado : AppError("Este trabajador ya está registrado. Usa otro correo o búscalo en Personal.")
    data object UsuarioInactivo : AppError("Este usuario se encuentra inactivo")
    data object BajaPropia : AppError("No puedes darte de baja a ti mismo")

    // Lotes
    data object LoteNoEncontrado : AppError("No encontramos ese lote. Revisa el código e inténtalo de nuevo.")
    data object CamposObligatorios : AppError("Completa los campos obligatorios")

    // Salidas
    data class CantidadExcedida(val disponibles: Int) :
        AppError("La cantidad supera las plantas disponibles ($disponibles). Ingresa $disponibles o menos.")

    // Consultas
    data object SinInformacion : AppError("No hay información para la consulta")

    // General
    data object Desconocido : AppError("Ocurrió un error inesperado. Intenta de nuevo")
}
