# Sistema de diseño GreenTech · MASTER

Fuente de verdad del diseño de GreenTech (Android, Kotlin + Compose Multiplatform, Material 3 Expressive).
Cada token de este documento existe en código; si cambia uno, cambian los dos.

- Auditoría y decisiones descartadas: [`AUDITORIA.md`](AUDITORIA.md).
- Generado inicialmente con **ui-ux-pro-max** (`--design-system --persist`) y reescrito en español sobre la
  identidad existente; la dirección estética viene de **frontend-design**.

| Archivo | Contenido |
|---|---|
| `ui/theme/Color.kt` | Primitivas `PaletaGreenTech`, esquemas claro/oscuro, `GreenTechExtendedColors` |
| `ui/theme/Type.kt` | Escala Inter y roles `GtTexto` |
| `ui/theme/Dimens.kt` | Espaciado, tamaños táctiles, íconos y medidas de componentes |
| `ui/theme/Shape.kt` | Formas M3 y `GtShapes` (incluido el corte de hoja) |
| `ui/theme/Motion.kt` | Duraciones, resortes y "Reducir animaciones" |
| `commonTest/.../ContrasteTest.kt` | Prueba WCAG AA de todos los pares, en claro y oscuro |

---

## 1. Dirección estética

**Botánica técnica.** La precisión de una herramienta de campo con la calidez de un vivero: superficies tonales
verde-gris sin sombras, cifras grandes y tabulares que se leen a un metro de distancia, y el color reservado para lo
que le pasa a la planta. Si algo es verde intenso, está vivo o se puede tocar; si cambia de forma, cambió su salud.

### Reglas de uso en campo (no se negocian)

- Área táctil mínima **56 dp**; botones principales **60 dp**.
- Toda acción lleva **ícono + texto**. Nada depende solo de un gesto (deslizar, mantener): siempre hay un botón.
- Ningún estado se comunica solo con color: color + texto + (ícono o forma).
- Texto ≥ 4.5:1 y elementos gráficos ≥ 3:1 en claro y en oscuro (lo verifica `ContrasteTest`).
- "Reducir animaciones" lleva todas las duraciones a 0 ms y detiene los bucles.

---

## 2. Color

### 2.1 Primitivas (`PaletaGreenTech`)

Derivadas del logo. Las pantallas **no** las usan directamente: pasan por el esquema o por los colores extendidos.

| Verde de la hoja | Hex | Uso |
|---|---|---|
| Verde50 | `#F1F7EF` | Reserva (fondos muy suaves) |
| Verde100 | `#E3F0DF` | primaryContainer (claro), fondo de "sana" |
| Verde200 | `#C6E2C0` | Reserva |
| Verde300 | `#8FCB8A` | primary (oscuro), success (oscuro) |
| Verde400 | `#6FAE6B` | Reserva |
| **Verde500** | `#559058` | **Verde de marca** (ilustración, marca de agua) |
| **Verde600** | `#3D7A3A` | **primary (claro)**, acento del QR |
| Verde700 | `#2F6A2C` | Texto del par "primario" |
| Verde800 | `#24452A` | primaryContainer (oscuro) |
| Verde900 | `#16361A` | onPrimaryContainer (claro) |

| Verde azulado | Hex | | Agua (gota) | Hex |
|---|---|---|---|---|
| Azulado100 | `#DCEFE9` | | Agua100 | `#E3F1FA` |
| Azulado300 | `#7FC4B3` | | Agua300 | `#7CC4F0` |
| **Azulado600** | `#2F6E5E` (secondary) | | Agua500 | `#2A9BD8` (solo ilustración: < 3:1) |
| Azulado800 | `#1E4A40` | | **Agua600** | `#1570A6` (tertiary, foco) |
| Azulado900 | `#0F3029` | | Agua800 / 900 | `#10364F` / `#0B3A57` |

### 2.2 Superficies

