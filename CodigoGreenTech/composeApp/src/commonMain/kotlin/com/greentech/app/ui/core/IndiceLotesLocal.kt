package com.greentech.app.ui.core

import com.greentech.shared.domain.model.Lote

/**
 * Índice local de lotes por código QR y por id (RNF 3.3.1 y 3.3.4: consulta por QR "local primero").
 *
 * Se llena con cada lote que la app ya obtuvo (Inicio, Lista de lotes, Detalle, búsquedas). Al leer un
 * QR, el lote se busca aquí en O(1) — nunca recorriendo listas — y el Detalle se dibuja con estos datos
 * mientras el repositorio refresca en segundo plano. Funciona igual sin conexión.
 *
 * TODO(Supabase): cuando exista la base local (SQLDelight), este índice se sustituye por la consulta
 * `SELECT ... WHERE codigo_qr = ?` sobre la tabla local con índice único en codigo_qr.
 */
class IndiceLotesLocal {
    private val porCodigo = HashMap<String, Lote>()
    private val porId = HashMap<String, Lote>()

    fun recordar(lotes: Collection<Lote>) = synchronized(this) { lotes.forEach(::guardar) }

    fun recordar(lote: Lote) = synchronized(this) { guardar(lote) }

    fun buscarPorCodigo(codigo: String): Lote? = synchronized(this) { porCodigo[codigo.uppercase()] }

    fun buscarPorId(id: String): Lote? = synchronized(this) { porId[id] }

    private fun guardar(lote: Lote) {
        porId[lote.id]?.let { anterior -> if (anterior.codigo != lote.codigo) porCodigo.remove(anterior.codigo.uppercase()) }
        porCodigo[lote.codigo.uppercase()] = lote
        porId[lote.id] = lote
    }
}
