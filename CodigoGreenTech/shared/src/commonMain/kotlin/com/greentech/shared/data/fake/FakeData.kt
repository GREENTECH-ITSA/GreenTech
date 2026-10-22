package com.greentech.shared.data.fake

import com.greentech.shared.domain.model.Anomalia
import com.greentech.shared.domain.model.Auditoria
import com.greentech.shared.domain.model.CategoriaRecomendacion
import com.greentech.shared.domain.model.Especie
import com.greentech.shared.domain.model.EstadoSalud
import com.greentech.shared.domain.model.Etapa
import com.greentech.shared.domain.model.EventoBitacora
import com.greentech.shared.domain.model.LecturaSensor
import com.greentech.shared.domain.model.Lote
import com.greentech.shared.domain.model.MotivoMerma
import com.greentech.shared.domain.model.Rol
import com.greentech.shared.domain.model.Salida
import com.greentech.shared.domain.model.Severidad
import com.greentech.shared.domain.model.TipoAnomalia
import com.greentech.shared.domain.model.TipoAuditoria
import com.greentech.shared.domain.model.TipoEvento
import com.greentech.shared.domain.model.TipoSalida
import com.greentech.shared.domain.model.Usuario
import com.greentech.shared.domain.model.Zona
import com.greentech.shared.util.Fechas
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/**
 * Datos semilla de un vivero en México. Las fechas son relativas a "hoy"
 * para que la app siempre se vea con información reciente.
 */
object FakeData {

    // Cuentas de prueba para desarrollo. No se muestran en la UI.
    const val CORREO_ADMIN = "admin@gmail.com"
    const val CORREO_EMPLEADO = "empleado@gmail.com"
    const val CORREO_INACTIVO = "inactivo@gmail.com"

    fun usuarios(): List<Usuario> = listOf(
        Usuario("u1", "Administrador GreenTech", CORREO_ADMIN, Rol.ADMINISTRADOR, true, Fechas.haceDias(420)),
        Usuario("u2", "Juan Pérez", CORREO_EMPLEADO, Rol.EMPLEADO, true, Fechas.haceDias(380)),
        Usuario("u3", "Luis Gómez", CORREO_INACTIVO, Rol.EMPLEADO, false, Fechas.haceDias(350)),
        Usuario("u4", "María Hernández", "maria.hernandez@greentech.mx", Rol.EMPLEADO, true, Fechas.haceDias(240)),
        Usuario("u5", "Ana López", "ana.lopez@greentech.mx", Rol.EMPLEADO, true, Fechas.haceDias(120)),
        Usuario("u6", "Carlos Ramírez", "carlos.ramirez@greentech.mx", Rol.ADMINISTRADOR, true, Fechas.haceDias(60)),
    )

    /** Contraseñas por correo (solo para los Fakes; en Supabase las maneja Auth). */
    fun passwords(): Map<String, String> = mapOf(
        CORREO_ADMIN to "admin123",
        CORREO_EMPLEADO to "empleado123",
        CORREO_INACTIVO to "inactivo123",
        "maria.hernandez@greentech.mx" to "greentech123",
        "ana.lopez@greentech.mx" to "greentech123",
        "carlos.ramirez@greentech.mx" to "greentech123",
    )

    // ---------- Catálogos ----------
    val agave = Especie("esp-agave", "Agave", "Agave americana", 15.0, 35.0, 12.0, 35.0)
    val nochebuena = Especie("esp-nochebuena", "Nochebuena", "Euphorbia pulcherrima", 45.0, 65.0, 18.0, 26.0)
    val lavanda = Especie("esp-lavanda", "Lavanda", "Lavandula angustifolia", 25.0, 45.0, 15.0, 28.0)
    val echeveria = Especie("esp-echeveria", "Echeveria", "Echeveria elegans", 15.0, 35.0, 18.0, 30.0)
    val rosa = Especie("esp-rosa", "Rosa", "Rosa chinensis", 50.0, 70.0, 16.0, 26.0)
    val albahaca = Especie("esp-albahaca", "Albahaca", "Ocimum basilicum", 55.0, 75.0, 20.0, 30.0)
    val cempasuchil = Especie("esp-cempasuchil", "Cempasúchil", "Tagetes erecta", 25.0, 50.0, 15.0, 30.0)
    val helecho = Especie("esp-helecho", "Helecho Boston", "Nephrolepis exaltata", 50.0, 75.0, 18.0, 26.0)

