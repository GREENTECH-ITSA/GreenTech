package com.greentech.shared.data.fake

import com.greentech.shared.domain.model.Actividad
import com.greentech.shared.domain.model.AlertaSensor
import com.greentech.shared.domain.model.Anomalia
import com.greentech.shared.domain.model.CondicionesLote
import com.greentech.shared.domain.model.EstadoSalud
import com.greentech.shared.domain.model.EventoBitacora
import com.greentech.shared.domain.model.Lote
import com.greentech.shared.domain.model.Observacion
import com.greentech.shared.domain.model.Recomendacion
import com.greentech.shared.domain.model.Severidad
import com.greentech.shared.domain.model.TipoActividad
import com.greentech.shared.domain.model.TipoAlerta
import com.greentech.shared.domain.model.TipoAnomalia
import com.greentech.shared.domain.model.TipoEvento
import com.greentech.shared.domain.repository.MonitoreoRepository
import com.greentech.shared.domain.result.AppError
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.util.Fechas
import kotlinx.coroutines.flow.update

class FakeMonitoreoRepository(private val store: FakeStore) : MonitoreoRepository {

    private fun lote(id: String): Lote? = store.lotes.value.firstOrNull { it.id == id }

    override suspend fun registrarObservacion(
        loteId: String,
        texto: String,
        estadoSalud: EstadoSalud,
        conFoto: Boolean,
        autor: String,
    ): AppResult<Observacion> {
        simularLatencia()
        val lote = store.actualizarLote(loteId) { it.copy(estadoSalud = estadoSalud) }
            ?: return AppResult.Fallo(AppError.LoteNoEncontrado)
        val obs = Observacion(store.nuevoId("o"), loteId, texto, estadoSalud, conFoto, autor, Fechas.ahora())
        store.observaciones.update { it + obs }
        val extra = if (conFoto) " (con foto)" else ""
        store.agregarEvento(lote, TipoEvento.OBSERVACION, "Observación", "$texto$extra", autor)
        return AppResult.Exito(obs)
    }

    override suspend fun registrarActividad(
        loteId: String,
        tipo: TipoActividad,
        observacion: String,
        responsable: String,
    ): AppResult<Actividad> {
        simularLatencia()
        val lote = lote(loteId) ?: return AppResult.Fallo(AppError.LoteNoEncontrado)
        val act = Actividad(store.nuevoId("ac"), loteId, tipo, observacion, responsable, Fechas.ahora())
        store.actividades.update { it + act }
        store.agregarEvento(lote, TipoEvento.ACTIVIDAD, tipo.etiqueta, observacion.ifBlank { "Sin observaciones" }, responsable)
        return AppResult.Exito(act)
    }

    override suspend fun obtenerCondiciones(loteId: String): AppResult<CondicionesLote> {
        simularLatencia()
        val lote = lote(loteId) ?: return AppResult.Fallo(AppError.LoteNoEncontrado)
        val lecturas = store.lecturas[lote.zona.id].orEmpty()
        if (lecturas.isEmpty()) return AppResult.Fallo(AppError.SinInformacion)
        return AppResult.Exito(CondicionesLote(lote, lecturas))
    }

    override suspend fun obtenerAlertas(): AppResult<List<AlertaSensor>> {
        simularLatencia()
        val alertas = store.lotes.value.groupBy { it.zona }.flatMap { (zona, lotes) ->
            val ultima = store.lecturas[zona.id]?.maxByOrNull { it.fecha } ?: return@flatMap emptyList()
            val bajos = lotes.filter { ultima.humedad < it.especie.humedadMin }
            val altos = lotes.filter { ultima.temperatura > it.especie.temperaturaMax }
            buildList {
                if (bajos.isNotEmpty()) add(
                    AlertaSensor(zona, TipoAlerta.HUMEDAD_BAJA, ultima.humedad,
                        bajos.minOf { it.especie.humedadMin }, bajos.maxOf { it.especie.humedadMax },
                        bajos.map { it.especie.nombre }.distinct()),
                )
                if (altos.isNotEmpty()) add(
                    AlertaSensor(zona, TipoAlerta.TEMPERATURA_ALTA, ultima.temperatura,
                        altos.minOf { it.especie.temperaturaMin }, altos.minOf { it.especie.temperaturaMax },
                        altos.map { it.especie.nombre }.distinct()),
                )
            }
        }
        return AppResult.Exito(alertas)
    }

