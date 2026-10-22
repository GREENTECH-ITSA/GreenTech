package com.greentech.app.ui.screens.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Key
import androidx.compose.ui.graphics.vector.ImageVector
import com.greentech.app.navigation.AppDestination
import com.greentech.app.ui.components.IconosModulo
import com.greentech.shared.domain.model.Rol

/** Guía de un módulo: qué hace, cuándo usarlo y pasos numerados. */
data class ModuloAyuda(
    val id: String,
    val titulo: String,
    val icono: ImageVector,
    val queHace: String,
    val cuando: String,
    val pasos: List<String>,
    /** Pantalla a la que lleva el botón "Ir al módulo". */
    val destino: AppDestination?,
)

data class PreguntaFrecuente(val id: String, val pregunta: String, val respuesta: String)

data class ContenidoAyuda(
    val primerosPasos: List<String>,
    val modulos: List<ModuloAyuda>,
    val preguntas: List<PreguntaFrecuente>,
)

/**
 * Todo el contenido del Centro de ayuda como datos. Español sencillo, frases cortas.
 * Para cambiar un texto de ayuda solo se edita este archivo.
 */
object AyudaContent {

    fun para(rol: Rol): ContenidoAyuda = when (rol) {
        Rol.EMPLEADO -> ContenidoAyuda(primerosPasosEmpleado, modulosEmpleado, preguntas(rol))
        Rol.ADMINISTRADOR -> ContenidoAyuda(primerosPasosAdmin, modulosAdmin, preguntas(rol))
    }

    // ---------------------------------------------------------------- Primeros pasos
    private val primerosPasosEmpleado = listOf(
        "GreenTech te ayuda a llevar el control de los lotes de plantas del vivero.",
        "Abajo tienes la barra con: Inicio, Lotes, Escanear y Perfil. Toca cualquiera para cambiar de sección.",
        "La pestaña \"Escanear\" de la barra de abajo abre la cámara para leer el QR de un lote.",
        "Al abrir un lote verás botones grandes para registrar todo lo que haces en campo.",
        "Si tienes dudas, toca el botón \"?\" arriba a la derecha en cualquier pantalla.",
    )

    private val primerosPasosAdmin = listOf(
        "GreenTech te muestra cómo va el vivero y te deja administrar al equipo.",
        "Abajo tienes la barra con: Dashboard, Personal, Lotes y Perfil.",
        "En Dashboard ves los números clave: lotes, ventas y merma.",
        "Tú solo consultas los lotes; los registros de campo los hacen los empleados.",
        "Si tienes dudas, toca el botón \"?\" arriba a la derecha en cualquier pantalla.",
    )

    // ---------------------------------------------------------------- Empleado
    private val cambiarPassword = ModuloAyuda(
        id = "password",
        titulo = "Cambiar contraseña",
        icono = Icons.Outlined.Key,
        queHace = "Cambia la contraseña con la que entras a la app.",
        cuando = "Cuando te dieron una contraseña provisional o crees que alguien más la conoce.",
        pasos = listOf(
            "Ve a Perfil y toca \"Cambiar contraseña\".",
            "Escribe tu contraseña actual.",
            "Escribe la nueva. Debe tener 8 caracteres o más, una letra y un número.",
            "Escríbela otra vez para confirmar y toca \"Actualizar contraseña\".",
        ),
        destino = AppDestination.CambiarPassword,
    )

