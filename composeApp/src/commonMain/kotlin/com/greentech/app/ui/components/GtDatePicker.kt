package com.greentech.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.heightIn
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Dimens
import com.greentech.shared.util.Fechas
import kotlinx.datetime.LocalDate

private const val MS_POR_DIA = 86_400_000L

/** Selector de fecha. Muestra "Hoy" cuando corresponde y no permite fechas futuras. */
@Composable
fun GtDatePicker(
    etiqueta: String,
    fecha: LocalDate,
    onFechaChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    fechaMaxima: LocalDate = Fechas.hoy(),
) {
    var abierto by rememberSaveable { mutableStateOf(false) }
    val texto = if (fecha == Fechas.hoy()) "${Textos.Comun.HOY} · ${Fechas.numerica(fecha)}" else Fechas.numerica(fecha)
    CampoSeleccion(
        etiqueta = etiqueta,
        valor = texto,
        placeholder = Textos.Comun.ELEGIR,
        icono = Icons.Outlined.CalendarMonth,
        iconoFinal = Icons.Outlined.EditCalendar,
        error = null,
        onClick = { abierto = true },
        modifier = modifier,
    )
    if (abierto) {
        RegistrarModal()
        val maxMs = fechaMaxima.toEpochDays() * MS_POR_DIA
        val estado = rememberDatePickerState(
            initialSelectedDateMillis = fecha.toEpochDays() * MS_POR_DIA,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= maxMs
            },
        )
        DatePickerDialog(
            onDismissRequest = { abierto = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        estado.selectedDateMillis?.let { onFechaChange(LocalDate.fromEpochDays(it / MS_POR_DIA)) }
                        abierto = false
                    },
                    modifier = Modifier.heightIn(min = Dimens.tactilMin),
                ) { Text(Textos.Comun.ELEGIR_FECHA, style = MaterialTheme.typography.labelLarge) }
            },
            dismissButton = {
                TextButton(onClick = { abierto = false }, modifier = Modifier.heightIn(min = Dimens.tactilMin)) {
                    Text(Textos.Comun.CANCELAR, style = MaterialTheme.typography.labelLarge)
                }
            },
            shape = MaterialTheme.shapes.extraLarge,
            colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        ) {
            DatePicker(state = estado, showModeToggle = false)
        }
    }
}