    val especies = listOf(agave, nochebuena, lavanda, echeveria, rosa, albahaca, cempasuchil, helecho)

    val inv1 = Zona("z-inv1", "Invernadero 1")
    val inv2 = Zona("z-inv2", "Invernadero 2")
    val exterior = Zona("z-ext", "Vivero exterior")
    val zonas = listOf(inv1, inv2, exterior)

    // ---------- Lotes ----------
    fun lotes(): List<Lote> = listOf(
        Lote("l1", "GT-8F2A91C0D4", agave, exterior, 1200, 980, Etapa.CRECIMIENTO_VEGETATIVO, EstadoSalud.SANA, Fechas.haceDias(150), "Juan Pérez"),
        Lote("l2", "GT-3B7E4D21A9", nochebuena, inv1, 800, 640, Etapa.FLORACION, EstadoSalud.SANA, Fechas.haceDias(120), "María Hernández"),
        Lote("l3", "GT-C41F09B8E2", lavanda, inv2, 500, 455, Etapa.CRECIMIENTO_VEGETATIVO, EstadoSalud.EN_OBSERVACION, Fechas.haceDias(75), "Juan Pérez"),
        Lote("l4", "GT-5D8A2E6F13", echeveria, inv2, 600, 590, Etapa.PLANTULA, EstadoSalud.SANA, Fechas.haceDias(40), "Ana López"),
        Lote("l5", "GT-A93C7B0E5F", rosa, inv1, 350, 290, Etapa.FLORACION, EstadoSalud.AFECTADA, Fechas.haceDias(110), "Juan Pérez"),
        Lote("l6", "GT-17E5F4C2B8", albahaca, inv2, 900, 720, Etapa.LISTA_PARA_VENTA, EstadoSalud.SANA, Fechas.haceDias(60), "María Hernández"),
        Lote("l7", "GT-E2B6098D4A", cempasuchil, exterior, 1500, 1180, Etapa.LISTA_PARA_VENTA, EstadoSalud.EN_OBSERVACION, Fechas.haceDias(95), "Ana López"),
        Lote("l8", "GT-6C0D3A7E91", helecho, inv1, 400, 384, Etapa.CRECIMIENTO_VEGETATIVO, EstadoSalud.AFECTADA, Fechas.haceDias(85), "Juan Pérez"),
        Lote("l9", "GT-9A4E1F8C27", nochebuena, inv1, 700, 700, Etapa.GERMINACION, EstadoSalud.SANA, Fechas.haceDias(10), "María Hernández"),
        Lote("l10", "GT-B5F2C6D03E", agave, exterior, 300, 210, Etapa.PLANTULA, EstadoSalud.CRITICA, Fechas.haceDias(30), "Juan Pérez"),
    )

    // ---------- Sensores: 25 lecturas por zona (cada hora, últimas 24 h) ----------
    fun lecturas(): Map<String, List<LecturaSensor>> {
        fun serie(zona: Zona, humedad: (Int) -> Double, temperatura: (Int) -> Double) =
            (24 downTo 0).map { h ->
                LecturaSensor(
                    zonaId = zona.id,
                    humedad = redondear(humedad(h)),
                    temperatura = redondear(temperatura(h)),
                    fecha = Fechas.hace(h.hours + 4.minutes),
                )
            }
        return mapOf(
            inv1.id to serie(inv1, { h -> 58 + 6 * sin(h / 24.0 * 2 * PI) }, { h -> 22 + 3 * sin((h + 6) / 24.0 * 2 * PI) }),
            // Invernadero 2: la humedad baja de forma constante hasta 18 % → dispara la alerta.
            inv2.id to serie(inv2, { h -> 18 + h * 0.7 + sin(h.toDouble()) }, { h -> 24 + 3 * sin((h + 6) / 24.0 * 2 * PI) }),
            exterior.id to serie(exterior, { h -> 30 + 4 * sin(h / 12.0 * PI) }, { h -> 21 + 4 * sin((h + 6) / 24.0 * 2 * PI) }),
        )
    }

