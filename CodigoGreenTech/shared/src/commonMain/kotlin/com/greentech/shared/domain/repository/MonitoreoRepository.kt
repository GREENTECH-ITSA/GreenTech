package com.greentech.shared.domain.repository

import com.greentech.shared.domain.model.Actividad
import com.greentech.shared.domain.model.AlertaSensor
import com.greentech.shared.domain.model.Anomalia
import com.greentech.shared.domain.model.CondicionesLote
import com.greentech.shared.domain.model.EstadoSalud
import com.greentech.shared.domain.model.EventoBitacora
import com.greentech.shared.domain.model.Observacion
import com.greentech.shared.domain.model.Recomendacion
import com.greentech.shared.domain.model.Severidad
import com.greentech.shared.domain.model.TipoActividad
import com.greentech.shared.domain.model.TipoAnomalia
import com.greentech.shared.domain.result.AppResult

interface MonitoreoRepository {
    suspend fun registrarObservacion(
        loteId: String,
        texto: String,
        estadoSalud: EstadoSalud,
        conFoto: Boolean,
        autor: String,
    ): AppResult<Observacion>

    suspend fun registrarActividad(
        loteId: String,
        tipo: TipoActividad,
        observacion: String,
        responsable: String,
    ): AppResult<Actividad>

    suspend fun obtenerCondiciones(loteId: String): AppResult<CondicionesLote>

    suspend fun obtenerAlertas(): AppResult<List<AlertaSensor>>

    suspend fun registrarAnomalia(
        loteId: String,
        tipo: TipoAnomalia,
        nombre: String,
        severidad: Severidad,
        descripcion: String,
        autor: String,
    ): AppResult<Anomalia>

    suspend fun obtenerRecomendaciones(loteId: String): AppResult<List<Recomendacion>>

    suspend fun obtenerBitacora(loteId: String): AppResult<List<EventoBitacora>>

    suspend fun obtenerActividadReciente(limite: Int): AppResult<List<EventoBitacora>>
}
