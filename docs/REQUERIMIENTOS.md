# Documento de Especificación de Requerimientos del Sistema (ERS)

## Proyecto: GREENTECH-ITSA

**Sistema de Gestión Inteligente de Vivero**

---

# 1. Requerimientos Funcionales (RF)

Los requerimientos funcionales definen lo que el sistema debe hacer directamente para resolver las problemáticas de pérdida de inventario, riego ineficiente, falta de trazabilidad y desinformación.

## Módulo 1: Identificación y Acceso por Código QR

### RF-01. Generación de Códigos QR Dinámicos

El sistema debe generar un código QR único asignable a una maceta individual o a un lote completo de cultivo desde el registro inicial.

### RF-02. Vista Pública (Ficha Técnica para Cliente)

Al escanear el QR sin autenticación previa, el sistema debe desplegar una página informativa que contenga:

- Nombre común y nombre científico de la planta.
- Requerimientos de luz y frecuencia de riego.
- Nivel de toxicidad para mascotas.
- Recomendaciones post-compra.

### RF-03. Vista Privada (Panel Operativo para Personal)

Al escanear el QR desde un dispositivo autenticado, el sistema debe desplegar la bitácora operativa permitiendo registrar:

- Eventos de riego y fertilización.
- Registro de podas y cambios de sustrato o maceta.
- Cambio manual o automático de estado de salud.

---

## Módulo 2: Gestión de Riegos y Mantenimiento

### RF-04. Parametrización de Riego por Especie

El sistema debe permitir definir matrices de riego según la especie, tipo de sustrato, etapa de desarrollo y época del año.

### RF-05. Trazabilidad y Confirmación de Tareas

Al realizar una labor de riego o fertilización, el operador debe escanear el QR para registrar la tarea. El sistema almacenará automáticamente la marca de tiempo (timestamp) y el ID del usuario.

### RF-06. Tablero de Alertas de Mantenimiento

El sistema debe emitir alertas visuales y notificaciones al encargado del vivero sobre lotes o plantas con retraso o incumplimiento en su ventana de riego programada.

---

## Módulo 3: Monitoreo de Madurez y Protocolo de Descarte

### RF-07. Semáforo de Estado de Salud y Comercialización

Cada planta o lote debe contar con un estado visual de salud:

- **Verde — Listo para Venta:** Cumple con los parámetros de madurez, altura, follaje y floración.
- **Amarillo — En Crecimiento / Cuarentena:** En desarrollo normal o bajo tratamiento recuperativo por plaga leve o estrés.
- **Rojo — Baja / Descarte:** Pérdida por plaga irreversible o muerte.

### RF-08. Publicación Automática en Catálogo

Al cambiar el estado a Verde, el sistema debe publicar o habilitar la planta en el inventario o catálogo del punto de venta de forma automática.

### RF-09. Protocolo Automático de Aislamiento

Al marcar un lote o planta en Rojo, el sistema debe generar una orden de retiro inmediato dirigida al personal para aislamiento o descarte rápido, previniendo contagios.

### RF-10. Proyección de Tiempo a Venta

El sistema debe calcular y mostrar la fecha estimada en que la planta alcanzará el estado "Listo para Venta" basándose en su fecha de propagación o siembra.

---

## Módulo 4: Funcionalidades Adicionales e Integraciones

### RF-11. Monitoreo e Integración IoT

El sistema debe recibir datos en tiempo real de sensores IoT de humedad de suelo, temperatura ambiental e intensidad de luz, y disparar alertas de riego automáticas si los niveles caen por debajo del umbral tolerado.

### RF-12. Módulo Post-Venta para Clientes

El cliente podrá registrar en una Web App el QR de la planta adquirida para recibir recordatorios programados de riego y fertilización en su hogar.

### RF-13. Registro de Merma y Compostaje

El sistema debe permitir registrar el peso en kg de la biomasa descartada correspondiente a plantas en estado Rojo y derivarla a un contador global de insumos para compostaje interno.

### RF-14. Integración con Punto de Venta (POS)

Al procesarse la venta de un QR en el POS, el sistema debe cambiar automáticamente su estado a "Vendida", removiéndola del inventario activo sin intervención manual.

---

# 2. Requerimientos No Funcionales (RNF)

Los requerimientos no funcionales establecen los criterios de calidad, rendimiento, seguridad y experiencia de uso del sistema GREENTECH-ITSA.

## Rendimiento y Escalabilidad

### RNF-01. Tiempo de Respuesta en Escaneo

La lectura y despliegue de la ficha técnica pública o privada tras escanear el QR no debe demorar más de 2 segundos con conexión móvil promedio 3G/4G.

### RNF-02. Capacidad de Ingesta IoT

El backend debe ser capaz de procesar lecturas periódicas, cada 5 a 15 minutos, de al menos 100 sensores IoT concurrentes sin degradación en el tiempo de respuesta.

## Usabilidad y Accesibilidad

### RNF-03. Diseño Responsivo y Móvil Primero

La interfaz para el personal de vivero y clientes debe ser 100% responsiva y optimizada para smartphones y tablets.

### RNF-04. Facilidad de Uso en Campo

Las acciones del personal, como marcar riego o cambiar estado, deben ejecutarse en un máximo de 3 clics o toques después de escanear el código QR.

### RNF-05. Acceso sin Instalación

La vista del cliente no debe exigir la descarga ni instalación de aplicaciones nativas; debe cargarse directamente en cualquier navegador web móvil como Chrome, Safari o Firefox.

## Seguridad y Control de Acceso

### RNF-06. Control de Acceso Basado en Roles (RBAC)

El sistema debe restringir estrictamente el acceso a las funciones operativas mediante tres roles:

- **Cliente:** acceso público.
- **Operador de Vivero:** lectura y escritura de bitácora.
- **Administrador:** configuración de parámetros y métricas.

### RNF-07. Protección de Datos e Inmutabilidad de Registros

Los registros con marca de tiempo y usuario no podrán ser editados ni eliminados por operadores para garantizar la trazabilidad de las tareas realizadas.

## Restricciones del Entorno Físico y Disponibilidad

### RNF-08. Durabilidad Física del Etiquetado

Los códigos QR impresos deben cumplir especificaciones de material resistente a humedad constante, riego por aspersión y exposición directa a radiación UV.

### RNF-09. Disponibilidad del Servicio

El sistema debe contar con una disponibilidad del 99.5% durante el horario operativo del vivero.
