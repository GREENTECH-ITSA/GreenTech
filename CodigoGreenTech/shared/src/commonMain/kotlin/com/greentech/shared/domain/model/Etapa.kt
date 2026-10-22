package com.greentech.shared.domain.model

/** Etapas de desarrollo en el orden en que ocurren. */
enum class Etapa(val etiqueta: String) {
    GERMINACION("Germinación"),
    PLANTULA("Plántula"),
    CRECIMIENTO_VEGETATIVO("Crecimiento vegetativo"),
    FLORACION("Floración"),
    LISTA_PARA_VENTA("Lista para venta"),
}
