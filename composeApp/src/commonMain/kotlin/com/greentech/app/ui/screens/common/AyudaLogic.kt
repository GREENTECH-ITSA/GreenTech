package com.greentech.app.ui.screens.common

import com.greentech.app.navigation.AppDestination
import com.greentech.app.ui.core.BaseLogic
import com.greentech.app.ui.core.UiEffect
import com.greentech.shared.domain.model.Rol
import com.greentech.shared.domain.usecase.ObtenerSesionUseCase

data class AyudaUiState(
    val contenido: ContenidoAyuda = AyudaContent.para(Rol.EMPLEADO),
    val busqueda: String = "",
    val expandidos: Set<String> = emptySet(),
) {
    private fun coincide(vararg textos: String) =
        busqueda.isBlank() || textos.any { it.contains(busqueda.trim(), ignoreCase = true) }

    val modulos: List<ModuloAyuda>
        get() = contenido.modulos.filter { m -> coincide(m.titulo, m.queHace, m.cuando, *m.pasos.toTypedArray()) }

    val preguntas: List<PreguntaFrecuente>
        get() = contenido.preguntas.filter { coincide(it.pregunta, it.respuesta) }

    val mostrarPrimerosPasos: Boolean get() = busqueda.isBlank()

    val sinResultados: Boolean get() = modulos.isEmpty() && preguntas.isEmpty()
}

sealed interface AyudaEvent {
    data class Buscar(val texto: String) : AyudaEvent
    data class Alternar(val id: String) : AyudaEvent
    data class IrAModulo(val destino: AppDestination) : AyudaEvent
    data object Volver : AyudaEvent
}

/** El contenido depende del rol de la sesión: el empleado y el administrador ven guías distintas. */
class AyudaLogic(sesion: ObtenerSesionUseCase) : BaseLogic<AyudaUiState, AyudaEvent>(
    AyudaUiState(contenido = AyudaContent.para(sesion.actual()?.rol ?: Rol.EMPLEADO)),
) {
    override fun onEvent(evento: AyudaEvent) {
        when (evento) {
            is AyudaEvent.Buscar -> actualizar {
                // Al buscar se abren las coincidencias para que se vean los pasos.
                val texto = evento.texto
                copy(busqueda = texto, expandidos = if (texto.isBlank()) emptySet() else expandidos)
            }
            is AyudaEvent.Alternar -> actualizar {
                copy(expandidos = if (evento.id in expandidos) expandidos - evento.id else expandidos + evento.id)
            }
            is AyudaEvent.IrAModulo -> emitir(UiEffect.Navegar(evento.destino, reemplazar = true))
            AyudaEvent.Volver -> emitir(UiEffect.Volver)
        }
    }
}
