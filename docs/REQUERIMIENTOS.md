Documento de Especificación de Requerimientos del Sistema (ERS)
Proyecto: GreenTech

Sistema Móvil, Multi-Plataforma e IoT para Viveros

1. Requerimientos Funcionales (RF)
Los requerimientos funcionales definen lo que el sistema debe hacer directamente para resolver las problemáticas de pérdida de inventario, riego ineficiente, falta de trazabilidad y desinformación en los viveros comerciales de Cabrera en Atlixco, Puebla.

Módulo 1: Autenticación, Usuarios y Control de Acceso
RF-01. Inicio de Sesión (Login)

El sistema debe permitir a los usuarios registrados (Personal Operativo y Administradores) autenticarse mediante credenciales (correo electrónico/nombre de usuario y contraseña).

RF-02. Cierre de Sesión (Logout)

El sistema debe permitir cerrar la sesión activa de forma segura desde la aplicación móvil o panel administrativo, invalidando el token actual para evitar accesos no autorizados.

RF-03. Administración y Registro de Usuarios (CRUD)

El sistema debe permitir al usuario con rol Administrador registrar, consultar, editar datos/roles y activar o desactivar cuentas de usuarios del personal del vivero.

RF-04. Recuperación y Restablecimiento de Contraseña

El sistema debe permitir a los usuarios solicitar la recuperación de su contraseña mediante el envío de un enlace seguro con token de tiempo limitado a su correo electrónico.

RF-05. Gestión de Perfil de Usuario

El sistema debe permitir a los usuarios autenticados consultar su información personal, actualizar datos de contacto y cambiar su contraseña de acceso.

Módulo 2: Identificación y Acceso por Código QR
RF-06. Generación de Códigos QR Dinámicos

El sistema debe generar un código QR único asignable a una maceta individual o a un lote completo de cultivo desde el registro inicial.

RF-07. Portal Web de Consulta Pública (Ficha Técnica para Cliente)

Al escanear el QR sin autenticación previa desde cualquier navegador móvil, el portal web debe desplegar una página informativa que contenga:

Nombre común y nombre científico de la planta.

Requerimientos de luz y frecuencia de riego.

Nivel de toxicidad para mascotas.

Recomendaciones post-compra.

RF-08. Vista Privada en App Móvil (Panel Operativo para Personal)

Al escanear el QR desde la aplicación móvil por un usuario autenticado, el sistema debe desplegar la bitácora operativa permitiendo registrar:

Eventos de riego y fertilización.

Registro de podas, reportes de síntomas y cambios de sustrato o maceta.

Cambio manual o automático de estado de salud.

Módulo 3: Telemetría e Infraestructura IoT de Riego Automatizado
RF-09. Parametrización de Riego por Especie

El sistema debe permitir definir esquemas y matrices de riego según la especie, tipo de sustrato, etapa de desarrollo y época del año.

RF-10. Monitoreo Telemétrico IoT (ESP32 y Sensores Capacitivos)

El sistema debe recibir y almacenar en tiempo real los datos transmitidos por microcontroladores ESP32 con sensores capacitivos de humedad de suelo y sensores de temperatura ambiental.

RF-11. Activación Automatizada de Electroválvulas y Alertas

El sistema debe enviar instrucciones para activar electroválvulas de riego de forma automática cuando la humedad del sustrato caiga por debajo del umbral tolerado, y emitir alertas visuales al personal sobre inconsistencias hídricas o fallas en el riego.

Módulo 4: Monitoreo de Madurez y Protocolo de Descarte
RF-12. Semáforo de Estado de Salud y Comercialización

Cada planta o lote debe contar con un estado visual de salud:

Verde — Listo para Venta: Cumple con los parámetros de madurez, altura, follaje y floración.

Amarillo — En Crecimiento / Cuarentena: En desarrollo normal o bajo seguimiento por estrés hídrico o daño leve.

Rojo — Baja / Descarte: Pérdida por daño irreversible o muerte del ejemplar.

RF-13. Publicación Automática en Catálogo

Al cambiar el estado a Verde, el sistema debe publicar o habilitar la planta en el inventario o catálogo del punto de venta de forma automática.

RF-14. Protocolo Automático de Aislamiento y Descarte

Al marcar un lote o planta en Rojo, el sistema debe generar una orden de retiro inmediato dirigida al personal operativo para aislamiento o descarte rápido, evitando el desperdicio de recursos de producción.