    private val modulosEmpleado = listOf(
        ModuloAyuda(
            id = "registrar",
            titulo = "Registrar lote y QR",
            icono = IconosModulo.qr,
            queHace = "Da de alta un grupo nuevo de plantas y crea su código QR.",
            cuando = "Cada vez que siembras o recibes plantas nuevas.",
            pasos = listOf(
                "En Inicio o en la pestaña Lotes toca \"Registrar lote\".",
                "Elige la especie y la zona o invernadero.",
                "Escribe cuántas plantas son o usa los botones − y +.",
                "Revisa la zona y la fecha (por defecto, la zona de tu último lote y hoy) y toca \"Guardar lote\".",
                "Se abre el QR: guarda la imagen, imprímela y colócala junto al lote. Desde ahí puedes ver el lote o registrar otro.",
            ),
            destino = AppDestination.RegistrarLote,
        ),
        ModuloAyuda(
            id = "escanear",
            titulo = "Escanear QR",
            icono = IconosModulo.escanear,
            queHace = "Abre un lote con solo apuntar la cámara a su QR.",
            cuando = "Cuando estás frente a un lote y quieres registrar algo.",
            pasos = listOf(
                "Toca la pestaña \"Escanear\" en la barra de abajo.",
                "Apunta la cámara al QR. Si hay poca luz, enciende la linterna.",
                "Si el QR no se lee, toca \"Escribir código del lote\" y escribe el código.",
            ),
            destino = AppDestination.EscanerQr,
        ),
        ModuloAyuda(
            id = "detalle",
            titulo = "Detalle del lote",
            icono = IconosModulo.lote,
            queHace = "Muestra todo sobre un lote: etapa, salud, plantas disponibles y condiciones.",
            cuando = "Para revisar un lote y elegir qué registrar.",
            pasos = listOf(
                "Abre un lote desde Lotes o escaneando su QR.",
                "Revisa la etapa, la salud y las plantas disponibles.",
                "Toca uno de los botones grandes para registrar algo.",
            ),
            destino = AppDestination.ListaLotes,
        ),
        ModuloAyuda(
            id = "etapa",
            titulo = "Etapa de desarrollo",
            icono = IconosModulo.etapa,
            queHace = "Cambia la etapa del lote (por ejemplo, de Plántula a Floración) y su estado de salud.",
            cuando = "Cuando notas que las plantas pasaron a otra etapa.",
            pasos = listOf(
                "Abre el lote y toca \"Etapa\".",
                "Toca la nueva etapa en la línea.",
                "Elige el estado de salud y, si quieres, escribe una nota.",
                "Toca \"Actualizar etapa\".",
            ),
            destino = AppDestination.ListaLotes,
        ),
        ModuloAyuda(
            id = "observacion",
            titulo = "Observaciones",
            icono = IconosModulo.observacion,
            queHace = "Guarda lo que viste en el lote, con foto si hace falta.",
            cuando = "Cuando notas algo que vale la pena anotar.",
            pasos = listOf(
                "Abre el lote y toca \"Observación\".",
                "Escribe qué observaste.",
                "Elige el estado del cultivo y agrega una foto si quieres.",
                "Toca \"Guardar observación\".",
            ),
            destino = AppDestination.ListaLotes,
        ),
        ModuloAyuda(
            id = "actividad",
            titulo = "Actividades",
            icono = IconosModulo.actividad,
            queHace = "Registra el trabajo hecho: riego, poda, fertilización, trasplante, revisión o control de plagas.",
            cuando = "Justo después de terminar una tarea en el lote.",
            pasos = listOf(
                "Abre el lote y toca \"Actividad\".",
                "Toca el tipo de actividad.",
                "Agrega una observación si quieres. Tu nombre y la hora se ponen solos.",
                "Toca \"Registrar actividad\".",
            ),
            destino = AppDestination.ListaLotes,
        ),
        ModuloAyuda(
            id = "condiciones",
            titulo = "Condiciones",
            icono = IconosModulo.condiciones,
            queHace = "Muestra la humedad del sustrato y la temperatura que miden los sensores.",
            cuando = "Para saber si el lote necesita riego o protección.",
            pasos = listOf(
                "Abre el lote y toca \"Condiciones\".",
                "\"En rango\" (verde) significa que todo va bien; \"Fuera de rango\" (ámbar) significa que hay que actuar.",
                "Revisa la gráfica para ver cómo cambió en las últimas 24 horas.",
            ),
            destino = AppDestination.ListaLotes,
        ),
        ModuloAyuda(
            id = "anomalia",
            titulo = "Anomalías y recomendaciones",
            icono = IconosModulo.anomalia,
            queHace = "Reporta plagas, enfermedades u otros problemas y te dice qué hacer.",
            cuando = "En cuanto veas un problema en las plantas.",
            pasos = listOf(
                "Abre el lote y toca \"Anomalía\".",
                "Elige si es plaga, enfermedad u otra anomalía, y cuál es.",
                "Elige la severidad y describe lo que ves.",
                "Toca \"Registrar anomalía\". Verás las recomendaciones paso a paso.",
            ),
            destino = AppDestination.ListaLotes,
        ),
        ModuloAyuda(
            id = "bitacora",
            titulo = "Bitácora",
            icono = IconosModulo.bitacora,
            queHace = "Muestra el historial completo del lote, del más reciente al más antiguo.",
            cuando = "Para saber qué se ha hecho en el lote y quién lo hizo.",
            pasos = listOf(
                "Abre el lote y toca \"Bitácora\".",
                "Usa los filtros de arriba para ver un periodo o un tipo de registro.",
            ),
            destino = AppDestination.ListaLotes,
        ),
        ModuloAyuda(
            id = "salida",
            titulo = "Salidas (venta o merma)",
            icono = IconosModulo.salida,
            queHace = "Descuenta plantas del lote por venta o por pérdida (merma).",
            cuando = "Cuando se venden plantas o se pierden por plaga, daño u otro motivo.",
            pasos = listOf(
                "Abre el lote y toca \"Salida\".",
                "Elige Venta o Merma.",
                "Escribe la cantidad. No puede ser mayor a las plantas disponibles.",
                "Si es merma, elige el motivo.",
                "Toca \"Registrar venta\" o \"Registrar merma\", revisa el resumen y confirma.",
            ),
            destino = AppDestination.ListaLotes,
        ),
        cambiarPassword,
    )