| Token | Claro | Oscuro |
|---|---|---|
| background | `#F7FAF6` | `#0F1512` |
| surface | `#FFFFFF` | `#141B17` |
| surfaceContainerLow (tarjetas, mosaicos) | `#F2F6F1` | `#18201B` |
| surfaceContainer | `#EDF2EC` | `#1D2621` |
| surfaceContainerHigh | `#E6ECE5` | `#242E28` |
| surfaceContainerHighest (pistas de barras) | `#DFE6DE` | `#2B3630` |
| onSurface / onSurfaceVariant | `#1F2A33` / `#5B6670` | `#E4EBE5` / `#A7B3AB` |
| outline (bordes ≥ 3:1) | `#77827B` | `#7C8981` |

La jerarquía viene del **tono**, no de sombras. No hay `shadowElevation` en tarjetas; solo la barra inferior de
vidrio y las notificaciones flotantes se elevan.

### 2.3 Semánticos (`GreenTechTheme.colors`)

Cada par `ParColores(contenido, fondo)` cumple 4.5:1 entre sí y el contenido también sobre `surface` y
`surfaceContainerLow`.

| Grupo | Token | Par base |
|---|---|---|
| **Salud vegetal** | `salud.sana` | exito |
| | `salud.observacion` | info |
| | `salud.afectada` | advertencia |
| | `salud.critica` | error |
| **Tarea** (Parte 2) | `tarea.pendiente` / `enCurso` / `hecha` / `vencida` | neutro / info / exito / error |
| **Traslado** (Parte 2) | `traslado.enTransito` / `recibido` / `cancelado` | secundario / exito / neutro |
| **Series de gráficas** | `series.ventas` · `merma` · `humedad` · `temperatura` | primary · warning · tertiary · secondary |
| **Foco** | `foco` | Agua600 (claro) / Agua300 (oscuro) |
| Otros | `peligro`, `floracion`, `primario`, `secundario`, `neutro` | Severidad, etapas, eventos |

---

## 3. Tipografía (Inter)

Pesos: Regular, Medium, SemiBold, Bold. Las cifras usan `tnum` (`.tabular()`) para que no "bailen".
Las pantallas eligen por **rol** (`GtTexto`), no por tamaño. **Máximo 5 roles por pantalla.**

| Rol (`GtTexto`) | Estilo M3 | Tamaño / línea | Uso |
|---|---|---|---|
| `tituloPantalla` | headlineMedium | 28 / 36 SemiBold | Saludo, nombre del lote en el detalle, títulos de login y bienvenida |
| `tituloTarjeta` | titleMedium | 18 / 24 SemiBold | Título de tarjeta, mosaico, sección, hoja inferior y diálogo |
| `dato` | displaySmall + tnum | 36 / 44 SemiBold | El número protagonista: plantas, KPI, lecturas |
| `etiqueta` | labelMedium | 14 / 20 Medium | Rótulo sobre un dato, chips de estado, ejes y leyendas |
| `ayuda` | bodyMedium | 15 / 22 Regular | Texto de apoyo: código, autor, fecha, descripciones, estados vacíos de mosaico |
| `cuerpo` | bodyLarge | 17 / 26 Regular | Solo texto largo: párrafos de Ayuda, mensajes de estados vacíos |
| `boton` | labelLarge | 15 / 20 SemiBold | Botones, chips de selección, segmentados |

La barra superior conserva su escala propia (headlineSmall en pestañas, titleLarge en detalles).

---

## 4. Espaciado y medidas (`Dimens`)

Escala en múltiplos de 4: `xs 4 · sm 8 · md 12 · lg 16 · lgx 20 · xl 24 · xxl 32 · xxxl 48 · xxxxl 64`
(`xxs 2` solo para ajustes ópticos).

