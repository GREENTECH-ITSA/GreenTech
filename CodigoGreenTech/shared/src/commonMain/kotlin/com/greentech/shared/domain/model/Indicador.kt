package com.greentech.shared.domain.model

enum class Periodo(val etiqueta: String, val dias: Int) {
    SEMANA("Semana", 7),
    MES("Mes", 30),
    ANIO("Año", 365),
}

/** Indicadores clave del dashboard del administrador. */
data class Indicador(
    val lotesActivos: Int,
    val plantasDisponibles: Int,
    val plantasVendidas: Int,
    val plantasMerma: Int,
) {
    /** Porcentaje de merma sobre el total de salidas del periodo. */
    val porcentajeMerma: Double
        get() {
            val total = plantasVendidas + plantasMerma
            return if (total == 0) 0.0 else plantasMerma * 100.0 / total
        }
}

data class PuntoMensual(val mes: String, val ventas: Int, val merma: Int)

data class MermaEspecie(val especie: String, val cantidad: Int)

data class Dashboard(
    val indicador: Indicador,
    val ventasVsMerma: List<PuntoMensual>,
    val mermaPorEspecie: List<MermaEspecie>,
    val anomaliasAbiertas: List<Anomalia>,
) {
    val sinDatos: Boolean
        get() = indicador.lotesActivos == 0 && ventasVsMerma.all { it.ventas == 0 && it.merma == 0 }
}
