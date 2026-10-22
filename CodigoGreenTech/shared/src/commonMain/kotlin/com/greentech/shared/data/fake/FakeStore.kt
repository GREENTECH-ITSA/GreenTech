package com.greentech.shared.data.fake

import com.greentech.shared.domain.model.Actividad
import com.greentech.shared.domain.model.EventoBitacora
import com.greentech.shared.domain.model.Lote
import com.greentech.shared.domain.model.Observacion
import com.greentech.shared.domain.model.TipoEvento
import com.greentech.shared.domain.model.Usuario
import com.greentech.shared.util.Fechas
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * "Base de datos" en memoria compartida por todos los repositorios Fake.
 * Los cambios (alta de lotes, salidas, bajas…) duran mientras la app esté abierta.
 */
class FakeStore {
    val usuarios = MutableStateFlow(FakeData.usuarios())
    val passwords: MutableMap<String, String> = FakeData.passwords().toMutableMap()
    val sesion = MutableStateFlow<Usuario?>(null)

    val lotes = MutableStateFlow(FakeData.lotes())
    val lecturas = FakeData.lecturas()
    val anomalias = MutableStateFlow(FakeData.anomalias())
    val bitacora = MutableStateFlow(FakeData.bitacora(lotes.value))
    val salidas = MutableStateFlow(FakeData.salidas(lotes.value))
    val auditoria = MutableStateFlow(FakeData.auditoria())
    val observaciones = MutableStateFlow<List<Observacion>>(emptyList())
    val actividades = MutableStateFlow<List<Actividad>>(emptyList())

    private var contador = 1000

    fun nuevoId(prefijo: String): String = "$prefijo${++contador}"

    fun agregarEvento(lote: Lote, tipo: TipoEvento, titulo: String, descripcion: String, autor: String) {
        bitacora.update {
            it + EventoBitacora(nuevoId("e"), lote.id, lote.codigo, lote.especie.nombre, tipo, titulo, descripcion, autor, Fechas.ahora())
        }
    }

    fun actualizarLote(id: String, cambio: (Lote) -> Lote): Lote? {
        var resultado: Lote? = null
        lotes.update { lista ->
            lista.map { if (it.id == id) cambio(it).also { nuevo -> resultado = nuevo } else it }
        }
        return resultado
    }
}

/** Simula la latencia de red (600–900 ms) para que se vean los estados de carga. */
suspend fun simularLatencia() = delay(Random.nextLong(600, 901))
