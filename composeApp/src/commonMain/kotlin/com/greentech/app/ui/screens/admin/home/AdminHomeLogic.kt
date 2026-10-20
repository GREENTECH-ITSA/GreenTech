package com.greentech.app.ui.screens.admin.home

import androidx.compose.runtime.Immutable
import com.greentech.app.navigation.AppDestination
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.UiEffect
import com.greentech.shared.domain.usecase.ObtenerSesionUseCase
import com.greentech.shared.util.Fechas

@Immutable
data class AdminHomeUiState(
    val nombre: String = "",
    val fecha: String = "",
)

sealed interface AdminHomeEvent {
    data object Auditoria : AdminHomeEvent
    data object AgregarTrabajador : AdminHomeEvent
    data object SupervisarLotes : AdminHomeEvent
    data object Ayuda : AdminHomeEvent
}

/** Encabezado y accesos rápidos del panel del administrador (pestaña Dashboard). */
class AdminHomeLogic(sesion: ObtenerSesionUseCase) : BaseLogic<AdminHomeUiState, AdminHomeEvent>(
    AdminHomeUiState(nombre = sesion.actual()?.primerNombre.orEmpty(), fecha = Fechas.larga(Fechas.hoy())),
) {
    override fun onEvent(evento: AdminHomeEvent) {
        emitir(
            UiEffect.Navegar(
                when (evento) {
                    AdminHomeEvent.Auditoria -> AppDestination.Auditoria
                    AdminHomeEvent.AgregarTrabajador -> AppDestination.AltaTrabajador
                    AdminHomeEvent.SupervisarLotes -> AppDestination.SupervisionLotes
                    AdminHomeEvent.Ayuda -> AppDestination.Ayuda
                },
            ),
        )
    }
}