    // ---------- Anomalías (3 abiertas y 1 cerrada) ----------
    fun anomalias(): List<Anomalia> = listOf(
        Anomalia("a1", "l5", "GT-A93C7B0E5F", "Rosa", TipoAnomalia.PLAGA, "Pulgón", Severidad.ALTA,
            "Colonias de pulgón en brotes nuevos y botones florales.", "Juan Pérez", Fechas.hace(20.hours), true),
        Anomalia("a2", "l8", "GT-6C0D3A7E91", "Helecho Boston", TipoAnomalia.ENFERMEDAD, "Pudrición de raíz", Severidad.MEDIA,
            "Raíces oscuras y blandas en 12 macetas del extremo norte.", "María Hernández", Fechas.hace(46.hours), true),
        Anomalia("a3", "l3", "GT-C41F09B8E2", "Lavanda", TipoAnomalia.ANOMALIA, "Hojas amarillas", Severidad.BAJA,
            "Hojas inferiores amarillas en algunas plantas.", "Juan Pérez", Fechas.hace(30.hours), true),
        Anomalia("a4", "l7", "GT-E2B6098D4A", "Cempasúchil", TipoAnomalia.PLAGA, "Mosca blanca", Severidad.MEDIA,
            "Presencia leve de mosca blanca, ya controlada.", "Ana López", Fechas.hace(240.hours), false),
    )

    // ---------- Bitácora (36 eventos) ----------
    fun bitacora(lotes: List<Lote>): List<EventoBitacora> {
        val porId = lotes.associateBy { it.id }
        var n = 0
        fun ev(loteId: String, tipo: TipoEvento, titulo: String, desc: String, autor: String, horas: Int): EventoBitacora {
            val l = porId.getValue(loteId)
            return EventoBitacora("e${++n}", loteId, l.codigo, l.especie.nombre, tipo, titulo, desc, autor, Fechas.hace(horas.hours))
        }
        val registros = lotes.map { l ->
            EventoBitacora("e${++n}", l.id, l.codigo, l.especie.nombre, TipoEvento.REGISTRO, "Lote registrado",
                "${l.cantidadInicial} plantas en ${l.zona.nombre}", l.responsable,
                kotlinx.datetime.LocalDateTime(l.fechaSiembra, kotlinx.datetime.LocalTime(8, 30)))
        }
        return registros + listOf(
            ev("l1", TipoEvento.ETAPA, "Etapa: Crecimiento vegetativo", "Estado de salud: Sana", "Juan Pérez", 900),
            ev("l1", TipoEvento.ACTIVIDAD, "Riego manual", "Riego ligero por la mañana.", "Juan Pérez", 26),
            ev("l1", TipoEvento.SALIDA, "Venta de 120 plantas", "Cliente: Jardines del Valle", "Juan Pérez", 150),
            ev("l2", TipoEvento.ETAPA, "Etapa: Floración", "Brácteas comenzando a pintar.", "María Hernández", 300),
            ev("l2", TipoEvento.ACTIVIDAD, "Fertilización", "Fertilizante 20-20-20, dosis baja.", "María Hernández", 52),
            ev("l2", TipoEvento.OBSERVACION, "Observación", "Buen color en brácteas, sin plagas visibles.", "Juan Pérez", 8),
            ev("l3", TipoEvento.OBSERVACION, "Observación", "Sustrato muy seco al tacto.", "Juan Pérez", 5),
            ev("l3", TipoEvento.ANOMALIA, "Anomalía: Hojas amarillas", "Severidad baja", "Juan Pérez", 30),
            ev("l3", TipoEvento.ACTIVIDAD, "Revisión", "Revisión general del lote.", "Ana López", 72),
            ev("l4", TipoEvento.ETAPA, "Etapa: Plántula", "Estado de salud: Sana", "Ana López", 400),
            ev("l4", TipoEvento.ACTIVIDAD, "Trasplante", "Trasplante a charola de 50 cavidades.", "Ana López", 98),
            ev("l5", TipoEvento.ANOMALIA, "Anomalía: Pulgón", "Severidad alta", "Juan Pérez", 20),
            ev("l5", TipoEvento.ACTIVIDAD, "Control fitosanitario", "Aplicación de jabón potásico.", "Juan Pérez", 18),
            ev("l5", TipoEvento.SALIDA, "Merma de 15 plantas", "Motivo: Plaga", "Juan Pérez", 44),
            ev("l5", TipoEvento.ETAPA, "Etapa: Floración", "Estado de salud: Afectada", "Juan Pérez", 21),
            ev("l6", TipoEvento.ETAPA, "Etapa: Lista para venta", "Estado de salud: Sana", "María Hernández", 120),
            ev("l6", TipoEvento.SALIDA, "Venta de 180 plantas", "Mercado de Atlixco", "María Hernández", 30),
            ev("l6", TipoEvento.ACTIVIDAD, "Poda", "Despunte para favorecer ramificación.", "Juan Pérez", 200),
            ev("l7", TipoEvento.SALIDA, "Venta de 300 plantas", "Pedido para Día de Muertos", "Ana López", 10),
            ev("l7", TipoEvento.OBSERVACION, "Observación", "Algunas flores con puntas secas.", "Ana López", 14),
            ev("l7", TipoEvento.ANOMALIA, "Anomalía: Mosca blanca", "Severidad media · cerrada", "Ana López", 240),
            ev("l8", TipoEvento.ANOMALIA, "Anomalía: Pudrición de raíz", "Severidad media", "María Hernández", 46),
            ev("l8", TipoEvento.ACTIVIDAD, "Riego manual", "Se redujo la frecuencia de riego.", "María Hernández", 40),
            ev("l9", TipoEvento.ACTIVIDAD, "Revisión", "Germinación uniforme al 85 %.", "María Hernández", 3),
            ev("l10", TipoEvento.OBSERVACION, "Observación", "Daño por granizo en hojas exteriores.", "Juan Pérez", 60),
            ev("l10", TipoEvento.SALIDA, "Merma de 90 plantas", "Motivo: Daño físico", "Juan Pérez", 58),
        )
    }

