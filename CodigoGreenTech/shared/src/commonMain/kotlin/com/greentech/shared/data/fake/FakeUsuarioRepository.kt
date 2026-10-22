package com.greentech.shared.data.fake

import com.greentech.shared.domain.model.Auditoria
import com.greentech.shared.domain.model.Rol
import com.greentech.shared.domain.model.TipoAuditoria
import com.greentech.shared.domain.model.Usuario
import com.greentech.shared.domain.repository.UsuarioRepository
import com.greentech.shared.domain.result.AppError
import com.greentech.shared.domain.result.AppResult
import com.greentech.shared.util.Fechas
import kotlinx.coroutines.flow.update

class FakeUsuarioRepository(private val store: FakeStore) : UsuarioRepository {

    override suspend fun obtenerTrabajadores(): AppResult<List<Usuario>> {
        simularLatencia()
        return AppResult.Exito(store.usuarios.value.sortedBy { it.nombre })
    }

    override suspend fun altaTrabajador(
        nombre: String,
        correo: String,
        passwordProvisional: String,
        rol: Rol,
        autor: String,
    ): AppResult<Usuario> {
        simularLatencia()
        if (store.usuarios.value.any { it.correo.equals(correo, ignoreCase = true) }) {
            return AppResult.Fallo(AppError.TrabajadorDuplicado)
        }
        val usuario = Usuario(store.nuevoId("u"), nombre, correo, rol, activo = true, fechaAlta = Fechas.hoy())
        store.usuarios.update { it + usuario }
        store.passwords[correo] = passwordProvisional
        registrarAuditoria(TipoAuditoria.ALTA_USUARIO, autor, "Alta de $nombre (${rol.etiqueta})")
        return AppResult.Exito(usuario)
    }

    override suspend fun bajaTrabajador(id: String, autor: String): AppResult<Usuario> {
        simularLatencia()
        val usuario = store.usuarios.value.firstOrNull { it.id == id } ?: return AppResult.Fallo(AppError.Desconocido)
        if (!usuario.activo) return AppResult.Fallo(AppError.UsuarioInactivo)
        val actualizado = usuario.copy(activo = false)
        store.usuarios.update { lista -> lista.map { if (it.id == id) actualizado else it } }
        registrarAuditoria(TipoAuditoria.BAJA_USUARIO, autor, "Baja de ${usuario.nombre} (${usuario.rol.etiqueta})")
        return AppResult.Exito(actualizado)
    }

    private fun registrarAuditoria(tipo: TipoAuditoria, autor: String, detalle: String) {
        store.auditoria.update { it + Auditoria(store.nuevoId("au"), tipo, autor, detalle, Fechas.ahora()) }
    }
}
