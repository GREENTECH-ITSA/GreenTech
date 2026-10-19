package com.greentech.app.ui.strings

/**
 * Textos de la interfaz centralizados. Los mensajes de error y éxito del dominio
 * viven en shared (AppError y Mensajes) para que el texto sea el mismo en todos lados.
 *
 * Reglas de redacción: mayúscula solo al inicio (sentence case), tuteo, frases cortas,
 * botones con verbo + objeto y errores que dicen qué pasó y cómo arreglarlo.
 * Los textos con %s / %d se completan con [conValores].
 */
object Textos {
    const val APP = "GreenTech"

    object Comun {
        const val VOLVER = "Volver"
        const val AYUDA = "Ayuda"
        const val CANCELAR = "Cancelar"
        const val CERRAR = "Cerrar"
        const val REINTENTAR = "Reintentar"
        const val TODOS = "Todos"
        const val TODAS = "Todas"
        const val NOTA_OBLIGATORIO = "* Obligatorio"
        const val HOY = "Hoy"
        const val AYER = "Ayer"
        const val ELEGIR = "Elegir"
        const val BUSCAR = "Buscar"
        const val SIN_RESULTADOS = "Sin resultados. Intenta con otra palabra."
        const val CARGANDO = "Cargando"
        const val LOGO = "Logo de GreenTech"
        const val COMPLETA_OBLIGATORIOS = "Completa los campos obligatorios para continuar:"
        const val MOSTRAR_PASSWORD = "Mostrar contraseña"
        const val OCULTAR_PASSWORD = "Ocultar contraseña"
        const val SOLO_LECTURA = "Solo lectura"
        const val ERROR_GENERICO = "Algo salió mal"
        const val SELECCIONADO = "Seleccionado"
        const val DISMINUIR = "Disminuir"
        const val AUMENTAR = "Aumentar"
        const val CAMBIAR_FECHA = "Cambiar fecha"
        const val ELEGIR_FECHA = "Elegir fecha"
        const val PLANTAS = "plantas"
        const val PLANTA = "planta"
        const val RESPONSABLE = "Responsable"
        const val ENTENDIDO = "Entendido"
        const val MAS_INFO = "Más información"
        const val VER_MAS_AYUDA = "Ver más en Ayuda"
        const val DESCARTAR_TITULO = "¿Descartar cambios?"
        const val DESCARTAR_MENSAJE = "Si sales ahora, se perderá lo que llenaste en este formulario."
        const val DESCARTAR = "Descartar"
        const val SEGUIR_EDITANDO = "Seguir editando"
        const val LIMPIAR_FILTROS = "Limpiar filtros"
        const val LIMPIAR_BUSQUEDA = "Borrar búsqueda"
        const val FILTRO_ACTIVO = "1 filtro activo"
        const val FILTROS_ACTIVOS = "%d filtros activos"
        const val STEPPER_AYUDA = "Escribe el número o usa los botones − y +."
        const val REGISTRADO_AHORA = "Registrado ahora"
        const val ACTUALIZADO_AHORA = "Actualizado ahora"
    }

    object Navegacion {
        const val INICIO = "Inicio"
        const val LOTES = "Lotes"
        const val ESCANEAR = "Escanear"
        const val PERFIL = "Perfil"
        const val DASHBOARD = "Dashboard"
        const val PERSONAL = "Personal"
    }

    object Sync {
        const val SINCRONIZADO = "Todo sincronizado"
        const val SIN_CONEXION = "Sin conexión"
        const val PENDIENTES = "registros por enviar"
        const val SINCRONIZANDO = "Sincronizando…"
        const val SINCRONIZAR = "Sincronizar ahora"
        const val ULTIMA = "Última sincronización"
        const val COMPLETA = "Sincronización completa"
    }

    object Splash {
        const val LEMA = "Gestión inteligente de viveros"
    }

