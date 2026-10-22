package com.greentech.shared.data.fake

import com.greentech.shared.domain.model.Especie
import com.greentech.shared.domain.model.EstadoSalud
import com.greentech.shared.domain.model.Etapa
import com.greentech.shared.domain.model.Lote
import com.greentech.shared.domain.model.TipoEvento
import com.greentech.shared.domain.model.Zona
import com.greentech.shared.domain.repository.LoteRepository
import com.greentech.shared.domain.result.AppError
import com.greentech.shared.domain.result.AppResult
import kotlin.random.Random
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate

class FakeLoteRepository(private val store: FakeStore) : LoteRepository {

    override suspend fun obtenerLotes(): AppResult<List<Lote>> {
        simularLatencia()
        return AppResult.Exito(store.lotes.value.sortedByDescending { it.fechaSiembra })
    }

    override suspend fun obtenerLote(id: String): AppResult<Lote> {
        simularLatencia()
        return store.lotes.value.firstOrNull { it.id == id }?.let { AppResult.Exito(it) }
            ?: AppResult.Fallo(AppError.LoteNoEncontrado)
    }

    override suspend fun buscarPorCodigo(codigo: String): AppResult<Lote> {
        simularLatencia()
        return store.lotes.value.firstOrNull { it.codigo.equals(codigo, ignoreCase = true) }?.let { AppResult.Exito(it) }
            ?: AppResult.Fallo(AppError.LoteNoEncontrado)
    }

    override suspend fun obtenerEspecies(): AppResult<List<Especie>> = AppResult.Exito(FakeData.especies)

    override suspend fun obtenerZonas(): AppResult<List<Zona>> = AppResult.Exito(FakeData.zonas)

    override suspend fun registrarLote(
        especieId: String,
        zonaId: String,
        cantidad: Int,
        fechaSiembra: LocalDate,
        responsable: String,
    ): AppResult<Lote> {
        simularLatencia()
        val especie = FakeData.especies.firstOrNull { it.id == especieId } ?: return AppResult.Fallo(AppError.CamposObligatorios)
        val zona = FakeData.zonas.firstOrNull { it.id == zonaId } ?: return AppResult.Fallo(AppError.CamposObligatorios)
        val lote = Lote(
            id = store.nuevoId("l"),
            codigo = generarCodigo(),
            especie = especie,
            zona = zona,
            cantidadInicial = cantidad,
            cantidadDisponible = cantidad,
            etapa = Etapa.GERMINACION,
            estadoSalud = EstadoSalud.SANA,
            fechaSiembra = fechaSiembra,
            responsable = responsable,
        )
        store.lotes.update { it + lote }
        store.agregarEvento(lote, TipoEvento.REGISTRO, "Lote registrado", "$cantidad plantas en ${zona.nombre}", responsable)
        return AppResult.Exito(lote)
    }

    override suspend fun actualizarEtapa(
        loteId: String,
        etapa: Etapa,
        estadoSalud: EstadoSalud,
        nota: String,
        autor: String,
    ): AppResult<Lote> {
        simularLatencia()
        val lote = store.actualizarLote(loteId) { it.copy(etapa = etapa, estadoSalud = estadoSalud) }
            ?: return AppResult.Fallo(AppError.LoteNoEncontrado)
        val detalle = buildString {
            append("Estado de salud: ${estadoSalud.etiqueta}")
            if (nota.isNotBlank()) append(". $nota")
        }
        store.agregarEvento(lote, TipoEvento.ETAPA, "Etapa: ${etapa.etiqueta}", detalle, autor)
        return AppResult.Exito(lote)
    }

    private fun generarCodigo(): String {
        val hex = "0123456789ABCDEF"
        var codigo: String
        do {
            codigo = "GT-" + (1..10).map { hex[Random.nextInt(hex.length)] }.joinToString("")
        } while (store.lotes.value.any { it.codigo == codigo })
        return codigo
    }
}