    override suspend fun registrarAnomalia(
        loteId: String,
        tipo: TipoAnomalia,
        nombre: String,
        severidad: Severidad,
        descripcion: String,
        autor: String,
    ): AppResult<Anomalia> {
        simularLatencia()
        val nuevoEstado = when (severidad) {
            Severidad.BAJA -> EstadoSalud.EN_OBSERVACION
            Severidad.MEDIA -> EstadoSalud.AFECTADA
            Severidad.ALTA -> EstadoSalud.CRITICA
        }
        val lote = store.actualizarLote(loteId) { l ->
            if (nuevoEstado.ordinal > l.estadoSalud.ordinal) l.copy(estadoSalud = nuevoEstado) else l
        } ?: return AppResult.Fallo(AppError.LoteNoEncontrado)
        val anomalia = Anomalia(store.nuevoId("a"), loteId, lote.codigo, lote.especie.nombre, tipo, nombre, severidad,
            descripcion, autor, Fechas.ahora(), abierta = true)
        store.anomalias.update { it + anomalia }
        store.agregarEvento(lote, TipoEvento.ANOMALIA, "${tipo.etiqueta}: $nombre", "Severidad ${severidad.etiqueta.lowercase()}. $descripcion", autor)
        return AppResult.Exito(anomalia)
    }

    override suspend fun obtenerRecomendaciones(loteId: String): AppResult<List<Recomendacion>> {
        simularLatencia()
        val lote = lote(loteId) ?: return AppResult.Fallo(AppError.LoteNoEncontrado)
        val porAnomalia = store.anomalias.value
            .filter { it.loteId == loteId && it.abierta }
            .sortedByDescending { it.fecha }
            .distinctBy { it.nombre }
            .mapNotNull { a ->
                FakeData.recomendacionesPorAnomalia[a.nombre]?.let {
                    Recomendacion("r-${a.id}", it.titulo, it.categoria, it.pasos, "Por anomalía: ${a.nombre}")
                }
            }
        val ultima = store.lecturas[lote.zona.id]?.maxByOrNull { it.fecha }
        val esp = lote.especie
        val porCondicion = buildList {
            if (ultima != null) {
                if (ultima.humedad < esp.humedadMin) add(FakeData.recomendacionHumedadBaja to "Por condición: humedad baja")
                if (ultima.humedad > esp.humedadMax) add(FakeData.recomendacionHumedadAlta to "Por condición: humedad alta")
                if (ultima.temperatura > esp.temperaturaMax) add(FakeData.recomendacionTemperaturaAlta to "Por condición: temperatura alta")
                if (ultima.temperatura < esp.temperaturaMin) add(FakeData.recomendacionTemperaturaBaja to "Por condición: temperatura baja")
            }
        }.mapIndexed { i, (p, origen) -> Recomendacion("rc-$loteId-$i", p.titulo, p.categoria, p.pasos, origen) }
        return AppResult.Exito(porAnomalia + porCondicion)
    }

    override suspend fun obtenerBitacora(loteId: String): AppResult<List<EventoBitacora>> {
        simularLatencia()
        if (lote(loteId) == null) return AppResult.Fallo(AppError.LoteNoEncontrado)
        return AppResult.Exito(store.bitacora.value.filter { it.loteId == loteId }.sortedByDescending { it.fecha })
    }

    override suspend fun obtenerActividadReciente(limite: Int): AppResult<List<EventoBitacora>> {
        simularLatencia()
        return AppResult.Exito(
            store.bitacora.value
                .filter { it.tipo != TipoEvento.REGISTRO }
                .sortedByDescending { it.fecha }
                .take(limite),
        )
    }
}