    object Login {
        const val TITULO = "Inicia sesión"
        const val CORREO = "Correo"
        const val CORREO_EJEMPLO = "Ej. nombre@gmail.com"
        const val PASSWORD = "Contraseña"
        const val ENTRAR = "Iniciar sesión"
        const val ENTRANDO = "Iniciando sesión…"
        const val BLOQ_MAYUS = "Bloq Mayús activado"
        const val OLVIDE = "¿Olvidaste tu contraseña?"
        const val OLVIDE_MENSAJE = "Pide al administrador que restablezca tu contraseña."
    }

    object Perfil {
        const val TITULO = "Mi perfil"
        const val CUENTA = "Cuenta"
        const val CAMBIAR_PASSWORD = "Cambiar contraseña"
        const val CAMBIAR_PASSWORD_DESC = "Actualiza la contraseña con la que entras"
        const val AYUDA_SOPORTE = "Ayuda y soporte"
        const val AYUDA = "Centro de ayuda"
        const val AYUDA_DESC = "Guías paso a paso y preguntas frecuentes"
        const val INTRO = "Ver introducción"
        const val INTRO_DESC = "Repasa en 3 pasos cómo usar GreenTech"
        const val ACERCA_DE = "Acerca de"
        const val ACERCA_DESC = "Lleva el control de los lotes del vivero: registro, seguimiento, salidas e indicadores en un solo lugar."
        const val CERRAR_SESION = "Cerrar sesión"
        const val CONFIRMAR_SALIR_TITULO = "¿Cerrar sesión?"
        const val CONFIRMAR_SALIR_MENSAJE = "Se cerrará tu sesión en este equipo. Para volver a entrar necesitarás tu correo y contraseña."
        const val VERSION = "GreenTech · v1.0.0"
        const val ROL = "Rol"
    }

    object Password {
        const val TITULO = "Cambiar contraseña"
        const val ACTUAL = "Contraseña actual"
        const val NUEVA = "Nueva contraseña"
        const val CONFIRMAR = "Confirma la nueva contraseña"
        const val REQUISITOS = "Tu nueva contraseña debe tener:"
        const val REQ_LONGITUD = "8 caracteres o más"
        const val REQ_LETRA = "Al menos una letra"
        const val REQ_NUMERO = "Al menos un número"
        const val CUMPLIDO = "Cumplido"
        const val PENDIENTE = "Pendiente"
        const val GUARDAR = "Actualizar contraseña"
    }

    object Inicio {
        const val HOLA = "Hola"
        const val NUEVO_LOTE = "Registrar lote"
        const val ESCANEAR = "Escanear QR"
        const val ALERTAS = "Alertas de sensores"
        const val SIN_ALERTAS = "Todas las zonas están en rango"
        const val SIN_ACTIVIDAD = "Aún no hay actividad registrada"
        const val MINIMO_IDEAL = "Mínimo ideal:"
        const val MAXIMO_IDEAL = "Máximo ideal:"
        const val AFECTA = "Afecta a"
        const val ALERTAS_MAS = "y %d más"
    }

    object Lotes {
        const val TITULO = "Lotes"
        const val TITULO_SUPERVISION = "Supervisión de lotes"
        const val BUSCAR = "Buscar por especie o código"
        const val ESPECIE = "Especie"
        const val ETAPA = "Etapa"
        const val DISPONIBLES = "disponibles"
        const val DE = "de"
        const val VACIO_TITULO = "Aún no hay lotes"
        const val VACIO_MENSAJE = "Registra tu primer lote para empezar a darle seguimiento."
        const val VACIO_MENSAJE_SUPERVISION = "Cuando el equipo registre lotes aparecerán aquí."
        const val SIN_COINCIDENCIAS_BUSQUEDA = "No hay lotes que coincidan con “%s”"
        const val SIN_COINCIDENCIAS_FILTROS = "No hay lotes con los filtros elegidos"
        const val SIN_COINCIDENCIAS_MENSAJE = "Intenta con otra búsqueda o limpia los filtros."
        const val REGISTRAR_PRIMERO = "Registrar primer lote"
        const val NUEVO = "Registrar lote"
        const val RESULTADO = "1 lote"
        const val RESULTADOS = "%d lotes"
        const val DISPONIBLES_DE = "%s de %s plantas disponibles"
    }