    // ---------------------------------------------------------------- Administrador
    private val modulosAdmin = listOf(
        ModuloAyuda(
            id = "dashboard",
            titulo = "Dashboard",
            icono = IconosModulo.dashboard,
            queHace = "Muestra los indicadores del vivero: lotes activos, plantas disponibles, ventas y merma.",
            cuando = "Para revisar cómo va el negocio y detectar problemas.",
            pasos = listOf(
                "Toca \"Dashboard\" en la barra de abajo.",
                "Elige el periodo: Semana, Mes o Año.",
                "Toca cada indicador para ver cómo se calcula. Revisa las gráficas de ventas y merma por mes y la merma por especie.",
                "Abajo verás las anomalías abiertas que hay que atender.",
            ),
            destino = AppDestination.Dashboard,
        ),
        ModuloAyuda(
            id = "personal",
            titulo = "Trabajadores (alta y baja)",
            icono = IconosModulo.personal,
            queHace = "Agrega personas al equipo o les quita el acceso.",
            cuando = "Cuando alguien entra a trabajar o deja el vivero.",
            pasos = listOf(
                "Toca \"Personal\" en la barra de abajo.",
                "Para agregar, toca \"Agregar trabajador\" y llena nombre, correo, contraseña y rol.",
                "Usa \"Generar contraseña segura\" y compártela con la persona. Revisa el resumen y confirma.",
                "Para quitar el acceso, toca \"Dar de baja\" en su tarjeta y confirma.",
            ),
            destino = AppDestination.Trabajadores,
        ),
        ModuloAyuda(
            id = "supervision",
            titulo = "Supervisión de lotes",
            icono = IconosModulo.lote,
            queHace = "Consulta todos los lotes, su detalle y su bitácora sin poder modificarlos.",
            cuando = "Para revisar el trabajo de campo.",
            pasos = listOf(
                "Toca \"Lotes\" en la barra de abajo.",
                "Busca o filtra por especie y etapa.",
                "Toca un lote para ver su detalle y su bitácora.",
            ),
            destino = AppDestination.SupervisionLotes,
        ),
        ModuloAyuda(
            id = "auditoria",
            titulo = "Auditoría",
            icono = IconosModulo.auditoria,
            queHace = "Lista las acciones críticas: ventas, bajas de plantas y altas o bajas de usuarios.",
            cuando = "Para saber quién hizo qué y cuándo.",
            pasos = listOf(
                "En Dashboard toca el mosaico \"Auditoría\".",
                "Filtra por tipo de acción si buscas algo en particular.",
            ),
            destino = AppDestination.Auditoria,
        ),
        cambiarPassword,
    )

    // ---------------------------------------------------------------- Preguntas frecuentes
    private fun preguntas(rol: Rol) = listOf(
        PreguntaFrecuente(
            "qr",
            "¿Qué hago si el QR no se lee?",
            "Limpia la etiqueta y enciende la linterna. Si sigue sin leerse, toca \"Escribir código del lote\" " +
                "y escribe el código que está debajo del QR (empieza con GT-).",
        ),
        PreguntaFrecuente(
            "internet",
            "¿Funciona sin internet?",
            "Pronto podrás registrar sin señal: tus registros se guardarán en el teléfono y se enviarán solos " +
                "cuando vuelva la conexión. El aviso de Inicio te dice si hay registros pendientes.",
        ),
        PreguntaFrecuente(
            "corregir",
            "¿Puedo corregir un registro?",
            if (rol == Rol.EMPLEADO) {
                "Los registros no se borran para cuidar el historial. Haz un registro nuevo con el dato correcto " +
                    "y explica el cambio en la observación. Si es una venta o merma, avisa al administrador."
            } else {
                "Los registros no se borran para cuidar el historial. Pide al empleado un registro nuevo con el dato " +
                    "correcto. Las ventas y mermas quedan en Auditoría."
            },
        ),
        PreguntaFrecuente(
            "disponibles",
            "¿Por qué no puedo registrar más plantas de las disponibles?",
            "Porque no puedes vender ni perder plantas que el lote ya no tiene. Revisa la cantidad " +
                "\"Disponibles\" que aparece en la pantalla de salida.",
        ),
        PreguntaFrecuente(
            "olvide",
            "¿Qué hago si olvidé mi contraseña?",
            if (rol == Rol.EMPLEADO) {
                "En la pantalla de inicio de sesión toca \"¿Olvidaste tu contraseña?\": pide al administrador que la restablezca. Después cámbiala desde Perfil."
            } else {
                "Pide a otro administrador que te asigne una contraseña provisional. Después cámbiala desde Perfil."
            },
        ),
    )
}