| Uso semántico | Token | Valor |
|---|---|---|
| Margen lateral de pantalla | `margenLateral` | 20 |
| Separación entre tarjetas y mosaicos | `separacionTarjetas` | 12 |
| Relleno de tarjeta y mosaico | `rellenoTarjeta` | 16 |
| Relleno de hoja inferior / diálogo | `rellenoHoja` | 24 |
| Separación entre secciones | `separacionSeccion` | 24 |
| Ícono ↔ texto | `iconoTexto` | 8 |
| Espacio para FAB + barra flotante | `espacioFab` | 88 |
| Área táctil mínima / botón | `tactilMin` / `botonAlto` | 56 / 60 |
| Fila de lista con dos líneas | `filaLista` | 64 |
| Íconos | `iconoMini · iconoChico · icono · iconoMediano · iconoGrande` | 16 · 20 · 24 · 28 · 32 |

---

## 5. Formas

| Forma | Radio | Uso |
|---|---|---|
| `GtShapes.campo` | 16 | Campos, chips de selección, contenedores de ícono |
| `GtShapes.tarjeta` | 20 | Tarjetas de lista (`GtCard`, `LoteCard`) |
| `GtShapes.mosaico` | 24 | Mosaicos bento, secciones de formulario |
| `GtShapes.hoja` | 32 arriba | Hojas inferiores |
| `GtShapes.pastilla` | 50 % | Botones, insignias, barra inferior |
| **`GtShapes.hojaCorte`** | 32 · 8 · 32 · 8 | **Detalle distintivo** (ver §6) |

## 6. Detalle distintivo: el corte de hoja

La hoja del logo tiene dos esquinas opuestas muy redondeadas y dos casi en punta. `GtShapes.hojaCorte` repite esa
silueta y se usa **solo** en tres lugares:

1. el mosaico de saludo de **Inicio del empleado**;
2. el mosaico de resumen del **Detalle del lote**;
3. la **LoteCard destacada** (encabezado "estás registrando en este lote" de los formularios), con su ícono en
   `hojaCorteIcono`.

Ningún otro componente la usa. Si aparece en un cuarto lugar, deja de ser distintiva.

---

## 7. Componentes

### 7.1 PlantHealthIndicator

| Estado | Token | Ícono outlined | Forma |
|---|---|---|---|
| Sana | `salud.sana` (success) | `Eco` | círculo lleno |
| En observación | `salud.observacion` (info) | `Visibility` | círculo con borde |
| Afectada | `salud.afectada` (warning) | `ReportProblem` | triángulo |
| Crítica | `salud.critica` (error) | `Dangerous` | rombo |

Variantes (`VarianteSalud`):

- **CHIP**: pastilla tonal con ícono + texto. Tarjetas y detalle.
- **PUNTO**: forma de 12 dp + texto. Listas densas (`LoteCard` compacta).
- **ANILLO**: anillo de 96 dp con el ícono al centro, la forma como insignia y el texto debajo.

`BotonLeyendaSalud` (ícono Info de 56 dp) abre `HojaLeyendaSalud`: forma, ícono, nombre y significado de cada
estado. Está junto al indicador en Lista de lotes, Detalle, Etapa y Observación. Los selectores de salud de los
formularios usan el mismo ícono y token.

### 7.2 LoteCard v2

Jerarquía: **especie** (tituloTarjeta) → **código** (ayuda + tnum) → **salud** (chip) → **etapa** (ícono + etiqueta)
→ **"X de Y plantas disponibles"** + zona → barra → **última actividad** (opcional).

| Variante | Uso | Contenido |
|---|---|---|
| `NORMAL` | Lista de lotes, supervisión | Todo lo anterior |
| `COMPACTA` | Listas densas, selección, traslados | Una fila: ícono, especie, código y salud en punto |
| `DESTACADA` | Encabezado de formularios (`LoteResumen`) | Una fila sobre primaryContainer con corte de hoja, "código · zona" y chip de salud |

Ranuras para la Parte 2: `tareasPendientes` (insignia `tarea.pendiente`, ícono `Pending`) y `plantasEnTransito`
(insignia `traslado.enTransito`, ícono `LocalShipping`). Solo se dibujan con un valor > 0.

### 7.3 Componentes base y estados