    object RegistrarLote {
        const val TITULO = "Registrar lote"
        const val ESPECIE = "Especie"
        const val ESPECIE_PLACEHOLDER = "Elige la especie"
        const val BUSCAR_ESPECIE = "Buscar especie"
        const val ZONA = "Zona o invernadero"
        const val ZONA_AYUDA = "Elegimos la zona de tu último lote. Cámbiala si hace falta."
        const val CANTIDAD = "Cantidad inicial de plantas"
        const val CANTIDAD_EJEMPLO = "Ej. 120"
        const val FECHA = "Fecha de siembra"
        const val GUARDAR = "Guardar lote"
        const val FALTA_ESPECIE = "Especie"
        const val FALTA_ZONA = "Zona"
        const val FALTA_CANTIDAD = "Cantidad"
    }

    object Qr {
        const val TITULO = "QR del lote"
        const val INSTRUCCION = "Imprime este código y colócalo junto al lote."
        const val LISTO_TITULO = "Lote registrado"
        const val LISTO_MENSAJE = "Guarda la imagen del QR, imprímela y colócala junto al lote."
        const val GUARDAR = "Guardar imagen"
        const val COMPARTIR = "Compartir QR"
        const val VER_LOTE = "Ver lote"
        const val OTRO_LOTE = "Registrar otro lote"
        const val GUARDADO = "QR guardado en la galería"
        const val COMPARTIDO = "Elige dónde compartir el QR"
        const val DESCRIPCION = "Código QR del lote"
        const val SEMBRADO = "Sembrado el"
    }

    object Escaner {
        const val TITULO = "Escanear QR"
        const val INSTRUCCION = "Apunta la cámara al código QR del lote"
        const val SIN_CAMARA = "La cámara no está disponible en este equipo. Escribe el código impreso debajo del QR."
        const val LINTERNA_ON = "Encender linterna"
        const val LINTERNA_OFF = "Apagar linterna"
        const val MANUAL = "Escribir código del lote"
        const val CODIGO = "Código del lote"
        const val CODIGO_EJEMPLO = "Ej. GT-8F2A91C0D4"
        const val BUSCAR = "Abrir lote"
        const val BUSCANDO = "Buscando lote…"
        const val MANUAL_TITULO = "Escribe el código del lote"
        const val MANUAL_AYUDA = "Está impreso debajo del QR. Empieza con GT-"
        const val FORMATO_INVALIDO = "Revisa el código: empieza con GT- y lleva 10 letras o números. Ej. GT-8F2A91C0D4."
        const val NO_ENCONTRADO = "No encontramos un lote con el código %s. Revisa que esté bien escrito."
        const val LOTE_NO_ENCONTRADO = "Lote no encontrado"
        const val CODIGO_LEIDO = "Código leído"
        const val ZOOM = "Cambiar zoom"
    }

    object Detalle {
        const val TITULO = "Detalle del lote"
        const val DISPONIBLES = "Plantas disponibles"
        const val ZONA = "Zona"
        const val SIEMBRA = "Siembra"
        const val HUMEDAD = "Humedad"
        const val TEMPERATURA = "Temperatura"
        const val LECTURA = "Lectura de las"
        const val ACCIONES = "¿Qué quieres registrar?"
        const val ETAPA = "Etapa"
        const val OBSERVACION = "Observación"
        const val ACTIVIDAD = "Actividad"
        const val CONDICIONES = "Condiciones"
        const val ANOMALIA = "Anomalía"
        const val RECOMENDACIONES = "Recomendaciones"
        const val BITACORA = "Bitácora"
        const val SALIDA = "Salida"
        const val SALIDA_DESC = "Venta o merma"
        const val VER_QR = "Ver QR del lote"
        const val SOLO_LECTURA_AVISO = "Estás viendo este lote en modo supervisión. No se pueden hacer registros."
    }