    // ---------- Salidas de los últimos 6 meses ----------
    fun salidas(lotes: List<Lote>): List<Salida> {
        val rnd = Random(42)
        val vendibles = lotes.filter { it.etapa == Etapa.LISTA_PARA_VENTA || it.etapa == Etapa.FLORACION || it.especie == agave }
        val responsables = listOf("Juan Pérez", "María Hernández", "Ana López")
        val lista = mutableListOf<Salida>()
        var n = 0
        for (dia in 1..180 step 3) {
            val l = vendibles[rnd.nextInt(vendibles.size)]
            lista += Salida("s${++n}", l.id, l.codigo, l.especie.nombre, TipoSalida.VENTA, rnd.nextInt(20, 90), null,
                Fechas.haceDias(dia), responsables[rnd.nextInt(responsables.size)])
            if (dia % 9 == 1) {
                val m = lotes[rnd.nextInt(lotes.size)]
                lista += Salida("s${++n}", m.id, m.codigo, m.especie.nombre, TipoSalida.MERMA, rnd.nextInt(4, 28),
                    MotivoMerma.entries[rnd.nextInt(MotivoMerma.entries.size)], Fechas.haceDias(dia + 1),
                    responsables[rnd.nextInt(responsables.size)])
            }
        }
        return lista
    }

