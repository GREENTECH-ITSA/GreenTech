package com.greentech.app.ui.preview

import com.greentech.shared.data.fake.FakeData

/** Datos de ejemplo solo para @Preview (no se usan en la app). */
internal object DatosPreview {
    val lotes = FakeData.lotes()
    val lote = lotes[4]
    val eventos = FakeData.bitacora(lotes).sortedByDescending { it.fecha }
    val lecturas = FakeData.lecturas().getValue(lote.zona.id)
    val auditoria = FakeData.auditoria()
    val usuarios = FakeData.usuarios()
    val especies = FakeData.especies
    val zonas = FakeData.zonas
}