    object Etapa {
        const val TITULO = "Cambiar etapa"
        const val LINEA = "Elige la nueva etapa"
        const val ACTUAL = "Actual"
        const val ETAPA_ACTUAL = "Etapa actual: %s"
        const val SALUD = "Estado de salud"
        const val NOTA = "Nota"
        const val NOTA_EJEMPLO = "Ej. brotes nuevos en todo el lote"
        const val GUARDAR = "Actualizar etapa"
        const val FALTA_ETAPA = "Nueva etapa"
        const val INFO_TITULO = "¿Qué es la etapa?"
        const val INFO = "La etapa dice en qué momento del crecimiento está el lote, de semilla a lista para venta. " +
            "Elige la etapa a la que pasaron las plantas; la actual está marcada como \"Actual\"."
    }

    object Permisos {
        const val TITULO = "Permisos"
        const val PERMITIR = "Permitir"
        const val AHORA_NO = "Ahora no"
        const val ABRIR_AJUSTES = "Abrir ajustes"
        const val PERMITIDO = "Permitido"
        const val NO_PERMITIDO = "No permitido"
        const val CAMARA = "Cámara"
        const val CAMARA_TITULO = "Usar la cámara"
        const val CAMARA_MENSAJE = "Para escanear el QR de los lotes, GreenTech necesita usar la cámara."
        const val CAMARA_FOTO_MENSAJE = "Para tomar fotos de tus observaciones, GreenTech necesita usar la cámara."
        const val SIN_CAMARA_TITULO = "Sin acceso a la cámara"
        const val SIN_CAMARA_MENSAJE = "Permite la cámara para escanear o escribe el código impreso debajo del QR."
        const val SIN_CAMARA_AJUSTES = "Activa la cámara en los ajustes del teléfono o escribe el código impreso debajo del QR."
        const val PERMITIR_CAMARA = "Permitir cámara"
        const val NOTIFICACIONES = "Notificaciones"
        const val NOTIFICACIONES_TITULO = "Activar notificaciones"
        const val NOTIFICACIONES_MENSAJE = "Te avisaremos si la humedad o la temperatura de un lote salen de rango."
        const val CAMBIAR_EN_AJUSTES = "Toca para cambiarlo en los ajustes del teléfono"
    }

    object Observacion {
        const val TITULO = "Nueva observación"
        const val TEXTO = "¿Qué observaste?"
        const val TEXTO_EJEMPLO = "Ej. hojas con manchas en la orilla"
        const val TEXTO_ERROR = "Describe lo que observaste en el lote."
        const val ESTADO = "Estado del cultivo"
        const val FOTO = "Agregar foto"
        const val FOTO_AGREGADA = "Foto agregada"
        const val QUITAR_FOTO = "Quitar foto"
        const val FOTO_TITULO = "Agregar foto"
        const val TOMAR_FOTO = "Tomar foto"
        const val ELEGIR_GALERIA = "Elegir de la galería"
        const val FOTO_DESCRIPCION = "Foto de la observación"
        const val GUARDAR = "Guardar observación"
        const val FALTA_TEXTO = "Descripción"
    }

    object Actividad {
        const val TITULO = "Registrar actividad"
        const val TIPO = "Tipo de actividad"
        const val OBSERVACION = "Observación"
        const val OBSERVACION_EJEMPLO = "Ej. se regó todo el lote por la mañana"
        const val GUARDAR = "Registrar actividad"
        const val FALTA_TIPO = "Tipo de actividad"
        const val AUTOMATICO = "Se registra a tu nombre con la fecha y hora actuales"
    }