    // ---------- Auditoría (15 registros) ----------
    fun auditoria(): List<Auditoria> = listOf(
        Auditoria("au1", TipoAuditoria.VENTA, "Ana López", "Venta de 300 plantas · Cempasúchil (GT-E2B6098D4A)", Fechas.hace(10.hours)),
        Auditoria("au2", TipoAuditoria.VENTA, "María Hernández", "Venta de 180 plantas · Albahaca (GT-17E5F4C2B8)", Fechas.hace(30.hours)),
        Auditoria("au3", TipoAuditoria.BAJA_PLANTAS, "Juan Pérez", "Merma de 15 plantas · Rosa (GT-A93C7B0E5F) · Plaga", Fechas.hace(44.hours)),
        Auditoria("au4", TipoAuditoria.BAJA_PLANTAS, "Juan Pérez", "Merma de 90 plantas · Agave (GT-B5F2C6D03E) · Daño físico", Fechas.hace(58.hours)),
        Auditoria("au5", TipoAuditoria.ALTA_USUARIO, "Administrador GreenTech", "Alta de Carlos Ramírez (Administrador)", Fechas.hace((60 * 24).hours)),
        Auditoria("au6", TipoAuditoria.VENTA, "Juan Pérez", "Venta de 120 plantas · Agave (GT-8F2A91C0D4)", Fechas.hace(150.hours)),
        Auditoria("au7", TipoAuditoria.BAJA_USUARIO, "Administrador GreenTech", "Baja de Luis Gómez (Empleado)", Fechas.hace((15 * 24).hours)),
        Auditoria("au8", TipoAuditoria.ALTA_USUARIO, "Administrador GreenTech", "Alta de Ana López (Empleado)", Fechas.hace((120 * 24).hours)),
        Auditoria("au9", TipoAuditoria.VENTA, "María Hernández", "Venta de 75 plantas · Nochebuena (GT-3B7E4D21A9)", Fechas.hace(200.hours)),
        Auditoria("au10", TipoAuditoria.BAJA_PLANTAS, "María Hernández", "Merma de 16 plantas · Helecho Boston (GT-6C0D3A7E91) · Enfermedad", Fechas.hace(260.hours)),
        Auditoria("au11", TipoAuditoria.ALTA_USUARIO, "Administrador GreenTech", "Alta de María Hernández (Empleado)", Fechas.hace((240 * 24).hours)),
        Auditoria("au12", TipoAuditoria.VENTA, "Ana López", "Venta de 60 plantas · Cempasúchil (GT-E2B6098D4A)", Fechas.hace(320.hours)),
        Auditoria("au13", TipoAuditoria.BAJA_PLANTAS, "Juan Pérez", "Merma de 45 plantas · Lavanda (GT-C41F09B8E2) · Estrés hídrico", Fechas.hace(400.hours)),
        Auditoria("au14", TipoAuditoria.ALTA_USUARIO, "Administrador GreenTech", "Alta de Juan Pérez (Empleado)", Fechas.hace((380 * 24).hours)),
        Auditoria("au15", TipoAuditoria.BAJA_USUARIO, "Carlos Ramírez", "Intento de baja de Luis Gómez: el usuario ya estaba inactivo", Fechas.hace((14 * 24).hours)),
    )

    // ---------- Catálogo de recomendaciones ----------
    data class PlantillaRecomendacion(val titulo: String, val categoria: CategoriaRecomendacion, val pasos: List<String>)

