package com.greentech.shared.data.fake

import com.greentech.shared.domain.model.Auditoria
import com.greentech.shared.domain.model.Dashboard
import com.greentech.shared.domain.model.Indicador
import com.greentech.shared.domain.model.MermaEspecie
import com.greentech.shared.domain.model.Periodo
import com.greentech.shared.domain.model.PuntoMensual
import com.greentech.shared.domain.model.TipoSalida
import com.greentech.shared.domain.repository.ReporteRepository
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.util.Fechas
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import kotlinx.datetime.number

class FakeReporteRepository(private val store: FakeStore) : ReporteRepository {

    override suspend fun obtenerDashboard(periodo: Periodo): AppResult<Dashboard> {
        simularLatencia()
        val hoy = Fechas.hoy()
        val desde = hoy.minus(periodo.dias, DateTimeUnit.DAY)
        val lotes = store.lotes.value
        val salidas = store.salidas.value
        val enPeriodo = salidas.filter { it.fecha > desde }

        val indicador = Indicador(
            lotesActivos = lotes.count { it.cantidadDisponible > 0 },
            plantasDisponibles = lotes.sumOf { it.cantidadDisponible },
            plantasVendidas = enPeriodo.filter { it.tipo == TipoSalida.VENTA }.sumOf { it.cantidad },
            plantasMerma = enPeriodo.filter { it.tipo == TipoSalida.MERMA }.sumOf { it.cantidad },
        )

        // Últimos 6 meses, del más antiguo al actual.
        val meses = (5 downTo 0).map { hoy.minus(it, DateTimeUnit.MONTH) }
        val serie = meses.map { ref ->
            val delMes = salidas.filter { it.fecha.year == ref.year && it.fecha.month == ref.month }
            PuntoMensual(
                mes = Fechas.mesCorto(ref.month.number),
                ventas = delMes.filter { it.tipo == TipoSalida.VENTA }.sumOf { it.cantidad },
                merma = delMes.filter { it.tipo == TipoSalida.MERMA }.sumOf { it.cantidad },
            )
        }

        val mermaEspecie = enPeriodo.filter { it.tipo == TipoSalida.MERMA }
            .groupBy { it.especie }
            .map { (especie, lista) -> MermaEspecie(especie, lista.sumOf { it.cantidad }) }
            .sortedByDescending { it.cantidad }

        return AppResult.Exito(
            Dashboard(
                indicador = indicador,
                ventasVsMerma = serie,
                mermaPorEspecie = mermaEspecie,
                anomaliasAbiertas = store.anomalias.value.filter { it.abierta }.sortedByDescending { it.severidad.ordinal },
            ),
        )
    }

    override suspend fun obtenerAuditoria(): AppResult<List<Auditoria>> {
        simularLatencia()
        return AppResult.Exito(store.auditoria.value.sortedByDescending { it.fecha })
    }
}