    object Condiciones {
        const val TITULO = "Condiciones"
        const val HUMEDAD = "Humedad del sustrato"
        const val TEMPERATURA = "Temperatura"
        const val IDEAL = "Ideal"
        const val EN_RANGO = "En rango"
        const val BAJO = "Bajo"
        const val ALTO = "Alto"
        const val ULTIMAS_24 = "Últimas 24 horas"
        const val ULTIMA_LECTURA = "Última lectura"
        const val RANGO_IDEAL = "Rango ideal"
        const val HACE_24 = "Hace 24 h"
        const val AHORA = "Ahora"
        const val GRAFICA_HUMEDAD = "Gráfica de humedad de las últimas 24 horas"
        const val GRAFICA_TEMPERATURA = "Gráfica de temperatura de las últimas 24 horas"
        const val VACIO = "Aún no hay lecturas"
        const val VACIO_DESC = "Los sensores de esta zona todavía no envían datos."
    }

    object Anomalia {
        const val TITULO = "Reportar anomalía"
        const val TIPO = "¿Qué encontraste?"
        const val OPCION = "Elige cuál"
        const val SEVERIDAD = "Severidad"
        const val DESCRIPCION = "Descripción"
        const val DESCRIPCION_EJEMPLO = "Ej. se ve en 20 plantas del lado norte"
        const val DESCRIPCION_ERROR = "Describe lo que ves para que el equipo pueda atenderlo."
        const val GUARDAR = "Registrar anomalía"
        const val FALTA_TIPO = "Tipo"
        const val FALTA_OPCION = "Problema"
        const val FALTA_SEVERIDAD = "Severidad"
        const val FALTA_DESCRIPCION = "Descripción"
        const val INFO_TITULO = "¿Qué es una anomalía?"
        const val INFO = "Es cualquier problema en las plantas: plagas, enfermedades u otros daños. " +
            "Al registrarla verás de inmediato las recomendaciones para atenderla."
    }

    object Recomendaciones {
        const val TITULO = "Recomendaciones"
        const val SUBTITULO = "Sigue estos pasos para cuidar el lote"
        const val VACIO = "Sin recomendaciones por ahora"
        const val VACIO_DESC = "Este lote no tiene anomalías abiertas y sus condiciones están en rango."
        const val PASOS = "Pasos sugeridos"
        const val VOLVER_LOTE = "Volver al lote"
    }

    object Bitacora {
        const val TITULO = "Bitácora"
        const val PERIODO = "Periodo"
        const val TIPO = "Tipo de registro"
        const val TODO_EL_HISTORIAL = "Todo"
        const val SIETE_DIAS = "7 días"
        const val TREINTA_DIAS = "30 días"
        const val VACIO = "Aún no hay registros"
        const val VACIO_DESC = "Cuando registres actividades o cambios aparecerán aquí."
        const val SIN_COINCIDENCIAS = "No hay registros con los filtros elegidos"
        const val POR = "Por"
        const val REGISTRO = "1 registro"
        const val REGISTROS = "%d registros"
    }

    object Salida {
        const val TITULO = "Registrar salida"
        const val TIPO = "Tipo de salida"
        const val VENTA = "Venta"
        const val VENTA_DESC = "Plantas vendidas"
        const val MERMA = "Merma"
        const val MERMA_DESC = "Plantas perdidas o descartadas"
        const val CANTIDAD = "Cantidad de plantas"
        const val CANTIDAD_EJEMPLO = "Ej. 20"
        const val DISPONIBLES = "Disponibles"
        const val DISPONIBLES_AYUDA = "Disponibles: %s plantas. Escribe el número o usa − y +."
        const val MOTIVO = "Motivo de la merma"
        const val FECHA = "Fecha de salida"
        const val GUARDAR_VENTA = "Registrar venta"
        const val GUARDAR_MERMA = "Registrar merma"
        const val GUARDAR = "Registrar salida"
        const val CONFIRMAR_TITULO_VENTA = "¿Registrar la venta?"
        const val CONFIRMAR_TITULO_MERMA = "¿Registrar la merma?"
        const val CONFIRMAR_MENSAJE_VENTA = "Se registrará la venta de %d plantas de %s. No se puede deshacer."
        const val CONFIRMAR_MENSAJE_MERMA = "Se registrará la merma de %d plantas de %s. No se puede deshacer."
        const val RESUMEN_LOTE = "Lote"
        const val RESUMEN_TIPO = "Tipo"
        const val RESUMEN_CANTIDAD = "Cantidad"
        const val RESUMEN_MOTIVO = "Motivo"
        const val RESUMEN_FECHA = "Fecha"
        const val RESUMEN_QUEDAN = "Quedarán disponibles"
        const val FALTA_TIPO = "Tipo de salida"
        const val FALTA_CANTIDAD = "Cantidad válida"
        const val FALTA_MOTIVO = "Motivo"
        const val INFO_TITULO = "Venta y merma"
        const val INFO = "Una salida descuenta plantas del lote. Venta: plantas vendidas. Merma: plantas perdidas o " +
            "descartadas por plaga, daño u otro motivo. No puedes registrar más plantas de las disponibles."
    }

