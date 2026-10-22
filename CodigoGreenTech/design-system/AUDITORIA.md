# Auditoría de diseño · GreenTech (Parte 1)

Revisión de `ui/theme/`, `ui/components/` (incluidas las gráficas) y las 27 pantallas antes de tocar código.
Fuentes: generador de sistema de diseño de **ui-ux-pro-max** (búsqueda "GreenTech, gestión de viveros, trabajadores
de campo y administrador, Android, Jetpack Compose", persistida en `MASTER.md` y luego reescrita en español),
búsquedas complementarias (`product: agriculture farm`, `color: agriculture green nature`, `chart: dashboard trend`,
`stack: jetpack-compose`) y la dirección estética de **frontend-design**.

> Regla de la auditoría: **refinar, no reemplazar**. Nada de lo que aquí se propone cambia la navegación, el flujo,
> los textos ni la lógica (`*Logic.kt`, casos de uso, repositorios).

---

## 1. Se conserva

- **Tema completo claro/oscuro** en `Color.kt` + `GreenTechExtendedColors`: ninguna pantalla tiene un hex propio
  (la búsqueda `Color(0x` fuera de `theme/` da 0 resultados).
- **Prueba de contraste** (`ContrasteTest`) que recorre ambos temas, incluido el vidrio sobre blanco/negro puros.
- **Inter** en cuatro pesos y `tabular()` para cifras.
- **Áreas táctiles**: botones de 60 dp, chips de 56 dp, `IconButton` de 56 dp, barra inferior de 64 dp.
- **Ícono + texto** en todas las acciones; estados que nunca dependen solo del color (flechas en sensores,
  "Registrado ahora" con ícono, variación con flecha de tendencia).
- **BentoTile / BentoGrid** (S/M/L, 2 → 4 columnas en tablet), **GlassSurface** con respaldo sólido,
  **Motion.kt** con "Reducir animaciones" que lleva todo a 0 ms.
- **Gráficas minimalistas** dibujadas en `Canvas`, con etiquetas directas, tooltip de vidrio y resumen accesible.
- **Iconografía outlined** centralizada en `IconosModulo` / `IconosDominio` (filled solo en la pestaña seleccionada).
- Jerarquía por **tono de superficie** (sin sombras), que funciona bien al sol y en oscuro.

## 2. Inconsistencias encontradas

| Área | Hallazgo |
|---|---|
| Espaciado | ~600 valores `N.dp` escritos a mano en pantallas y componentes; `Dimens` solo se usa en 3 componentes. Hay valores fuera de la escala de 4: `6.dp` (13 usos), `10.dp` (7), `14.dp`, `22.dp`, `30.dp`. |
| Tarjetas | Cinco contenedores distintos: `GtCard` (radio 20), `BentoTile` (24), `TimelineItem` (`Surface` de 16), `SensorGauge` (`Surface` propio) y dos `Surface` con `shadowElevation = 2.dp` (Login y QR) — las únicas sombras de la app. |
| Salud vegetal | "En observación" usa ámbar y "Afectada" naranja: dos tonos casi iguales. Los íconos `WarningAmber` (Afectada) y `ReportProblem` (Crítica) son dos triángulos: sin color no se distinguen. La salud solo existe como chip; no hay forma ni leyenda. |
| Tipografía | Inicio usa 8 estilos, Detalle/Dashboard/Ayuda 6. El mismo rol cambia de estilo: el título de tarjeta es `titleSmall`, `titleMedium` o `titleLarge` según la pantalla; el dato protagonista es `displaySmall` en Inicio y `headlineMedium` en los KPI. Las etiquetas (`labelMedium`, 13 sp) son el texto más chico y el que más se lee al sol. |
| Íconos | Tamaños escritos a mano (16/20/24/28/32 dp); `androidx.compose.material3.Icon` con nombre completo en Dashboard. |
| Estados vacíos | Las listas usan `EmptyState`, pero los mosaicos muestran "Sin datos" como texto suelto con estilos distintos (`bodyLarge`, `titleMedium`, con o sin ícono). Los skeleton de Dashboard calculan su alto con sumas sueltas (`altoMinimo + 40.dp`). |
| Gráficas | Grosor de línea 2.5 dp en `LineChart` y 2 dp en `Sparkline`; el `Gauge` entra con otra duración (`CONTADOR`) que el resto (`ENTRADA_GRAFICA`); tooltips de 112 y 128 dp; la humedad se dibuja en verde primario aunque la marca la representa con la gota azul; `Dimens.barraHorizontalAlto` existe pero no se usa. |
| Foco | Tarjetas, mosaicos y chips no muestran foco visible con teclado / D-pad (solo ripple al tocar). |
| LoteCard | Sin variantes, sin última actividad, y la salud compite con la etapa con el mismo peso visual. |

## 3. Se refina

| Prioridad | Cambio |
|---|---|
| **Alta** | `PlantHealthIndicator` (chip, punto con etiqueta, anillo) con token + ícono + **forma** por estado y leyenda desde el ícono Info. Sustituye a `SaludChip` y a los íconos de salud de los formularios. |
| **Alta** | Tokens semánticos de salud (`salud.sana/observacion/afectada/critica`) siguiendo la tabla: success · info · warning · error. Tokens de estado de tarea y de traslado para la Parte 2. Todos añadidos a `ContrasteTest`. |
| **Alta** | `LoteCard` v2: especie → código (tnum) → salud → etapa → barra "X de Y plantas" → última actividad. Variantes compacta y destacada, ranuras para "tareas pendientes" y "en tránsito". |
| **Alta** | Escala de espaciado en `Dimens` aplicada en todas las pantallas y componentes; valores fuera de la escala de 4 redondeados. |
| **Media** | Roles tipográficos (`GtTexto`): título de pantalla, título de tarjeta, dato protagonista, etiqueta, ayuda. Máximo 5 por pantalla. Etiquetas a 14 sp. |
| **Media** | Paleta verde 50–900 derivada del logo, más escalas cortas de verde azulado y agua, como primitivas documentadas. |
| **Media** | Gráficas: un grosor, colores por serie (`GreenTechTheme.colors.series`), una duración de entrada, tooltips del mismo ancho, humedad en azul agua. |
| **Media** | Anillo de foco visible (`anilloFoco`) en botones, tarjetas, mosaicos y chips. |
| **Media** | Un solo contenedor de tarjeta (`GtCard`/`BentoTile`); se quitan las dos sombras sueltas (la tarjeta del QR conserva el blanco por legibilidad del código). |
| **Baja** | Estado vacío compacto para mosaicos (`VacioMosaico`) con ícono + texto en el mismo estilo. |
| **Baja** | Detalle distintivo "corte de hoja" (ver §6) en tres lugares. |

## 4. Se descarta (propuestas de la skill que no aplican)

| Propuesta del generador | Por qué no |
|---|---|
| Paleta azul `#2563EB` + acento naranja `#EA580C` (primera búsqueda) | Rompe la identidad: los verdes salen del logo. |
| Paleta "Organic Biophilic" `#15803D` + dorado `#A16207`, fondo menta `#F0FDF4` | Verde ajeno al logo; el dorado compite con `warning` (merma, afectada). El borde `#BBF7D0` no llega a 3:1 sobre blanco. |
| Fuentes Outfit + Work Sans / Fira Code + Fira Sans | Se mantiene Inter. Las cifras usan `tnum` de Inter en vez de una monoespaciada (frontend-design también la señala como cliché). |
| Patrón "Hero + Features + CTA" | Es de landing page; la app no tiene página de venta. |
| Estados *hover*, `cursor-pointer`, *breakpoints* 375/768/1024/1440 | Pantalla táctil; el ancho se resuelve con 2/4 columnas a partir de 600 dp. |
| "Siempre transiciones de 150–300 ms" | Con "Reducir animaciones" el cambio debe ser inmediato (0 ms). Se respeta la preferencia. |
| "Formas SVG orgánicas y fluidas" en toda la interfaz | Ruido visual al sol y con prisa; se reduce a un único detalle (§6). |
| Sombras marcadas (estilo suizo) | La jerarquía por tono se lee mejor en oscuro y no se "lava" al sol. |
| Gráficas con zoom y teclado (+/-) | Uso con guantes: se mantiene tocar-y-mantener para el tooltip y un resumen accesible. |

---

## 5. Dirección estética (frontend-design)

**Botánica técnica.** La precisión de una herramienta de campo con la calidez de un vivero: superficies tonales
verde-gris sin sombras, cifras grandes y tabulares que se leen a un metro de distancia, y el color reservado para lo
que le pasa a la planta. Si algo es verde intenso, está vivo o se puede tocar; si cambia de forma, cambió su salud.

## 6. Detalle distintivo: el corte de hoja

La hoja del logo tiene dos esquinas opuestas muy redondeadas y dos casi en punta. Esa silueta se vuelve una forma
(`GtShapes.hojaCorte`: 32 dp arriba-izquierda y abajo-derecha, 8 dp en las otras dos).

Se usa **solo** en:

1. el mosaico protagonista de **Inicio del empleado** (saludo y acciones),
2. el mosaico de resumen del **Detalle del lote**,
3. la variante **destacada** de `LoteCard`.

En ningún otro lugar: botones, chips, campos y hojas inferiores conservan su familia redondeada.

## 7. Jerarquía: qué se ve primero

| Tipo de pantalla | 1.º | 2.º | 3.º |
|---|---|---|---|
| Panel (Inicio, Admin) | Saludo y acción principal | Alerta activa | Números del día |
| Lista (Lotes, Trabajadores, Auditoría) | Buscador y filtros | Especie o nombre de cada tarjeta | Estado (salud, rol) |
| Detalle del lote | Especie + salud | Plantas disponibles (dato protagonista) | Sensores, luego acciones |
| Formulario | Lote en contexto | Campo actual | Botón fijo de guardar |
| Dashboard | Periodo | KPI | Gráficas y listas |
| Bitácora | Fecha | Título del evento | Autor y hora |
| Perfil / Ayuda | Nombre o tema | Preferencias / pasos | Acciones secundarias |

---

## 8. Límites de esta parte (sin tocar lógica)

- **Salud en Bitácora y Dashboard:** `EventoBitacora` e `Indicador`/`Dashboard` no traen el estado de salud, así que
  ahí no hay un `PlantHealthIndicator` que mostrar sin cambiar casos de uso. El indicador y su leyenda están en Lista
  de lotes, Detalle, Etapa, Observación y en el encabezado de todos los formularios (LoteCard destacada).
- **Última actividad en LoteCard:** la ranura existe (`ultimaActividad`), pero `ListaLotesUiState` no la trae; se
  llenará cuando la lógica la exponga (Parte 2), igual que `tareasPendientes` y `plantasEnTransito`.
- **Variante compacta:** lista para listas densas y traslados (Parte 2); hoy solo se ve en la `@Preview` de LoteCard.
