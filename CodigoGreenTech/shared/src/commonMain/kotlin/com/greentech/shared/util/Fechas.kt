package com.greentech.shared.util

import kotlin.time.Clock
import kotlin.time.Duration
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

/** Utilidades de fecha y formato en español (sin depender de la configuración regional). */
object Fechas {
    private val zona: TimeZone get() = TimeZone.currentSystemDefault()

    private val meses = listOf(
        "enero", "febrero", "marzo", "abril", "mayo", "junio",
        "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre",
    )
    private val dias = listOf("lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo")

    fun ahora(): LocalDateTime = Clock.System.now().toLocalDateTime(zona)

    fun hoy(): LocalDate = ahora().date

    fun hace(duracion: Duration): LocalDateTime = (Clock.System.now() - duracion).toLocalDateTime(zona)

    fun haceDias(dias: Int): LocalDate = hoy().minus(dias, DateTimeUnit.DAY)

    fun nombreMes(mes: Int): String = meses[mes - 1]

    fun mesCorto(mes: Int): String = meses[mes - 1].take(3).replaceFirstChar { it.uppercase() }

    /** "3 oct 2026" */
    fun corta(fecha: LocalDate): String = "${fecha.day} ${meses[fecha.month.number - 1].take(3)} ${fecha.year}"

    /** "viernes, 3 de octubre" */
    fun larga(fecha: LocalDate): String =
        "${dias[fecha.dayOfWeek.isoDayNumber - 1]}, ${fecha.day} de ${meses[fecha.month.number - 1]}"

    /** "10:05" */
    fun hora(fechaHora: LocalDateTime): String =
        "${fechaHora.hour.toString().padStart(2, '0')}:${fechaHora.minute.toString().padStart(2, '0')}"

    /** "3 oct 2026 · 10:05" */
    fun cortaConHora(fechaHora: LocalDateTime): String = "${corta(fechaHora.date)} · ${hora(fechaHora)}"

    /** "03/10/2026" */
    fun numerica(fecha: LocalDate): String =
        "${fecha.day.toString().padStart(2, '0')}/${fecha.month.number.toString().padStart(2, '0')}/${fecha.year}"
}