    object Dashboard {
        const val TITULO = "Dashboard"
        const val INDICADORES = "Indicadores"
        const val LOTES_ACTIVOS = "Lotes activos"
        const val DISPONIBLES = "Plantas disponibles"
        const val VENDIDAS = "Plantas vendidas"
        const val PORC_MERMA = "% de merma"
        const val MERMA = "Merma"
        const val MERMA_DESC = "Plantas perdidas o descartadas"
        const val VENTAS_VS_MERMA = "Ventas y merma por mes"
        const val VENTAS = "Ventas"
        const val MERMA_ESPECIE = "Merma por especie"
        const val ANOMALIAS = "Anomalías abiertas"
        const val SIN_ANOMALIAS = "No hay anomalías abiertas"
        const val SIN_MERMA = "Sin merma en el periodo"
        const val VACIO = "Aún no hay información"
        const val VACIO_DESC = "Cuando el equipo registre lotes y salidas, aquí verás los indicadores."
        const val ACCESOS = "Accesos rápidos"
        const val AUDITORIA = "Auditoría"
        const val AUDITORIA_DESC = "Revisa las acciones críticas"
        const val AGREGAR = "Agregar trabajador"
        const val SUPERVISAR = "Supervisar lotes"
        const val GRAFICA_BARRAS = "Gráfica de barras de ventas y merma de los últimos 6 meses"
        const val GRAFICA_ESPECIES = "Gráfica de merma por especie"
        const val TOCA_KPI = "Toca para ver cómo se calcula"
        const val INFO_INDICADORES = "Son los números clave del vivero. Elige Semana, Mes o Año para ver las ventas y la " +
            "merma de ese periodo. Toca cada indicador para saber cómo se calcula."
        const val INFO_LOTES = "Lotes que todavía tienen plantas disponibles. Es el valor de hoy: no cambia con el periodo."
        const val INFO_DISPONIBLES = "Suma de las plantas disponibles de todos los lotes. Es el valor de hoy: no cambia con el periodo."
        const val INFO_VENDIDAS = "Plantas registradas como venta dentro del periodo elegido. La línea muestra los últimos 6 meses."
        const val INFO_MERMA = "% merma = plantas perdidas ÷ plantas que salieron (ventas + merma) en el periodo. " +
            "Entre más bajo, mejor."
    }

    object Personal {
        const val TITULO = "Personal"
        const val BUSCAR = "Buscar por nombre o correo"
        const val ACTIVOS = "Activos"
        const val INACTIVOS = "Inactivos"
        const val AGREGAR = "Agregar trabajador"
        const val DAR_BAJA = "Dar de baja"
        const val INACTIVO = "Inactivo"
        const val ALTA = "Alta"
        const val CONFIRMAR_TITULO = "¿Dar de baja a %s?"
        const val CONFIRMAR_MENSAJE = "%s ya no podrá iniciar sesión en GreenTech. Su historial se conserva."
        const val VACIO_ACTIVOS = "No hay trabajadores activos"
        const val VACIO_INACTIVOS = "No hay trabajadores inactivos"
        const val VACIO_DESC = "Agrega a tu equipo para que pueda usar la app."
        const val VACIO_INACTIVOS_DESC = "Aquí aparecerán las personas que des de baja."
        const val SIN_COINCIDENCIAS = "Nadie coincide con “%s”"
        const val SIN_COINCIDENCIAS_DESC = "Revisa el nombre o el correo, o borra la búsqueda."
        const val TU = "Tú"
    }

