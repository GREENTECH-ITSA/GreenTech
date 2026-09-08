Documento de Especificación de Requerimientos del Sistema (ERS)
Nombre del proyecto: GreenTech

Nombre completo: Sistema Móvil, Multi-Plataforma e IoT para viveros

Área de aplicación: Viveros comerciales de la región de Cabrera en Atlixco, Puebla

1. Requerimientos Funcionales (RF)
Módulo 1: Autenticación, Usuarios y Control de Acceso (App Móvil Operativa)
RF-01. Inicio de Sesión (Login)

El sistema debe permitir al personal operativo y administradores autenticarse en la aplicación móvil utilizando sus credenciales (correo electrónico/usuario y contraseña).

RF-02. Cierre de Sesión (Logout)

El sistema debe permitir cerrar la sesión activa en la aplicación móvil, invalidando el token de acceso para proteger la información operativa en campo.

RF-03. Administración y Registro de Usuarios (CRUD)

El sistema debe permitir al rol Administrador registrar, consultar, actualizar roles y desactivar cuentas del personal de campo.

RF-04. Recuperación y Restablecimiento de Contraseña

El sistema debe permitir solicitar el restablecimiento de contraseña mediante el envío de un enlace con token temporal al correo registrado.

RF-05. Gestión de Perfil Operativo

El sistema debe permitir a los usuarios autenticados consultar y actualizar su información de contacto y cambiar su clave de acceso.

Módulo 2: Trazabilidad e Identificación por Código QR
RF-06. Generación e Impresión de Códigos QR Únicos

El sistema debe generar un código QR único e irrepetible para cada ejemplar o lote registrado en el vivero.

RF-07. Portal Web de Consulta Pública (Ficha Técnica para Cliente)

Al escanear el código QR desde cualquier navegador web sin necesidad de instalar una aplicación, el portal debe desplegar:

Nombre común y nombre científico del ejemplar.

Requerimientos hídricos, de luz y tipo de sustrato.

Nivel de toxicidad para mascotas.

Recomendaciones de cuidado post-compra.

RF-08. Vista Privada Operativa (App Móvil)

Al escanear el QR desde la aplicación móvil por un trabajador autenticado, el sistema debe abrir la bitácora operativa para registro de riego, fertilización, podas y síntomas.

Módulo 3: Telemetría e Infraestructura IoT de Riego Automatizado
RF-09. Parametrización de Riego por Especie y Sustrato

El sistema debe permitir definir esquemas y matrices de riego según la especie, etapa de desarrollo, tipo de sustrato y condiciones climáticas de Cabrera, Atlixco.

RF-10. Monitoreo de Humedad y Temperatura vía ESP32

El sistema IoT debe capturar periódicamente datos en tiempo real de humedad del sustrato (sensores capacitivos) y temperatura ambiental mediante microcontroladores ESP32.

RF-11. Activación Automatizada de Electroválvulas

El backend relacional y el motor inferencial deben enviar instrucciones a las electroválvulas para iniciar o detener el riego automáticamente cuando la humedad del sustrato caiga por debajo del umbral mínimo configurado para cada especie.

RF-12. Registro y Confirmación de Tareas de Campo

El personal operativo podrá confirmar tareas manuales de riego o fertilización en la app móvil mediante el escaneo del QR, almacenando automáticamente el ID del trabajador y marca de tiempo (timestamp).

RF-13. Tablero de Alertas de Mantenimiento e Inconsistencia Hídrica

El sistema debe generar alertas visuales y notificaciones sobre fallas en el riego, desviaciones en los niveles de humedad o retrasos en la atención del cultivo.

Módulo 4: Control de Madurez, Reporte de Síntomas y Protocolo de Descarte
RF-14. Semáforo de Estado de Salud y Comercialización

Cada planta o lote debe contar con un estado visual dentro de la plataforma:

Verde — Listo para Venta: Planta con desarrollo óptimo en altura, follaje y floración.

Amarillo — En Crecimiento / Cuarentena: Planta en desarrollo o bajo seguimiento preventivo.

Rojo — Baja / Descarte: Ejemplar irreversiblemente dañado o infectado.

RF-15. Registro de Síntomas en Campo

El personal operativo debe poder reportar desde la aplicación móvil los síntomas visibles de la planta (hojas amarillas, marchitez, machas o signos fúngicos) para actualizar de inmediato la bitácora del cultivo.

RF-16. Protocolo Automático de Aislamiento y Descarte

