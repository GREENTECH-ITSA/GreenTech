package com.greentech.shared.data.fake

import com.greentech.shared.domain.model.Auditoria
import com.greentech.shared.domain.model.MotivoMerma
import com.greentech.shared.domain.model.Salida
import com.greentech.shared.domain.model.TipoAuditoria
import com.greentech.shared.domain.model.TipoEvento
import com.greentech.shared.domain.model.TipoSalida
import com.greentech.shared.domain.repository.SalidaRepository
import com.greentech.shared.domain.result.AppError
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.util.Fechas
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate

class FakeSalidaRepository(private val store: FakeStore) : SalidaRepository {

    override suspend fun registrarSalida(
        loteId: String,
        tipo: TipoSalida,
        cantidad: Int,
        motivo: MotivoMerma?,
        fecha: LocalDate,
        responsable: String,
    ): AppResult<Salida> {
        simularLatencia()
        val actual = store.lotes.value.firstOrNull { it.id == loteId } ?: return AppResult.Fallo(AppError.LoteNoEncontrado)
        if (cantidad > actual.cantidadDisponible) return AppResult.Fallo(AppError.CantidadExcedida(actual.cantidadDisponible))
        val lote = store.actualizarLote(loteId) { it.copy(cantidadDisponible = it.cantidadDisponible - cantidad) }!!
        val salida = Salida(store.nuevoId("s"), loteId, lote.codigo, lote.especie.nombre, tipo, cantidad, motivo, fecha, responsable)
        store.salidas.update { it + salida }

        val titulo = "${tipo.etiqueta} de $cantidad plantas"
        val detalle = motivo?.let { "Motivo: ${it.etiqueta}" } ?: "Quedan ${lote.cantidadDisponible} plantas"
        store.agregarEvento(lote, TipoEvento.SALIDA, titulo, detalle, responsable)
        store.auditoria.update {
            it + Auditoria(
                id = store.nuevoId("au"),
                tipo = if (tipo == TipoSalida.VENTA) TipoAuditoria.VENTA else TipoAuditoria.BAJA_PLANTAS,
                usuario = responsable,
                detalle = "$titulo · ${lote.especie.nombre} (${lote.codigo})" + (motivo?.let { " · ${it.etiqueta}" } ?: ""),
                fecha = Fechas.ahora(),
            )
        }
        return AppResult.Exito(salida)
    }
}
