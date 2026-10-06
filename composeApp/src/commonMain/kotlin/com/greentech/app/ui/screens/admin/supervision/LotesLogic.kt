package com.greentech.app.ui.screens.admin.supervision

import com.greentech.app.ui.screens.employee.lotes.ListaLotesLogic
import com.greentech.shared.domain.usecase.ObtenerLotesUseCase

/**
 * Supervisión de lotes del administrador: misma lista y filtros que el empleado,
 * pero en solo lectura (abre Detalle y Bitácora sin botones de registro).
 */
class LotesLogic(obtenerLotes: ObtenerLotesUseCase) : ListaLotesLogic(obtenerLotes, soloLectura = true)