RF-15. Proyección de Tiempo a Venta

El sistema debe calcular y mostrar la fecha estimada en que la planta alcanzará el estado "Listo para Venta" basándose en su fecha de siembra y parámetros de desarrollo de la especie.

Módulo 5: Backend Relacional, Motor Inferencial e Integraciones
RF-16. Backend Relacional y Motor Inferencial

El backend debe coordinar los datos telemétricos IoT, peticiones de la app móvil y del portal web, procesando las variables mediante un motor inferencial para ajustar recomendaciones de riego y estimaciones de venta o descarte.

RF-17. Módulo Post-Venta para Clientes

El cliente podrá registrar en una Web App el QR de la planta adquirida para recibir recordatorios programados de riego y fertilización en su hogar.

RF-18. Registro de Merma y Compostaje

El sistema debe permitir registrar el peso en kg de la biomasa descartada correspondiente a plantas en estado Rojo y derivarla a un contador global de insumos para compostaje interno.

RF-19. Integración con Punto de Venta (POS)

Al procesarse la venta de un QR en el POS, el sistema debe cambiar automáticamente su estado a "Vendida", removiéndola del inventario activo sin intervención manual.

2. Requerimientos No Funcionales (RNF)
Los requerimientos no funcionales establecen los criterios de calidad, rendimiento, seguridad y experiencia de uso del sistema GreenTech.

Rendimiento y Escalabilidad
RNF-01. Tiempo de Respuesta en Escaneo

La lectura y despliegue de la ficha técnica pública en el portal web o de la bitácora en la app móvil tras escanear el QR no debe demorar más de 2 segundos con conexión móvil promedio 3G/4G/5G.

RNF-02. Capacidad de Ingesta Telemétrica IoT

El backend relacional debe ser capaz de procesar lecturas periódicas, cada 5 a 15 minutos, enviadas por microcontroladores ESP32 de forma concurrente sin degradación en el tiempo de respuesta.

Usabilidad y Accesibilidad
RNF-03. Diseño Responsivo y Móvil Primero

La interfaz de la aplicación móvil para el personal operativo y del portal web para clientes debe ser 100% responsiva y optimizada para smartphones y tablets.

RNF-04. Facilidad de Uso en Campo

Las acciones del personal operativo, como confirmar riego, reportar síntomas o cambiar estado, deben ejecutarse en un máximo de 3 toques en pantalla después de escanear el código QR.

RNF-05. Acceso sin Instalación para Clientes

El portal web público para clientes no debe exigir la descarga ni instalación de aplicaciones nativas; debe cargarse directamente en cualquier navegador web móvil como Chrome, Safari o Firefox.

Seguridad y Control de Acceso
RNF-06. Control de Acceso Basado en Roles (RBAC)

El sistema debe restringir estrictamente el acceso a las funciones operativas mediante tres roles:

Cliente: acceso público únicamente al portal web de ficha técnica y módulo post-venta.

Personal Operativo: lectura y escritura de bitácora, registro de riego, reporte de síntomas y tareas en la aplicación móvil.

Administrador: gestión de usuarios, configuración del motor inferencial, parámetros IoT y métricas del sistema.

RNF-07. Cifrado y Autenticación Segura

Las credenciales de acceso deben ser almacenadas utilizando algoritmos de hashing seguros (ej. Bcrypt o Argon2). Todo el tráfico entre app, web, IoT y backend debe realizarse mediante protocolos cifrados HTTPS/TLS.

RNF-08. Gestión de Sesiones y Tokens

La aplicación móvil debe manejar sesiones autenticadas mediante tokens seguros (ej. JWT) con tiempo de expiración y renovación controlada para mitigar el riesgo de secuestro de sesión.

RNF-09. Inmutabilidad de Registros de Trazabilidad

Los registros operativos con marca de tiempo (timestamp) y usuario no podrán ser editados ni eliminados por los trabajadores para garantizar la trazabilidad de las tareas realizadas en campo.

Restricciones del Entorno Físico y Disponibilidad
RNF-10. Durabilidad Física del Etiquetado

Los códigos QR impresos deben cumplir especificaciones de material resistente a humedad constante, riego por aspersión y exposición directa a radiación UV en los viveros de Atlixco.

RNF-11. Disponibilidad del Servicio

El sistema debe contar con una disponibilidad del 99.5% durante el horario operativo del vivero.