    object AltaTrabajador {
        const val TITULO = "Agregar trabajador"
        const val NOMBRE = "Nombre completo"
        const val NOMBRE_EJEMPLO = "Ej. María Hernández"
        const val CORREO = "Correo"
        const val PASSWORD = "Contraseña provisional"
        const val GENERAR = "Generar contraseña segura"
        const val PASSWORD_AYUDA = "8 caracteres o más, con letras y números. Compártela con el trabajador; podrá cambiarla en su perfil."
        const val ROL = "Rol"
        const val GUARDAR = "Agregar trabajador"
        const val FALTA_NOMBRE = "Nombre"
        const val FALTA_CORREO = "Correo"
        const val FALTA_PASSWORD = "Contraseña válida"
        const val FALTA_ROL = "Rol"
        const val CONFIRMAR_TITULO = "¿Agregar a %s?"
        const val CONFIRMAR_MENSAJE = "Podrá iniciar sesión con este correo y la contraseña provisional."
    }

    object Auditoria {
        const val TITULO = "Auditoría"
        const val SUBTITULO = "Ventas, mermas, altas y bajas de personal, de la más reciente a la más antigua."
        const val VACIO = "Aún no hay registros de auditoría"
        const val VACIO_DESC = "Las ventas, mermas y movimientos de personal aparecerán aquí."
        const val SIN_COINCIDENCIAS = "No hay registros de este tipo"
    }

    object Ayuda {
        const val TITULO = "Centro de ayuda"
        const val SUBTITULO = "Encuentra cómo usar cada parte de la app"
        const val BUSCAR = "Buscar en la ayuda"
        const val PRIMEROS_PASOS = "Primeros pasos"
        const val MODULOS = "Guía por módulo"
        const val FAQ = "Preguntas frecuentes"
        const val QUE_HACE = "Qué hace"
        const val CUANDO = "Cuándo usarlo"
        const val PASOS = "Pasos"
        const val IR = "Ir al módulo"
        const val SIN_RESULTADOS = "No encontramos nada con “%s”"
        const val SIN_RESULTADOS_DESC = "Intenta con otra palabra, por ejemplo \"QR\" o \"merma\"."
        const val EXPANDIR = "Mostrar detalle"
        const val CONTRAER = "Ocultar detalle"
        const val VER_INTRO = "Ver introducción"
    }

    object Intro {
        const val SIGUIENTE = "Siguiente"
        const val OMITIR = "Omitir"
        const val EMPEZAR = "Empezar a usar GreenTech"
        const val PASO = "Paso %d de %d"

        const val EMPLEADO_1_TITULO = "Registra tus lotes"
        const val EMPLEADO_1 = "Da de alta cada grupo de plantas con su especie, zona y cantidad. Al guardar se crea su código QR."
        const val EMPLEADO_2_TITULO = "Escanea el QR para ver todo"
        const val EMPLEADO_2 = "Apunta al QR del lote, o escribe su código, y abre su etapa, salud, plantas disponibles y condiciones."
        const val EMPLEADO_3_TITULO = "Lleva la bitácora del día"
        const val EMPLEADO_3 = "Registra riegos, observaciones, anomalías y salidas. Todo queda en la bitácora del lote."