| Componente | Normal | Presionado | Deshabilitado | Foco | Error |
|---|---|---|---|---|---|
| `GtButton` (60 dp, pastilla) | primary / tonal / contorno | escala 0.97 + morph a 16 dp | surfaceContainerHigh + onSurfaceVariant | anillo `foco` 3 dp | variante Peligro |
| `GtTextField` (56 dp, radio 16) | borde outline | — | surfaceContainerHigh | borde primary | borde error + ícono + mensaje |
| `GtChip` (56 dp) | contorno outline | escala 0.97 | — | anillo `foco` | sacudida + mensaje |
| `GtCard` / `BentoTile` / `LoteCard` | tono de superficie | escala 0.97 + ripple | — | anillo `foco` | — |
| `GtBottomSheet` | radio 32 arriba, surfaceContainerLow | — | — | — | — |
| `ConfirmSheet` / `ConfirmDialog` | círculo de 64 dp con ícono + resumen | — | — | — | variante Peligro |
| `EmptyState` (pantalla) | círculo 104 dp + título + mensaje + acción | | | | |
| `VacioMosaico` (dentro de mosaicos) | ícono 24 + texto `ayuda` | | | | |
| `ErrorState` | `EmptyState` + "Reintentar" | | | | |
| `OfflineBanner`, `GtNotification` | pares semánticos | | | | |
| `GtBottomBar` | vidrio, 64 dp, pastilla del seleccionado | escala | — | ripple acotado | — |

Carga: skeletons con alto de `Dimens` (`skeletonKpi`, `skeletonGrafica`, `skeletonResumen`) y `GtCargando` en botones.

### 7.4 Gráficas (`ui/components/charts`)

- Un grosor de línea (2.5 dp) para líneas y sparklines; guías de 1 dp en outlineVariant, máximo 3.
- Colores por serie desde `colors.series`: ventas (verde), merma (ámbar), humedad (azul agua), temperatura (azulado).
- Etiqueta directa en el periodo resaltado o en el último punto. Leyenda solo con 2+ series, con forma distinta por serie.
- Tooltip de vidrio de 128 dp al tocar y mantener; no consume el desplazamiento.
- Entrada única: 600 ms + 40 ms por elemento, desaceleración cúbica (`Grafica.easingEntrada`); 0 ms con
  "Reducir animaciones". El gauge usa la misma duración y curva.
- Cada gráfica expone un resumen accesible (`contentDescription`) con los valores.

---

## 8. Jerarquía por tipo de pantalla

| Tipo | 1.º | 2.º | 3.º |
|---|---|---|---|
| Panel (Inicio, Admin) | Saludo y acción principal | Alerta activa | Números del día |
| Lista | Buscador y filtros | Especie o nombre | Estado (salud, rol) |
| Detalle del lote | Especie + salud | Plantas disponibles (`dato`) | Sensores, luego acciones |
| Formulario | Lote en contexto (LoteCard destacada) | Campo actual | Botón fijo |
| Dashboard | Periodo | KPI | Gráficas y listas |
| Bitácora | Fecha | Título del evento | Autor y hora |
| Perfil / Ayuda | Nombre o tema | Preferencias / pasos | Acciones secundarias |

## 9. Movimiento

Duraciones 100 · 200 · 300 ms, resortes amortiguados (`Motion`). Una sola entrada orquestada por lista (escalonado
de 30 ms) y por gráfica. Las respuestas al toque (presión, morph, check de guardado) sí se animan; nada se mueve en
bucle salvo el fondo del login, que se detiene con "Reducir animaciones".

## 10. Lista de verificación antes de entregar

- [ ] Ningún `Color(0x…)` fuera de `ui/theme/`.
- [ ] Espaciados con `Dimens`; nada fuera de la escala de 4 salvo bordes y trazos.
- [ ] Máximo 5 roles de `GtTexto` por pantalla.
- [ ] Salud siempre con `PlantHealthIndicator` (nunca solo color).
- [ ] Áreas táctiles ≥ 56 dp; acciones con ícono + texto.
- [ ] `ContrasteTest` pasa; colores nuevos agregados a la prueba.
- [ ] `@Preview` en claro y oscuro de cada componente y pantalla nuevos.
- [ ] Sin elementos de prueba visibles.
