package com.greentech.app.util

import com.greentech.app.ui.strings.Textos
import com.greentech.shared.util.Fechas
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus
import kotlinx.datetime.number

/** Fechas legibles para la interfaz: "Hoy, 10:32", "Ayer, 18:05", "12 sep 2026". */
object FechasUi {

    /** "Hoy", "Ayer" o "12 sep 2026". */
    fun dia(fecha: LocalDate): String {
        val hoy = Fechas.hoy()
        return when (fecha) {
            hoy -> Textos.Comun.HOY
            hoy.minus(1, DateTimeUnit.DAY) -> Textos.Comun.AYER
            else -> Fechas.corta(fecha)
        }
    }

    /** "Hoy, 10:32" · "Ayer, 18:05" · "12 sep 2026". */
    fun relativa(fechaHora: LocalDateTime): String {
        val dia = dia(fechaHora.date)
        return if (dia == Textos.Comun.HOY || dia == Textos.Comun.AYER) "$dia, ${Fechas.hora(fechaHora)}" else dia
    }

    /** "1 – 31 de octubre de 2026" · "6 de septiembre – 5 de octubre de 2026" · años distintos completos. */
    fun rango(desde: LocalDate, hasta: LocalDate): String {
        fun mes(f: LocalDate) = Fechas.nombreMes(f.month.number)
        return when {
            desde.year == hasta.year && desde.month == hasta.month ->
                "${desde.day} – ${hasta.day} de ${mes(hasta)} de ${hasta.year}"
            desde.year == hasta.year ->
                "${desde.day} de ${mes(desde)} – ${hasta.day} de ${mes(hasta)} de ${hasta.year}"
            else -> "${desde.day} de ${mes(desde)} de ${desde.year} – ${hasta.day} de ${mes(hasta)} de ${hasta.year}"
        }
    }
}