    val recomendacionesPorAnomalia: Map<String, PlantillaRecomendacion> = mapOf(
        "Pulgón" to PlantillaRecomendacion("Controlar el pulgón", CategoriaRecomendacion.PLAGA, listOf(
            "Separa las plantas más afectadas.",
            "Aplica jabón potásico en hojas y brotes, por arriba y por abajo.",
            "Repite la aplicación cada 5 días durante 2 semanas.",
            "Revisa los brotes nuevos cada 2 días.",
        )),
        "Mosca blanca" to PlantillaRecomendacion("Controlar la mosca blanca", CategoriaRecomendacion.PLAGA, listOf(
            "Coloca trampas amarillas pegajosas entre las plantas.",
            "Aplica aceite de neem al atardecer.",
            "Retira las hojas muy dañadas.",
        )),
        "Araña roja" to PlantillaRecomendacion("Controlar la araña roja", CategoriaRecomendacion.PLAGA, listOf(
            "Aumenta la humedad del ambiente con nebulización.",
            "Aplica acaricida autorizado en el envés de las hojas.",
            "Revisa de nuevo en 3 días.",
        )),
        "Trips" to PlantillaRecomendacion("Controlar trips", CategoriaRecomendacion.PLAGA, listOf(
            "Coloca trampas azules pegajosas.",
            "Aplica insecticida biológico (Beauveria) cada 7 días.",
        )),
        "Cochinilla" to PlantillaRecomendacion("Controlar la cochinilla", CategoriaRecomendacion.PLAGA, listOf(
            "Limpia las colonias con un algodón con alcohol.",
            "Aplica aceite mineral ligero.",
        )),
        "Oídio" to PlantillaRecomendacion("Tratar el oídio", CategoriaRecomendacion.ENFERMEDAD, listOf(
            "Mejora la ventilación del invernadero.",
            "Aplica azufre mojable en las primeras horas del día.",
            "Evita mojar las hojas al regar.",
        )),
        "Pudrición de raíz" to PlantillaRecomendacion("Tratar la pudrición de raíz", CategoriaRecomendacion.ENFERMEDAD, listOf(
            "Reduce el riego hasta que el sustrato seque en la superficie.",
            "Retira las plantas con raíz blanda y oscura.",
            "Aplica un fungicida para raíz en las plantas sanas cercanas.",
            "Revisa que las macetas drenen bien.",
        )),
        "Botrytis" to PlantillaRecomendacion("Tratar la botrytis", CategoriaRecomendacion.ENFERMEDAD, listOf(
            "Retira flores y hojas con moho gris.",
            "Baja la humedad del ambiente y ventila.",
        )),
        "Roya" to PlantillaRecomendacion("Tratar la roya", CategoriaRecomendacion.ENFERMEDAD, listOf(
            "Retira las hojas con manchas naranjas.",
            "Aplica fungicida a base de cobre.",
        )),
        "Mildiu" to PlantillaRecomendacion("Tratar el mildiu", CategoriaRecomendacion.ENFERMEDAD, listOf(
            "Riega solo el sustrato, no las hojas.",
            "Aplica fungicida preventivo cada 10 días.",
        )),
        "Hojas amarillas" to PlantillaRecomendacion("Revisar riego y nutrición", CategoriaRecomendacion.NUTRICION, listOf(
            "Revisa si el sustrato está muy seco o muy mojado.",
            "Aplica fertilizante con nitrógeno en dosis baja.",
            "Observa las hojas nuevas durante una semana.",
        )),
        "Marchitez" to PlantillaRecomendacion("Recuperar plantas marchitas", CategoriaRecomendacion.RIEGO, listOf(
            "Riega de inmediato si el sustrato está seco.",
            "Mueve las plantas a media sombra por 2 días.",
        )),
        "Quemadura solar" to PlantillaRecomendacion("Proteger del sol", CategoriaRecomendacion.TEMPERATURA, listOf(
            "Coloca malla sombra de 30 a 50 %.",
            "Riega temprano por la mañana.",
        )),
        "Falta de nutrientes" to PlantillaRecomendacion("Mejorar la nutrición", CategoriaRecomendacion.NUTRICION, listOf(
            "Aplica fertilizante completo en dosis baja.",
            "Repite cada 15 días.",
        )),
        "Crecimiento lento" to PlantillaRecomendacion("Impulsar el crecimiento", CategoriaRecomendacion.GENERAL, listOf(
            "Revisa luz, riego y temperatura del lote.",
            "Aplica fertilizante foliar suave.",
        )),
    )

    val recomendacionHumedadBaja = PlantillaRecomendacion("Aumentar el riego", CategoriaRecomendacion.RIEGO, listOf(
        "Haz un riego manual ligero hoy mismo.",
        "Revisa que los goteros no estén tapados.",
        "Vuelve a revisar la humedad en 2 horas.",
    ))
    val recomendacionHumedadAlta = PlantillaRecomendacion("Reducir el riego", CategoriaRecomendacion.RIEGO, listOf(
        "Suspende el riego hasta que el sustrato seque en la superficie.",
        "Abre ventilas para que circule el aire.",
    ))
    val recomendacionTemperaturaAlta = PlantillaRecomendacion("Bajar la temperatura", CategoriaRecomendacion.TEMPERATURA, listOf(
        "Abre ventilas y cortinas laterales.",
        "Coloca malla sombra.",
        "Nebuliza en las horas de más calor.",
    ))
    val recomendacionTemperaturaBaja = PlantillaRecomendacion("Proteger del frío", CategoriaRecomendacion.TEMPERATURA, listOf(
        "Cierra ventilas al atardecer.",
        "Cubre las plantas con manta térmica por la noche.",
    ))

    private fun redondear(valor: Double) = (valor * 10).roundToInt() / 10.0
}