Al marcar una planta o lote en estado Rojo debido a hongos o plagas, el sistema debe emitir una orden prioritaria de retiro e instruir el aislamiento o descarte inmediato para evitar la propagación en el vivero.

RF-17. Proyección de Tiempo a Venta

El sistema debe calcular y mostrar la fecha estimada en la que el cultivo alcanzará el estado "Verde - Listo para Venta" en función de la fecha de siembra y especie.

Módulo 5: Backend Relacional, Motor Inferencial e Integración
RF-18. Centralización de Datos en Backend Relacional

El backend relacional debe coordinar e integrar la información de las lecturas IoT, solicitudes del portal web público, actividades de la app móvil y reglas de negocio.

RF-19. Motor Inferencial de Reglas de Cultivo

El sistema debe procesar las variables de humedad, temperatura y síntomas registrados mediante un motor inferencial para sugerir la estimación de venta, tiempos de descarte o ajustes en la frecuencia de riego.

RF-20. Habilitación Automática en Catálogo

Al cambiar el estado de un lote a Verde, el sistema debe habilitarlo automáticamente en el catálogo o punto de venta del vivero.

RF-21. Registro de Merma y Biomasa para Compostaje

El sistema debe permitir registrar el peso en kilogramos de la biomasa descartada (plantas en estado Rojo) para su contabilización y reutilización en procesos de compostaje interno.

2. Requerimientos No Funcionales (RNF)
Rendimiento y Escalabilidad
RNF-01. Tiempo de Respuesta en Escaneo QR

El despliegue de la ficha técnica pública en la web o de la bitácora operativa en la app móvil no debe demorar más de 2 segundos bajo redes móviles estándar (3G/4G/5G).

RNF-02. Capacidad de Ingesta Telemétrica IoT

El backend debe procesar lecturas continuas enviadas por múltiples microcontroladores ESP32 cada 5 a 15 minutos sin generar cuello de botella en la base de datos relacional.

Usabilidad y Accesibilidad
RNF-03. Diseño Responsivo y Móvil Primero

La interfaz de la app móvil para trabajadores y el portal web para clientes deben ser totalmente adaptables a teléfonos inteligentes y tabletas.

RNF-04. Acceso al Portal Público sin Instalación

El cliente debe acceder a la ficha técnica directamente desde el navegador web de su dispositivo móvil (Chrome, Safari, Firefox) sin requerir instalación previa de software.

RNF-05. Usabilidad Operativa en Campo

Las tareas operativas del trabajador (confirmar riego, registrar síntomas o cambiar estado) deben completarse en un máximo de 3 toques en la pantalla tras escanear el QR.

Seguridad y Control de Acceso
RNF-06. Control de Acceso Basado en Roles (RBAC)

El acceso a las funcionalidades del sistema debe estar segmentado:

Cliente: Acceso exclusivo de lectura al portal web público.

Personal Operativo (Trabajadores de campo): Acceso en la app móvil para lectura y registro de bitácoras, escaneo QR y síntomas.

Administrador: Control total de configuraciones, usuarios, parámetros IoT, motor inferencial y métricas globales.

RNF-07. Cifrado y Comunicaciones Seguras

Las contraseñas de los usuarios deben almacenarse con algoritmos de hashing seguros (Bcrypt/Argon2). Toda la transferencia de datos entre app, web, IoT y backend debe realizarse bajo protocolo HTTPS/TLS.

RNF-08. Autenticación por Tokens (JWT)

La app móvil debe gestionar la sesión mediante tokens JSON Web Tokens (JWT) con mecanismos de expiración y renovación segura.

RNF-09. Inmutabilidad de Registros de Trazabilidad

Los registros de riego, fertilización y marcas de tiempo (timestamps) generados en campo no podrán ser alterados ni borrados por el personal operativo para garantizar la trazabilidad de la producción.

Arquitectura Hardware y Entorno Físico
RNF-10. Integración y Tolerancia Hardware IoT

Los módulos embebidos basados en ESP32, sensores capacitivos de humedad y electroválvulas deben contar con comunicación tolerante a cortes temporales de red local, reintentando el envío de telemetría al reconectarse.

RNF-11. Durabilidad de Etiquetado QR

Las etiquetas físicas con códigos QR para el vivero deben imprimirse en materiales resistentes al agua, humedad constante, riego por aspersión y radiación UV directa.

RNF-12. Disponibilidad del Sistema

El backend relacional y la plataforma de servicios deben garantizar una disponibilidad de servicio del 99.5% durante el horario de operación del vivero.
