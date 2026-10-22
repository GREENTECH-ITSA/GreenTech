package com.greentech.app.ui.screens.admin.supervision

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.preview.DatosPreview
import com.greentech.app.ui.screens.employee.lotes.ListaLotesEvent
import com.greentech.app.ui.screens.employee.lotes.ListaLotesScreen
import com.greentech.app.ui.screens.employee.lotes.ListaLotesUiState
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.ThemeMode

/** Reutiliza el diseño de la lista de lotes; el estado viene con soloLectura = true (sin botón "Nuevo lote"). */
@Composable
fun LotesScreen(estado: ListaLotesUiState, onEvent: (ListaLotesEvent) -> Unit) = ListaLotesScreen(estado, onEvent)

@Preview
@Composable
private fun LotesSupervisionPreviewClaro() {
    GreenTechTheme { LotesScreen(ListaLotesUiState(lotes = EstadoCarga.Listo(DatosPreview.lotes), soloLectura = true)) {} }
}

@Preview
@Composable
private fun LotesSupervisionPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { LotesScreen(ListaLotesUiState(lotes = EstadoCarga.Listo(DatosPreview.lotes), soloLectura = true)) {} }
}