        const val ADMIN_1_TITULO = "Revisa los indicadores"
        const val ADMIN_1 = "En Dashboard ves lotes activos, plantas disponibles, ventas y merma. Toca cada número para saber cómo se calcula."
        const val ADMIN_2_TITULO = "Gestiona a tu equipo"
        const val ADMIN_2 = "En Personal agregas trabajadores o les quitas el acceso. Cada cambio queda en Auditoría."
        const val ADMIN_3_TITULO = "Supervisa los lotes"
        const val ADMIN_3 = "En Lotes consultas el detalle, las condiciones y la bitácora de cada lote, sin modificar nada."
    }

    object Apariencia {
        const val TITULO = "Apariencia"
        const val TEMA = "Tema"
        const val SISTEMA = "Sistema"
        const val CLARO = "Claro"
        const val OSCURO = "Oscuro"
        const val REDUCIR = "Reducir animaciones"
        const val REDUCIR_DESC = "Quita transiciones y movimientos en bucle"
    }

    object Notificacion {
        const val EXITO = "¡Listo!"
        const val ERROR = "No se pudo completar"
        const val ADVERTENCIA = "Atención"
        const val INFO = "Aviso"
    }

    object Disenio {
        const val VS_ANTERIOR = "vs periodo anterior"
        const val SIN_CAMBIO = "Sin cambio"
        const val MAS_ACCIONES = "Más acciones"
        const val CERRAR_MENU = "Cerrar menú"
        const val MENU_ABIERTO = "Abierto"
        const val MENU_CERRADO = "Cerrado"
        const val GUARDADO = "Guardado"
        const val FUERA_DE_RANGO = "Fuera de rango"
        const val DISPONIBLES_DE = "disponibles de"
        const val SALUD = "Salud"
        const val ULTIMA_ACTIVIDAD = "Última actividad"
        const val CONDICIONES_ZONA = "Condiciones de la zona"
        const val LOTES_ACTIVOS = "Lotes activos"
        const val ANOMALIAS_ABIERTAS = "Anomalías abiertas"
        const val EN_TU_ZONA = "en seguimiento"
        const val POR_ATENDER = "por atender"
        const val QUE_CONSULTAR = "¿Qué quieres consultar?"
        const val SIN_DATOS = "Sin datos"
        const val TOCA_PARA_VER = "Toca para ver detalle"
    }

    /** Indicador de salud vegetal y su leyenda. */
    object Salud {
        const val TITULO = "Salud de las plantas"
        const val VER_LEYENDA = "Ver qué significa cada estado de salud"
        const val LEYENDA_INTRO = "Cada estado tiene su color, su ícono y su forma, para reconocerlo aunque no se distinga el color."
        const val SANA = "Sin daños visibles. Sigue el cuidado normal."
        const val EN_OBSERVACION = "Algo llamó la atención. Revísala en los próximos días."
        const val AFECTADA = "Tiene plaga, enfermedad o daño. Necesita tratamiento."
        const val CRITICA = "Riesgo de perder plantas. Atiéndela de inmediato."
        const val FORMA_CIRCULO = "Círculo lleno"
        const val FORMA_CIRCULO_BORDE = "Círculo con borde"
        const val FORMA_TRIANGULO = "Triángulo"
        const val FORMA_ROMBO = "Rombo"
    }

    /** Indicadores de la tarjeta de lote (los datos llegan en la Parte 2). */
    object LoteCard {
        const val TAREA_PENDIENTE = "1 tarea pendiente"
        const val TAREAS_PENDIENTES = "%d tareas pendientes"
        const val EN_TRANSITO = "En tránsito"
        const val PLANTAS_EN_TRANSITO = "%d plantas en tránsito"
        const val ULTIMA_ACTIVIDAD = "Última actividad"
    }
}

/** Completa los marcadores %s y %d de un texto, en orden: "Paso %d de %d".conValores(1, 3). */
fun String.conValores(vararg valores: Any): String {
    var resultado = this
    valores.forEach { v ->
        val i = Regex("%[sd]").find(resultado) ?: return resultado
        resultado = resultado.replaceRange(i.range, v.toString())
    }
    return resultado
}
