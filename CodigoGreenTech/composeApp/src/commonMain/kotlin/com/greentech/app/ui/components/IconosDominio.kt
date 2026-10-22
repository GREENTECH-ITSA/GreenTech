package com.greentech.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.PlaylistAdd
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.Coronavirus
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Grass
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material.icons.outlined.Sanitizer
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.Yard
import androidx.compose.ui.graphics.vector.ImageVector
import com.greentech.shared.domain.model.CategoriaRecomendacion
import com.greentech.shared.domain.model.Etapa
import com.greentech.shared.domain.model.TipoActividad
import com.greentech.shared.domain.model.TipoAnomalia
import com.greentech.shared.domain.model.TipoAuditoria
import com.greentech.shared.domain.model.TipoEvento
import com.greentech.shared.domain.model.TipoSalida

/**
 * Íconos de cada módulo: los mismos en navegación, mosaicos y Ayuda. Solo outlined;
 * las variantes filled se usan únicamente para la pestaña seleccionada de la barra inferior.
 */
object IconosModulo {
    val lote: ImageVector = Icons.Outlined.Eco
    val qr: ImageVector = Icons.Outlined.QrCode2
    val escanear: ImageVector = Icons.Outlined.QrCodeScanner
    val etapa: ImageVector = Icons.Outlined.Timeline
    val observacion: ImageVector = Icons.Outlined.EditNote
    val actividad: ImageVector = Icons.Outlined.TaskAlt
    val humedad: ImageVector = Icons.Outlined.WaterDrop
    val temperatura: ImageVector = Icons.Outlined.Thermostat
    val condiciones: ImageVector = Icons.Outlined.WaterDrop
    val anomalia: ImageVector = Icons.Outlined.BugReport
    val recomendaciones: ImageVector = Icons.Outlined.Lightbulb
    val bitacora: ImageVector = Icons.Outlined.History
    val salida: ImageVector = Icons.Outlined.Sell
    val merma: ImageVector = Icons.Outlined.DeleteOutline
    val dashboard: ImageVector = Icons.Outlined.Insights
    val personal: ImageVector = Icons.Outlined.Groups
    val auditoria: ImageVector = Icons.Outlined.Shield
    val ayuda: ImageVector = Icons.AutoMirrored.Outlined.HelpOutline
    val perfil: ImageVector = Icons.Outlined.AccountCircle
    val inicio: ImageVector = Icons.Outlined.Home

    // Variantes filled: solo para el ícono seleccionado de la barra inferior.
    object Seleccionado {
        val inicio: ImageVector = Icons.Filled.Home
        val lote: ImageVector = Icons.Filled.Eco
        val escanear: ImageVector = Icons.Filled.QrCodeScanner
        val perfil: ImageVector = Icons.Filled.AccountCircle
        val dashboard: ImageVector = Icons.Filled.Insights
        val personal: ImageVector = Icons.Filled.Groups
    }
}

/** Íconos de cada concepto del dominio, en un solo lugar para que sean consistentes. */
object IconosDominio {
    fun etapa(etapa: Etapa): ImageVector = when (etapa) {
        Etapa.GERMINACION -> Icons.Outlined.Grass
        Etapa.PLANTULA -> Icons.Outlined.Spa
        Etapa.CRECIMIENTO_VEGETATIVO -> Icons.Outlined.Eco
        Etapa.FLORACION -> Icons.Outlined.LocalFlorist
        Etapa.LISTA_PARA_VENTA -> Icons.Outlined.Storefront
    }

    fun actividad(tipo: TipoActividad): ImageVector = when (tipo) {
        TipoActividad.RIEGO_MANUAL -> Icons.Outlined.WaterDrop
        TipoActividad.PODA -> Icons.Outlined.ContentCut
        TipoActividad.FERTILIZACION -> Icons.Outlined.Science
        TipoActividad.TRASPLANTE -> Icons.Outlined.Yard
        TipoActividad.REVISION -> Icons.Outlined.Checklist
        TipoActividad.CONTROL_FITOSANITARIO -> Icons.Outlined.Sanitizer
    }

    fun anomalia(tipo: TipoAnomalia): ImageVector = when (tipo) {
        TipoAnomalia.PLAGA -> Icons.Outlined.BugReport
        TipoAnomalia.ENFERMEDAD -> Icons.Outlined.Coronavirus
        TipoAnomalia.ANOMALIA -> Icons.Outlined.WarningAmber
    }

    fun evento(tipo: TipoEvento): ImageVector = when (tipo) {
        TipoEvento.REGISTRO -> Icons.AutoMirrored.Outlined.PlaylistAdd
        TipoEvento.ETAPA -> IconosModulo.etapa
        TipoEvento.OBSERVACION -> IconosModulo.observacion
        TipoEvento.ACTIVIDAD -> IconosModulo.actividad
        TipoEvento.ANOMALIA -> IconosModulo.anomalia
        TipoEvento.SALIDA -> IconosModulo.salida
    }

    fun salida(tipo: TipoSalida): ImageVector = when (tipo) {
        TipoSalida.VENTA -> IconosModulo.salida
        TipoSalida.MERMA -> IconosModulo.merma
    }

    fun auditoria(tipo: TipoAuditoria): ImageVector = when (tipo) {
        TipoAuditoria.VENTA -> IconosModulo.salida
        TipoAuditoria.BAJA_PLANTAS -> Icons.Outlined.RemoveCircleOutline
        TipoAuditoria.ALTA_USUARIO -> Icons.Outlined.PersonAddAlt
        TipoAuditoria.BAJA_USUARIO -> Icons.Outlined.PersonOff
    }

    fun recomendacion(categoria: CategoriaRecomendacion): ImageVector = when (categoria) {
        CategoriaRecomendacion.RIEGO -> Icons.Outlined.WaterDrop
        CategoriaRecomendacion.PLAGA -> Icons.Outlined.BugReport
        CategoriaRecomendacion.ENFERMEDAD -> Icons.Outlined.Coronavirus
        CategoriaRecomendacion.TEMPERATURA -> Icons.Outlined.Thermostat
        CategoriaRecomendacion.NUTRICION -> Icons.Outlined.Science
        CategoriaRecomendacion.GENERAL -> Icons.Outlined.Lightbulb
    }

    /** Tendencia a la baja (merma en gráficas). */
    val tendenciaBaja: ImageVector = Icons.AutoMirrored.Outlined.TrendingDown
    val sensores: ImageVector = IconosModulo.condiciones
}
