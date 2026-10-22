# GreenTech

**GreenTech para Android**: app de gestión de viveros hecha con **Kotlin + Compose Multiplatform** (un solo target Android).
Dos roles: **Empleado** (operación en campo) y **Administrador** (gestión y reportes).

> Estado actual (**v1.0.0**): todas las interfaces son navegables y funcionan con datos en memoria.
> Supabase, el modo sin conexión, la cámara real y las notificaciones push todavía no están conectados (ver [Pendientes](#pendientes-todo)).

## Novedades v1.0.0

Versión final para usuarios reales: sin elementos de prueba y con una experiencia más clara.

- **Sin elementos de desarrollo en la interfaz**: se quitaron los "Usuarios de prueba" del login, la Galería de
  diseño, el botón "Simular escaneo" y los textos "simulado".
- **Login final**: marca + "Gestión inteligente de viveros", correo y contraseña con teclado adecuado, aviso de
  Bloq Mayús (teclado físico en Android), "¿Olvidaste tu contraseña?" y versión al pie. Si falla, se conserva el correo.
- **Navegación coherente**: pestañas con título grande; detalles con subtítulo de contexto ("Agave · GT-8F2A91C0D4");
  formularios con X, nota "* Obligatorio" y botón fijo. Tocar la pestaña activa sube al inicio. Escanear destacado.
- **Formularios**: "¿Descartar cambios?" al salir con cambios, validación al salir del campo, primer campo enfocado,
  "Siguiente" avanza y la última tecla envía, valores por defecto (hoy, zona del último lote, responsable) y
  resumen antes de confirmar en Registrar salida y Agregar trabajador.
- **Retroalimentación**: el QR de un lote nuevo ofrece "Guardar imagen", "Ver lote" y "Registrar otro lote"; al volver
  al detalle se resalta lo recién registrado; las acciones irreversibles (salidas, bajas, cerrar sesión) se confirman
  en una hoja inferior que resume lo que va a pasar.
- **Primer uso y ayuda**: bienvenida de 3 tarjetas por rol (se puede volver a ver desde Ayuda o Perfil) e ícono Info
  en Registrar salida, Etapa, Reportar anomalía y Dashboard (cada KPI explica cómo se calcula y se muestra el periodo exacto).
- **Listas y datos**: conteo con "Limpiar filtros", búsquedas vacías que dicen qué se buscó, fechas como "Hoy, 10:32"
  y cantidades con contexto ("45 de 120 plantas disponibles").
- **Lenguaje**: botones con verbo + objeto, sentence case, tuteo y errores que dicen cómo arreglarlos
  ("La cantidad supera las plantas disponibles (45). Ingresa 45 o menos.").
- **Accesibilidad**: títulos de pantalla anunciados, ningún estado solo con color y etiquetas que se ajustan con letra grande.

---

## Estructura

```
GreenTech/
├── composeApp/   App Android (Compose Multiplatform): tema, componentes, pantallas, navegación
├── shared/       Lógica y datos: modelos, repositorios, casos de uso, validaciones, Fakes, DI (Koin)
└── supabase/     README (la base de datos ya existe en la nube)
```

Cada pantalla tiene dos archivos:

- `XScreen.kt` → solo diseño. Composables sin estado que reciben `estado` y `onEvent`.
- `XLogic.kt` → ViewModel multiplataforma con `StateFlow<XUiState>`, `onEvent(XEvent)` y un canal de efectos
  de un solo uso (navegar, volver, mostrar notificación).

La UI nunca llama a repositorios: **Logic → caso de uso → repositorio**.

## Requisitos

- Android Studio (Narwhal o más reciente) con el plugin **Kotlin Multiplatform**.
- JDK 17 o superior (el de Android Studio sirve).

Versiones (en `gradle/libs.versions.toml`): AGP 9.4.1, Kotlin 2.4.20, Compose Multiplatform 1.12.1,
Material 3 **1.12.0-alpha03** (APIs Expressive), Navigation 2.9.2, Lifecycle 2.11.0, Koin 4.2.2, Coroutines 1.11.0,
Serialization 1.11.0, kotlinx-datetime 0.8.0, qrose 1.3.0, **Haze 2.0.1** (`haze` + `haze-blur`),
**multiplatform-settings-no-arg 1.3.0**. minSdk 26.

## Cómo ejecutar

### Android
1. Abre la carpeta `GreenTech` en Android Studio y espera la sincronización de Gradle.
2. Elige la configuración **composeApp** y un emulador o teléfono.
3. Pulsa **Run**. Desde terminal:
   ```bash
   ./gradlew :composeApp:assembleDebug      # compila el APK
   ./gradlew :composeApp:installDebug       # lo instala en el dispositivo conectado
   ```

## Solo para desarrollo

Cuentas de los repositorios en memoria (`shared/.../data/fake/FakeData.kt`). **No se muestran en la app**:
se escriben a mano en el login.

| Correo | Contraseña | Nombre | Rol | Estado |
|---|---|---|---|---|
| admin@gmail.com | admin123 | Administrador GreenTech | ADMINISTRADOR | Activo |
| empleado@gmail.com | empleado123 | Juan Pérez | EMPLEADO | Activo |
| inactivo@gmail.com | inactivo123 | Luis Gómez | EMPLEADO | Inactivo (muestra "Tu cuenta está desactivada…") |

Los repositorios en memoria tardan 600–900 ms en responder. El cambio de contraseña y el alta/baja de trabajadores
modifican los datos mientras la app esté abierta. Códigos de lote de ejemplo para "Escribir código del lote":
`GT-8F2A91C0D4` (Agave) y `GT-3B7E4D21A9` (Nochebuena).

La bienvenida de primer uso se guarda por usuario en el dispositivo; para verla otra vez usa
Ayuda → "Ver introducción" o borra los datos de la app.

## Logo e íconos

- `drawable/logo_icon_transparente.png` → emblema sin fondo. Junto con el texto "GreenTech" compuesto en Compose
  (Inter Bold, "Green" en `primary` y "Tech" en `onSurface`) forma la marca en claro y en oscuro:
  `EmblemaGreenTech`, `LogotipoGreenTech` y `MarcaGreenTech` en `ui/components/Marca.kt`.
- `drawable/logo_icon.png` → emblema con fondo blanco; solo se usa dentro del QR.
- `drawable/logo.jpg` → logo completo original. Ya no se usa en la app (tiene fondo blanco y "Tech" gris oscuro, no sirve en tema oscuro).
- Ícono Android: adaptive icon en `composeApp/src/androidMain/res/mipmap-*` (fondo `#FFFFFF`).

Los íconos se generaron recortando el emblema circular de `logo.jpg`. Si cambias el logo, reemplaza los dos archivos de
`drawable/` y vuelve a generar los íconos (en Android Studio: *New → Image Asset* con `logo_icon.png`, fondo blanco).

## Conectar Supabase (cuando toque)

1. Implementa los repositorios reales en `shared/src/commonMain/kotlin/com/greentech/shared/data/repository/`
   (hay un esqueleto `SupabaseAuthRepository`) usando `data/remote` (cliente, fuentes y DTOs) y `data/mapper`.
2. Regístralos en `supabaseRepositoriesModule` dentro de
   `shared/src/commonMain/kotlin/com/greentech/shared/di/SharedModule.kt`.
3. Cambia la bandera:
   ```kotlin
   const val USE_FAKE_DATA = false
   ```
   No hace falta tocar ninguna pantalla: la UI solo conoce las interfaces de repositorio.

## Sistema de diseño

La fuente de verdad es **[design-system/MASTER.md](design-system/MASTER.md)**: paleta verde 50–900 derivada del
logo, colores semánticos de salud vegetal, tareas y traslados, roles tipográficos (`GtTexto`), espaciado (`Dimens`),
formas, componentes con sus estados, reglas de gráficas y jerarquía por tipo de pantalla. La auditoría que lo
originó (qué se conservó, qué se refinó y qué se descartó) está en [design-system/AUDITORIA.md](design-system/AUDITORIA.md).

Resumen rápido:

- **Dirección:** "botánica técnica": superficies tonales sin sombras, cifras grandes con `tnum` y color reservado
  para el estado de la planta.
- **Salud vegetal:** `PlantHealthIndicator` (chip, punto con etiqueta y anillo). Cada estado tiene token, ícono y
  forma (sana ● · en observación ○ · afectada ▲ · crítica ◆); la leyenda se abre con el ícono Info.
- **LoteCard v2:** variantes normal, compacta y destacada, con ranuras para "tareas pendientes" y "en tránsito".
- **Detalle distintivo:** el corte de hoja del logo (`GtShapes.hojaCorte`), solo en el saludo de Inicio, el resumen
  del Detalle del lote y la LoteCard destacada.
- **Foco visible:** anillo azul agua de 3 dp (`anilloFoco`) en botones, tarjetas, mosaicos y chips.

Todo vive en `composeApp/src/commonMain/kotlin/com/greentech/app/ui/theme/` y `ui/components/`.
Las pantallas **no tienen colores fijos**: leen `MaterialTheme.colorScheme` o `GreenTechTheme.colors`.

| Capa | Qué se usa |
|---|---|
| Sistema | Material 3 con `MaterialExpressiveTheme` y `MotionScheme.expressive()` |
| Tema | Claro (por defecto, se usa al sol), Oscuro y Sistema. Se elige en Perfil → Apariencia y se guarda con multiplatform-settings |
| Tipografía | Inter (OFL) en `composeResources/font/`; roles `GtTexto` (título de pantalla, título de tarjeta, dato, etiqueta, ayuda); cifras con `tnum` |
| Formas | 8 · 12 · 16 · 24 · 32 dp. Mosaicos 24 dp, tarjetas 20 dp, campos 16 dp, botones pill (pasan a 16 dp al presionar); corte de hoja en 3 lugares |
| Íconos | Solo `Icons.Outlined.*`. Relleno únicamente en la pestaña activa de la barra inferior. Íconos de módulo en `IconosModulo` |
| Movimiento | `Motion.kt`: 100 / 200 / 300 ms y resortes. Con "Reducir animaciones" todo dura 0 y no hay bucles |

### Paletas

| Token | Claro | Oscuro |
|---|---|---|
| primary / onPrimary | `#3D7A3A` / `#FFFFFF` | `#8FCB8A` / `#0F2A10` |
| primaryContainer / on | `#E3F0DF` / `#16361A` | `#24452A` / `#CFEBC9` |
| secondary / onSecondary | `#2F6E5E` / `#FFFFFF` | `#7FC4B3` / `#0B2B24` |
| secondaryContainer / on | `#DCEFE9` / `#0F3029` | `#1E4A40` / `#C9EDE3` |
| tertiary / onTertiary | `#1570A6` / `#FFFFFF` | `#7CC4F0` / `#06263A` |
| background / surface | `#F7FAF6` / `#FFFFFF` | `#0F1512` / `#141B17` |
| surfaceContainerLow / – / High | `#F2F6F1` / `#EDF2EC` / `#E6ECE5` | `#18201B` / `#1D2621` / `#242E28` |
| onSurface / onSurfaceVariant | `#1F2A33` / `#5B6670` | `#E4EBE5` / `#A7B3AB` |
| outline / outlineVariant | `#77827B`* / `#D5DED3` | `#7C8981` / `#3A463F` |
| error / errorContainer | `#C62828` / `#FDECEA` | `#FF9A8F` / `#5C1A16` |
| success (+ container) | `#2E7D32` (`#E3F0DF`) | `#8FCB8A` (`#24452A`) |
| warning (+ container) | `#8A5A00` (`#FFF4DB`) | `#F2C266` (`#4A3500`) |
| brandGreen | `#559058` (solo ilustración) | `#559058` |

\* El `outline` claro propuesto (`#8A958E`) no llegaba a 3:1 como borde; se oscureció a `#77827B`.
Los colores extra (éxito, advertencia, salud, tareas, traslados, series de gráficas, foco, vidrio, QR, cámara…) están en `GreenTechExtendedColors` y cambian con el tema. Las primitivas del logo están en `PaletaGreenTech`.

**Contraste:** `composeApp/src/commonTest/.../ContrasteTest.kt` calcula el contraste WCAG de los pares texto/fondo de
ambos temas (4.5:1 en texto, 3:1 en íconos grandes y bordes), incluido el peor caso del vidrio. Ejecútala con
`./gradlew :composeApp:testDebugUnitTest`.

### Componentes principales

| Componente | Uso |
|---|---|
| `GlassSurface` | Vidrio ligero (Haze, desenfoque 22 dp, tinte surface 78 % / 70 %, borde 1 dp). Solo en elementos flotantes |
| `BentoTile` / `BentoGrid` / `bento()` | Mosaicos S (1×120), M (2×120) y L (2×200) en cuadrícula de 2 columnas (4 en tablet) |
| `GtButton` | Pill de 60 dp con ícono, morph al presionar, carga con `LoadingIndicator` y check tras guardar |
| `FormularioScaffold` | Patrón único de formulario: X, subtítulo de contexto, "* Obligatorio", botón fijo, "¿Descartar cambios?" |
| `ConfirmSheet` + `ResumenFila` | Confirmación en hoja inferior con resumen de lo que va a pasar |
| `BotonInfo` / `HojaInfo` | Ayuda contextual: ícono Info → explicación breve + "Ver más en Ayuda" |
| `FiltrosActivos` | "12 lotes · 2 filtros activos" + "Limpiar filtros" |
| `GtSegmentado` | ButtonGroup conectado (Venta/Merma, Activos/Inactivos, periodo, tema) |
| `GtFabMenu` | `FloatingActionButtonMenu` del admin |
| `GtTextField`, `GtStepper`, `GtChip*`, `GtBuscador` | Campos de 16 dp con sacudida al enviar con error; stepper con número animado |
| `GtBottomBar` | Cápsula flotante de vidrio con indicador pill que se desliza |
| `GtNotification` | Banner de vidrio con borde de color por tipo e ícono animado |
| `charts/` | `Sparkline`, `BarChart`, `HorizontalBarChart`, `LineChart`, `Gauge`: Canvas minimalista, un grosor, colores por serie (`colors.series`), etiqueta directa, tooltip al tocar y una sola entrada animada |
| `PlantHealthIndicator` / `BotonLeyendaSalud` | Salud vegetal con color + ícono + forma, y su leyenda |
| `LoteCard` | Tarjeta de lote v2 (normal, compacta, destacada) |
| `VacioMosaico` | Estado vacío compacto dentro de mosaicos |

**Cómo usar `GlassSurface`:** el contenido que va detrás se marca con `Modifier.fuenteVidrio(hazeState)` y el
elemento flotante usa `GlassSurface(fuente = hazeState)`. `GtScaffold` ya lo hace para la barra superior, el panel
para la barra inferior y `App` para los banners. El vidrio no puede estar dentro de su propia fuente, y el texto
encima va en `onSurface`. Sin desenfoque disponible (Android < 12) usa surface al 94 %.

```kotlin
val haze = rememberHazeState()
Box {
    Contenido(Modifier.fuenteVidrio(haze))
    GlassSurface(Modifier.align(Alignment.BottomCenter), shape = GtShapes.pastilla, fuente = haze) {
        Text("Flotante", Modifier.padding(16.dp))
    }
}
```

**Cómo usar `BentoTile`:**

```kotlin
BentoGrid(contentPadding = PaddingValues(Dimens.margenLateral)) {
    bento(BentoSize.S, "lotes") {
        BentoTile(BentoSize.S, etiqueta = "Lotes activos", icono = IconosModulo.lote, onClick = { /* … */ }) {
            NumeroAnimado(10.0, GtTexto.dato)
        }
    }
    bento(BentoSize.L, "grafica") {
        BentoTile(BentoSize.L, etiqueta = "Ventas vs merma") { BarChart(etiquetas, series, descripcion = "…") }
    }
}
```

## Cómo probar cada flujo

**Empleado** (`empleado@gmail.com`)
- **Primer uso**: bienvenida de 3 tarjetas ("Registra tus lotes", "Escanea el QR para ver todo", "Lleva la bitácora del día").
- **Inicio**: saludo con "Registrar lote" y "Escanear QR", alerta de sensores, actividad reciente y sincronización.
- **Registrar lote**: especie (con buscador), zona (por defecto la del último lote), cantidad y fecha → "Lote registrado"
  → QR con "Guardar imagen", "Ver lote" y "Registrar otro lote". Sal con cambios para ver "¿Descartar cambios?".
- **Escanear**: vista de cámara con marco que "respira" y linterna. Sin cámara, usa **"Escribir código del lote"**
  (`GT-8F2A91C0D4`, o un código con otro formato o inexistente para ver los errores).
- **Detalle del lote** (subtítulo "Especie · código") → Etapa (con Info), Observación, Actividad, Condiciones, Anomalía
  (con Info; al guardar abre Recomendaciones), Recomendaciones, Bitácora (filtros + "Limpiar filtros") y Salida
  (con Info y resumen; intenta una cantidad mayor a la disponible). Al volver se resalta lo recién registrado.
- **Perfil**: Cuenta, Apariencia (Claro/Oscuro/Sistema y "Reducir animaciones"), Ayuda y soporte (Centro de ayuda,
  Ver introducción), Acerca de y Cerrar sesión (con confirmación).

**Administrador** (`admin@gmail.com`)
- **Dashboard** (bento): Info en "Indicadores", periodo Semana/Mes/Año con el rango exacto debajo, 4 KPI (toca cada uno
  para ver cómo se calcula), ventas y merma por mes, merma por especie, anomalías abiertas, Auditoría y accesos rápidos.
- **Personal**: buscador, Activos/Inactivos, botón "+" para "Agregar trabajador" (prueba un correo existente para ver
  el error; con datos válidos aparece un resumen antes de confirmar) y "Dar de baja" con hoja de confirmación.
- **Lotes**: supervisión en solo lectura (detalle, condiciones y bitácora sin botones de registro).
- **Auditoría**: desde Dashboard; se actualiza con las ventas, mermas, altas y bajas que hagas.

**Cuenta inactiva** (`inactivo@gmail.com`): el login muestra "Tu cuenta está desactivada. Contacta al administrador".

## Pendientes (TODO)

| Área | Archivo | Qué falta |
|---|---|---|
| Supabase | `shared/.../data/remote/SupabaseProvider.kt` | Crear el cliente (Auth, Postgrest, Realtime, Storage) |
| Supabase | `shared/.../data/remote/source/`, `dto/`, `data/mapper/` | Consultas, DTOs y mapeos reales |
| Supabase | `shared/.../data/repository/RepositoriosSupabase.kt` | Implementar los 6 repositorios reales |
| Supabase | `shared/.../di/SharedModule.kt` | Registrar las implementaciones y poner `USE_FAKE_DATA = false` |
| Offline | `shared/.../data/local/` (+ `androidMain`) | SQLDelight: base local, DAOs y drivers |
| Offline | `shared/.../data/sync/SyncManager.kt` | Sincronización y estado real para el `OfflineBanner` |
| Cámara | `composeApp/.../platform/CameraScanner.android.kt` / `.ios.kt` | CameraX + ML Kit / AVCaptureSession y linterna |
| QR | `composeApp/.../platform/QrSaver.android.kt` / `.ios.kt` | Guardar imagen en galería y compartir (hoy devuelven éxito sin guardar: el aviso "Imagen del QR guardada" aún no es real) |
| Fotos | `composeApp/.../employee/monitoreo/ObservacionLogic.kt` | Tomar foto real y subirla a Storage |
| Push | — | Notificaciones push (FCM) y Edge Functions (alta/baja de usuarios de Auth) |
| Material 3 | `gradle/libs.versions.toml` | Volver a una versión estable de `compose-material3` cuando publiquen las APIs Expressive (hoy 1.12.0-alpha03) |
| Gradle | `gradle.properties` | AGP 9 pide separar la app Android en un módulo `androidApp`; hoy se usan `android.builtInKotlin=false` y `android.newDsl=false` para conservar la estructura pedida |
