# Rendimiento de GreenTech

Optimización de rendimiento, fluidez y estabilidad de la app Android (Kotlin + Compose Multiplatform).
No se tocaron Supabase, la sincronización ni los repositorios Fake. Este documento reúne cómo se midió,
qué se cambió, los resultados y el cumplimiento de los requisitos no funcionales (RNF) del SRS.

> En el teléfono de gama media se midió el **antes y el después en el mismo equipo**. La gama baja se midió
> en emuladores (no hay un teléfono de entrada disponible); esos números se comparan contra las **metas del
> RNF**, no contra el teléfono.

## 1. Equipos y método

| Perfil | Equipo | Uso |
|---|---|---|
| Gama media (antes y después) | Xiaomi 22101320G, Android 12 (API 31), 8 GB, 120 Hz | Arranque, frames, memoria, cámara y 40 consultas QR |
| API reciente (después) | Emulador Pixel 7, Android 14 (API 34), 2 GB, 4 vCPU, 1080 × 2400 | Generación del Baseline Profile, arranque, frames, memoria y cámara |
| Gama baja (después) | Emulador "GamaBaja_API30", Android 11 (API 30), 2 GB, 2 vCPU, 360 × 640 dp | Arranque, frames, memoria y las 40 consultas QR |
| Compatibilidad | Emulador "GamaBaja_API26", Android 8.0 (API 26), 2 GB, 2 vCPU | Verificación funcional, lectura de QR, monkey, LeakCanary |

Herramientas (fase 1):

- **Macrobenchmark** (`:baselineprofile`, build `benchmarkRelease` con R8): `StartupTimingMetric`,
  `FrameTimingMetric`, `MemoryUsageMetric` y `TraceSectionMetric` para las marcas de la consulta QR.
  Los recorridos escriben en los campos con `UiObject2.setText` (sin teclado).
  - Compilación: se prepara como una instalación real (ProfileInstaller + `speed-profile`) y se mide con
    `CompilationMode.Ignore()`, porque MIUI bloquea la reinstalación por USB que hace `Partial`.
- **Marcas de la consulta QR** (`platform/Medicion.kt`): secciones async de Perfetto, también en logcat
  (`adb logcat -s GtMedicion`):
  - `qr_consulta` = T0 (código leído o confirmado) → T2 (primer frame del Detalle con datos).
  - `qr_busqueda` = T0 → T1 (se tiene el lote).
  - `qr_abrir_a_detalle` = se abre Escanear → T2.
  - `qr_camara_lista` = se liga la cámara → primer cuadro en el analizador.
- **Informes del compilador de Compose**: `./gradlew :composeApp:assembleRelease -PinformesCompose`.
- **JankStats** (debug, `adb logcat -s GtJank`, con la pantalla en la que ocurrió), **StrictMode**
  (debug) y **LeakCanary** (solo `debugImplementation`).
- **LecturaQrTest** (prueba instrumentada): el PNG que exporta la app se pasa al mismo lector de ML Kit
  en cuadros de 1280 × 720 que simulan las condiciones de campo.

Comandos:

```
./gradlew :composeApp:generateBaselineProfile          # Baseline + Startup Profile (API 33+)
./gradlew :baselineprofile:connectedBenchmarkReleaseAndroidTest
./gradlew :composeApp:connectedDebugAndroidTest         # LecturaQrTest
./gradlew :composeApp:assembleRelease -PinformesCompose # informes de estabilidad
```

## 2. Resultados

Mediana / P90 salvo que se indique. "—" = no se midió en ese equipo.

**Cómo leer las tablas:**
- **Teléfono:** el "antes" (commit `00bedcf`: solo la infraestructura de medición) y el "después" (`f2fe88f`) se midieron en el **mismo teléfono**, así que son comparables.
- **Emuladores:** sus números solo se comparan contra las metas. Son más lentos que un equipo real de su RAM: usan GPU emulada y la PC estaba cargada (Android Studio abierto), y varían bastante entre corridas.

### 2.1 Arranque (hasta ver el Login)

`timeToInitialDisplay` = primer frame. `timeToFullDisplay` = Login dibujado (`reportFullyDrawn`).

| Escenario | Teléfono antes | Teléfono después | Gama baja API 30 (después) | API 34 emulado (después) |
|---|---|---|---|---|
| Frío: primer frame | 380 / 393 ms | **360 / 390 ms** | 496 / 646 ms | 984 / 1513 ms (n = 6) |
| Frío: Login visible | 1626 / 1639 ms | **1595 / 1615 ms** | 1656 / 1741 ms (P95 1875) | 2003 / 2410 ms (n = 6) |
| Caliente: primer frame | 104 / 126 ms | 115 / 127 ms | 150 / 262 ms | 405 / 672 ms (n = 5) |
| Caliente: Login visible | 1427 / 1442 ms | 1419 / 1434 ms | 1392 / 1481 ms | 1585 / 1769 ms (n = 5) |

El arranque en API 34 se midió mientras corría a la vez el emulador de gama baja (PC saturada): Macrobenchmark descartó iteraciones (n = 5–6). Hay que repetirlo con el emulador solo.

Del Login a ~1.6 s, **1.3 s son la espera fija del splash animado** (`SplashLogic.DURACION_MS`, parte del diseño). El arranque real hasta el primer frame es de 0.36 s en el teléfono y 0.5 s en gama baja.

### 2.2 Frames por escenario

| Escenario | Teléfono antes (120 Hz) | Teléfono después (120 Hz) | Gama baja API 30 (después, 60 Hz) | API 34 emulado (después, 60 Hz) |
|---|---|---|---|---|
| Scroll de la lista de lotes | CPU por frame P50 4.7 / P90 5.6 ms; retraso P95 7.1 ms (≈ 5–10 % lentos) | **7.4 % lentos**; CPU P50 6.4 / P90 8.0 ms | 98.9 % lentos (CPU > 16.7 ms); CPU P50 58 / P90 73 ms | 73.4 % lentos; CPU P50 20 / P90 34 ms |
| Abrir el Detalle y regresar | — | **21.5 % lentos**; CPU P50 6.1 / P90 8.1 ms | 92.7 % lentos; CPU P50 52 / P90 69 ms. En una corrida con menos carga: P50 12 / P90 27 ms | 78.4 % lentos; CPU P50 20 / P90 27 ms |
| Login → Inicio | — | 25.2 % lentos; total 2470 / 2596 ms (P95 2644)¹ | pendiente² | 92.7 % lentos; total 2454 / 2612 ms¹ |
| Dashboard del administrador (gráficas + scroll) | — | 44.0 % lentos; CPU P50 5.8 / P90 11.2 ms | pendiente² | 66.2 % lentos; CPU P50 20 / P90 33 ms |
| Abrir Escanear (inicio de la cámara) | retraso P90 −1.4 / P95 9.6 ms (≈ 5–10 % lentos) | 8.7 % lentos; CPU P50 6.6 / P90 10.6 ms | 73.3 % lentos; CPU P50 29 / P90 70 ms | 55.5 % lentos; CPU P50 9.4 / P90 31 ms |
| Frame más largo | — | 131 ms (Login → Inicio) | 572 ms (scroll) | 295 ms |

"Lento" significa:
- **Teléfono:** el frame no se entregó a tiempo (`frameOverrunMs` > 0; presupuesto de 8.3 ms a 120 Hz).
- **API 30:** CPU > 16.7 ms (Macrobenchmark no da `frameOverrun` antes de API 31).
- **API 34 emulado:** `frameOverrunMs` > 0. El emulador renderiza 1080 × 2400 con GPU emulada, así que sus frames no representan un Pixel real; sirven para confirmar que los recorridos funcionan y para comparar contra futuras corridas en el mismo emulador.

¹ Incluye los 600–900 ms de latencia simulada del repositorio Fake de login y las cargas del Inicio.
² En el emulador de gama baja la UI no respondió a tiempo en estas dos pruebas (sin cierres de la app).

### 2.3 Consulta por QR (RNF 3.3.1)

Marcas de `Medicion.kt` registradas en logcat:
- **Teléfono:** 40 consultas con `consultaPorCodigo`.
- **API 30:** 40 consultas.
- **Cámara lista:** 39 aperturas de Escanear en API 30 y 50 en el teléfono.

| Tramo | Teléfono antes | Teléfono después | Gama baja API 30 (después) |
|---|---|---|---|
| T0 → T1 (código → lote) | 736 ms (n = 1)³ | **0 / 0 ms**, máx. 1 | **0 / 0 ms**, P95 2, máx. 27 |
| T0 → T2 (código → Detalle dibujado) | 1660 ms (n = 1)³ | **80 / 90 ms, P95 97**, máx. 118 | **230 / 702 ms, P95 849**, máx. 1378 |
| Cámara lista (ligar → primer cuadro analizado) | 441 / 509 ms | 482 / 496 ms, P95 508 | 525 / 844 ms, P95 1033, máx. 8171 |
| Decodificación de un cuadro | — | — | 90 ms; P95 99 (Android 8, LecturaQrTest) |
| Cámara lista en API 34 emulado | — | 550 / 870 ms, P95 1107 (n = 10) | — |

³ Antes, la consulta esperaba al repositorio Fake (600–900 ms) y el Detalle volvía a pedir el lote. En Android 8, antes: T0→T2 = 1795 ms.

**Abrir Escanear → Detalle con la cámara.** No se puede medir de punta a punta en un emulador porque la cámara no ve un QR real. Se estimó combinando las distribuciones medidas: cámara lista + decodificación + 250 ms del check de confirmación (diseño) + T0→T2.

| Equipo | Mediana | P95 | Consultas ≤ 2 s |
|---|---|---|---|
| Teléfono (gama media) | ≈ 0.9 s | ≤ 0.95 s (suma de los P95) | ~100 % |
| Gama baja API 30 (emulador) | 1.15 s | **2.18 s** (1.91 s sin el valor atípico de 8.2 s de la cámara) | 93.6 % |

Las consultas por código en Android 8 se verificaron funcionalmente (sección 7), sin benchmark.

### 2.4 Memoria

| Momento | Teléfono antes | Teléfono después | Gama baja API 30 (después) | API 34 emulado (después) |
|---|---|---|---|---|
| Inicio en reposo | RSS anónima 42.4 MB; heap 8.2 MB | RSS anónima 51.3 MB; heap 16.3 MB (n = 1)⁴ | RSS anónima 57.9–59.5 MB; heap 10.4–11.4 MB | RSS anónima 52.1 MB; heap 7.1 MB |
| Dashboard | — | RSS anónima 40.2 MB; heap 8.9 MB | pendiente² | RSS anónima 53.7 MB; heap 7.9 MB |
| Emblema decodificado | 4 MB (1024 px) | 1 MB (512 px) | 1 MB | 1 MB |

⁴ Una sola muestra válida; el heap de Java varía mucho según cuándo pasa el recolector. Hay que repetirla.

### 2.5 Tamaño de la APK release (R8 + shrinkResources, sin firmar)

| APK | Antes | Después |
|---|---|---|
| Universal | 26.54 MB | 25.24 MB |
| arm64-v8a (la mayoría de teléfonos) | — (solo había universal) | 9.69 MB |
| armeabi-v7a (gama baja de 32 bits) | — | 8.00 MB |
| x86_64 / x86 (emuladores) | — | 10.69 MB / 10.88 MB |

La mayor parte es `libbarhopper_v3.so` de ML Kit: 3.2–6.1 MB por ABI. Con App Bundle (`bundleRelease`), Play entrega solo la ABI y la densidad del equipo.

## 3. Cumplimiento de RNF

| RNF | Requisito | Evidencia | Resultado |
|---|---|---|---|
| 3.3.1 | 95 % de las consultas por QR muestran el lote en ≤ 2 s (local o con conexión estable) | **Gama media (teléfono):** T0→T2 con P95 de 97 ms en 40 consultas; cámara lista con P95 de 508 ms; abrir Escanear → Detalle estimado en P95 ≤ 0.95 s. **Gama baja (emulador API 30, 2 GB, 2 vCPU):** T0→T2 con P95 de 849 ms; abrir → Detalle estimado en P95 2.18 s (93.6 % ≤ 2 s). La búsqueda es local (P95 2 ms) y no usa la red. | **Cumple en gama media. No cumple (estimado) en el emulador de gama baja**, por 0.18 s. |
| 3.3.4 | Capturar datos y escanear el 100 % del tiempo aunque el servidor no responda | En Android 8 sin Wi-Fi ni datos: la app arranca, inicia sesión y abre Escanear, y el banner "Sin conexión" aparece de inmediato. La consulta QR resuelve con el índice local, sin red. Nada del arranque ni del escaneo llama a la red (los repositorios son locales). | **Cumple** en lo verificado. **Pendiente:** prueba manual en modo avión con captura de datos (§7). |
| 3.3.3 (app) | Operación continua sin fallos por fugas de memoria | LeakCanary: 0 objetos retenidos tras 5000 eventos de monkey en Android 8. Monkey: 0 cierres y 0 ANR. Ningún frame > 700 ms en las corridas medidas. | **Cumple** en lo medido. **Pendiente:** 30 min de uso continuo con el Memory Profiler. |
| 3.3.6 | Android 8.0 o superior, equipos de entrada | `RecorridoBasicoTest` pasa en Android 8.0 con 2 GB: abre, inicia sesión, Escanear con cámara y Detalle. `LecturaQrTest` y monkey también pasan en Android 8.0. minSdk 26. | **Cumple** |
| 3.3.6 | QR estándar, imprimible en papel, plástico o metal, legible con cualquier cámara de 5 MP | ISO/IEC 18004, nivel Q, negro sobre blanco, zona de silencio de 4 módulos, PNG ≥ 1024 px. Se lee en todas las condiciones de §5 (3, 5 y 8 cm, poca luz, reflejo, giro, inclinación de 30°, mancha). Análisis a 1280 × 720 con mínimo de 640 × 480, enfoque continuo y por toque. | **Cumple** (prueba instrumentada). **Pendiente:** prueba física con QR impresos. |

**Metas internas de esta tarea** (además del SRS):

| Meta | Resultado | Cumple |
|---|---|---|
| Consulta QR: P95 ≤ 2 s, también en gama baja | Gama media ≤ 0.95 s; gama baja (emulador) 2.18 s estimado | Gama media sí; gama baja **no** |
| T0 → T2 con P95 ≤ 500 ms | Teléfono 97 ms; gama baja (emulador) 849 ms | Gama media sí; gama baja **no** |
| Arranque en frío ≤ 1.5 s hasta ver el Login | 1.6 s (teléfono y gama baja). El primer frame llega a 0.36–0.5 s; el resto es la espera fija de 1.3 s del splash | **No** (por el diseño del splash, ver §9) |
| ≥ 95 % de frames a tiempo en scroll y transiciones | Teléfono a 120 Hz: scroll 92.6 %, Escanear 91.3 %, Detalle 78.5 % | **No** |
| Sin congelamientos > 700 ms | Frame más largo: 131 ms en teléfono, 572 ms en gama baja | **Sí** |
| Sin fugas de memoria | LeakCanary: 0 | **Sí** |
| APK lo más pequeña posible sin quitar funciones | Universal 26.5 → 25.24 MB; arm64 9.69 MB | **Sí** |

**Causas y plan para lo que no cumple:**

1. **QR y frames en gama baja.** Los números salen de un emulador de 2 vCPU con GPU emulada y la PC cargada; el mismo escenario dio CPU por frame P50 de 12 ms en una corrida y de 52 ms en otra.
   - Plan: repetir en un teléfono real de entrada (Android 8–11, 2 GB) con `ConsultaQrBenchmark` y una lectura de QR impreso.
   - Si sigue arriba de 2 s, la palanca más directa es el **check de 250 ms** antes de navegar (`EscanerQrLogic`): bajarlo o navegar mientras se dibuja daría ~1.9 s de P95. Es un cambio visible, decisión de diseño.
2. **Frames a 120 Hz en el Detalle y el Dashboard.** La CPU por frame tiene P90 de 8–11 ms, justo arriba del presupuesto de 8.3 ms.
   - Siguiente paso: perfilar la transición al Detalle y la entrada de las gráficas con Perfetto (las marcas y JankStats ya están) y escalonar la entrada de los mosaicos.
   - A 60 Hz (presupuesto de 16.7 ms) esos mismos tiempos quedan dentro.
3. **Login a 1.6 s.** La espera de 1.3 s del splash es de diseño (§9).

## 4. Optimizaciones aplicadas

| # | Optimización | Dónde | Impacto medido |
|---|---|---|---|
| 1 | Baseline Profile + Startup Profile (38 188 reglas) incluidos con ProfileInstaller | `composeApp/src/androidRelease/generated/baselineProfiles` | `baseline.prof` en la APK pasa de 11 KB (solo bibliotecas) a 15.5 KB; arranque en la tabla 2.1 |
| 2 | ML Kit fuera del arranque: se quita `MlKitInitProvider` y se inicializa al abrir Escanear | `AndroidManifest.xml`, `CameraScanner.android.kt` | Un ContentProvider menos antes de `Application.onCreate` |
| 3 | Precalentamiento de ML Kit al ligar la cámara | `CameraScanner.precalentarLector` | La primera decodificación tardaba 1044 ms en Android 8; las siguientes, 90–110 ms |
| 4 | `Application.onCreate`: canal de notificaciones en otro hilo; precarga asíncrona de SharedPreferences | `GreenTechApplication.kt` | Sin trabajo de disco en el hilo principal antes del primer frame (StrictMode) |
| 5 | Splash Screen API con el emblema y el fondo del tema (también en Android 8–11) | `themes.xml`, `MainActivity` | Sin ventana en blanco al arrancar |
| 6 | Emblema 1024 px PNG → WebP 512 px; íconos del launcher en WebP; se quitan `logo.jpg` y `logo_icon.png` (sin uso) | `composeResources/drawable`, `res/mipmap-*` | Emblema decodificado: 4 MB → 1 MB; −0.86 MB de imágenes en la APK |
| 7 | Inter en subconjunto latino (español, signos, flechas), los 4 pesos que se usan, con `tnum` | `composeResources/font` | 1.67 MB → 1.05 MB sin comprimir |
| 8 | Fondo del Login: la fase se lee al dibujar y el bucle empieza tras el primer frame; quieto en nivel bajo | `LoginScreen.kt` | Antes recomponía la pantalla 60 veces por segundo durante 20 s por ciclo |
| 9 | Valores animados leídos en layout/dibujo: pill de la barra inferior (`offset {}`), giro de sincronización, brillo del skeleton, visor del escáner, velo modal (`graphicsLayer`), espacio de la barra inferior (`State` leído al medir) | `GtBottomBar`, `OfflineBanner`, `LoadingSkeleton`, `EscanerQrScreen`, `GlassSurface`, `GtScaffold`, `AppNavGraph` | El espacio de la barra recomponía todo el panel en cada frame al entrar o salir de una pestaña; ahora solo vuelve a hacer layout |
| 10 | Estabilidad: `@Immutable` en los 25 UiState y en `EstadoCarga`; archivo de estabilidad para los modelos de dominio y kotlinx-datetime | `*Logic.kt`, `compose-stability.conf` | 243 composables reiniciables, **0 no saltables** (strong skipping activo) |
| 11 | Lista de lotes: filtrado y especies en `Dispatchers.Default` con `ImmutableList`; Bitácora, Auditoría y Trabajadores calculan su filtro una vez por estado | `ListaLotesLogic`, `BitacoraLogic`, `AuditoriaLogic`, `TrabajadoresLogic` | El filtro ya no corre en cada recomposición |
| 12 | `contentType` en todos los ítems lazy; textos de `LoteCard` memorizados | listas y `BentoTile`, `LoteCard` | Reutilización correcta de ítems en el scroll |
| 13 | Gráficas con `drawWithCache` (rutas, escalas, `TextMeasurer`); resúmenes memorizados | `LineChart`, `BarChart`, `Sparkline` | La animación de entrada ya no reconstruye rutas ni mide texto en cada frame |
| 14 | Niveles de rendimiento (`NivelRendimiento`: `isLowRamDevice`, memoria, núcleos, `core-performance`) y respeto de la escala de animación del sistema | `platform/NivelRendimiento*`, `Motion` | Ver sección 6 |
| 15 | Índice local de lotes por código y por id: la consulta QR no espera al repositorio y el Detalle se dibuja con datos locales mientras refresca en segundo plano | `ui/core/IndiceLotesLocal.kt`, `EscanerQrLogic`, `DetalleLoteLogic` | `qr_busqueda` (T0→T1) en tabla 2.3 |
| 16 | Cámara: resolución de análisis por rango (1280 × 720 objetivo, mínimo 640 × 480), toque para enfocar | `CameraScanner.android.kt` | `qr_camara_lista` en tabla 2.3 |
| 17 | QR estándar e imprimible (sección 5) | `QrEstandar.kt`, `QrSaver.android.kt`, `QrLoteScreen.kt` | LecturaQrTest |
| 18 | `MonitorConexion` (NetworkCallback en su propio HandlerThread) alimenta el banner "Sin conexión" | `platform/MonitorConexion*`, `EmployeeHomeLogic` | El banner cambia en cuanto cambia la red (verificado en Android 8) |
| 19 | Borradores de formularios en `SavedStateHandle` (sobreviven a rotación, segundo plano, "No conservar actividades" y muerte del proceso) | `BaseLogic.conservarBorrador` y 7 formularios | — |
| 20 | Adaptación: Navigation Rail (≥ 600 dp), lista-detalle de lotes (≥ 840 dp), ancho máximo de 840 dp, orientación libre, gesto atrás predictivo | `Adaptable.kt`, `AppNavGraph`, `Navegador`, manifest | — |
| 21 | Tamaño: APKs por ABI + universal, solo recursos en español, R8 completo (predeterminado en AGP 9) | `composeApp/build.gradle.kts` | Tabla 2.5 |
| 22 | Escanear en pantallas bajas: el visor se reduce cuando no cabe todo, para que la instrucción y "Escribir código del lote" no queden bajo la barra inferior ni la del sistema (encontrado en 360 × 640 dp) | `EscanerQrScreen.kt` | En 412 × 915 dp o más se ve igual que antes |
| 23 | Prueba funcional `RecorridoBasicoTest` (abre, entra, Escanear, Detalle) para cualquier versión, con texto por accesibilidad | `androidInstrumentedTest/.../recorrido` | Pasa en Android 8.0 |

Ya estaba bien antes y se conservó: `collectAsStateWithLifecycle` en todas las pantallas (no hay ningún
`collectAsState()`), CameraX ligada al ciclo de vida con `STRATEGY_KEEP_ONLY_LATEST`, análisis fuera del
hilo principal, linterna que se apaga al salir y escáner de ML Kit limitado a `FORMAT_QR_CODE`.

### Clases de inestables a estables

Con `@Immutable` y el archivo de estabilidad pasan a estables todos los `*UiState`
(`EmployeeHomeUiState`, `ListaLotesUiState`, `DetalleLoteUiState`, `EscanerQrUiState`, `DashboardUiState`,
`AdminHomeUiState`, `BitacoraUiState`, `AuditoriaUiState`, `TrabajadoresUiState`, los de los 9 formularios,
Login, Perfil, Ayuda, Introducción y Recomendaciones), `EstadoCarga` y los modelos de dominio
(`Lote`, `Especie`, `Zona`, `EventoBitacora`, `CondicionesLote`, etc.).

Las 30 clases que el informe marca inestables después son ViewModels (`*Logic`), `MainActivity`,
`GreenTechApplication` y servicios (`CameraScanner`, `Navegador`, `NotificationManager`,
`IndiceLotesLocal`, `HapticaVibrador`). No son estado de UI. Solo `CameraScanner` y `Navegador` llegan
como parámetro a un composable, y se crean una vez por destino.

## 5. QR estándar e imprimible (RNF 3.3.6)

- **Formato:**
  - ISO/IEC 18004 con solo el código del lote (`GT-XXXXXXXXXX`), en modo alfanumérico.
  - Versión 1 (21 × 21 módulos) con corrección de errores **Q (25 %)**.
- **Aspecto:**
  - Módulos cuadrados en **negro puro sobre blanco puro**, también en tema oscuro.
  - Sin logo encima.
  - Zona de silencio de **4 módulos**.
- **Exportación:**
  - PNG sin pérdida con módulos de 40 px enteros: 1160 × 1280 px, ≈ 370 dpi impreso a 8 cm.
  - El código va debajo, por si el QR se daña.
- **Nota en pantalla:** "Imprime el QR de al menos 3 × 3 cm".

Lectura del PNG exportado con el lector de la app (ML Kit, solo QR), en Android 8 con 2 GB:

| Condición (cuadro de 1280 × 720) | Resultado |
|---|---|
| 3 cm, 5 cm, 8 cm impresos | Lee |
| Poca luz con linterna (45 % de brillo + ruido) | Lee |
| 3 cm + poca luz | Lee |
| Reflejo de luz sobre el código | Lee |
| Giro de 12° | Lee |
| Etiqueta inclinada 30° frente a la cámara, también con giro de 12° | Lee |
| Mancha de barro sobre ≈ 8 % del código | Lee |
| Límite de inclinación | Lee hasta 20–30°; falla desde 35° (igual con QR de 150, 250 o 400 px: es geométrico, no de resolución) |
| Decodificación de un cuadro (40 repeticiones) | mediana 90 ms, P95 99 ms, máx. 106 ms |

**Prueba física pendiente:** probar con QR impresos (3, 5 y 8 cm) en papel, plástico y metal y una
cámara de 5 MP. La prueba instrumentada no modela el desenfoque por movimiento ni el ruido real del sensor.

## 6. Cambios visibles por nivel de dispositivo

Solo en **nivel bajo** (`isLowRamDevice`, < 3 GB de RAM, ≤ 4 núcleos, o Android < 12 con < 4 GB):

- **Desenfoque y sombras:**
  - Vidrio sin desenfoque real: superficie semi-opaca al 94 %, la misma que ya se usaba en Android < 12.
  - Sin desenfoque detrás de hojas y diálogos (queda el scrim).
  - Sin sombra en la barra inferior flotante.
- **Animaciones en bucle:**
  - Fondo del Login quieto.
  - Skeleton sin brillo animado.
  - Visor del escáner sin "respiración".
- **Entradas escalonadas** de listas más cortas: la mitad de escalones y 100 ms en lugar de 300 ms.

Con **"Reducir animaciones"** activo (en la app o con la escala de animación del sistema en 0):

- Animaciones en 0 ms.
- Vidrio semi-opaco en lugar de desenfoque real, en cualquier nivel.

En nivel medio y alto todo se ve como estaba diseñado.

Otros cambios visibles que pide la tarea, en todos los equipos:

- **QR:** estándar negro sobre blanco, sin logo ni esquinas redondeadas ni acento verde, con la nota de impresión (RNF 3.3.6).
- **Pantallas grandes:** Navigation Rail en ventanas ≥ 600 dp y lista-detalle de lotes en ≥ 840 dp.
- **Orientación:** la app ya gira; Escanear queda en vertical solo en teléfonos.

## 7. Pruebas de estabilidad y compatibilidad

| Prueba | Resultado |
|---|---|
| Android 8.0 (API 26, 2 GB) | `RecorridoBasicoTest` **pasa**: abre, inicia sesión (texto con `setText`, sin teclado), abre Escanear con la cámara y muestra el Detalle. `LecturaQrTest` pasa |
| `adb shell monkey -p com.greentech.app.debug --throttle 200 -v 5000` (Android 8) | 5000 eventos, **0 cierres, 0 ANR** |
| LeakCanary (debug, Android 8, tras el monkey) | Análisis del heap: **0 rutas a objetos retenidos** (sin fugas) |
| Sin red (Android 8, Wi-Fi y datos apagados) | El banner muestra "Sin conexión · 2 registros por enviar" de inmediato; arranque, sesión y Escanear funcionan |
| Lint (`:composeApp:lintRelease`) | **0 errores**, 25 advertencias (ver §9) |
| `./gradlew :composeApp:assembleRelease` | Compila (APK universal + por ABI) |
| Frames > 700 ms (congelamientos) | Ninguno; máximo 131 ms en el teléfono, 295 ms en API 34 y 572 ms en el emulador de gama baja |

Bug del sistema encontrado en Android 8.0 (no de la app):

- **Síntoma:** `NullPointerException` en `ViewRootImpl.onAccessibilityStateChanged`.
- **Cuándo ocurre:** si el estado de accesibilidad cambia justo mientras se destruye la ventana de una hoja inferior. Lo dispara `uiautomator dump` al cerrar la hoja "Escribir código del lote".
- **Corrección:** Google lo corrigió en Android 8.1.
- **Riesgo para usuarios:** solo si activan o desactivan TalkBack en ese instante.

### Prueba en modo avión (a mano)

1. Instala el build (`./gradlew :composeApp:installDebug`), abre la app e inicia sesión con conexión.
   Luego ciérrala desde Recientes.
2. Activa el **modo avión** y abre la app: debe llegar al Login sin quedarse cargando. Inicia sesión
   (con los datos Fake la sesión vive en memoria, así que hay que volver a entrar).
3. En **Inicio**, baja hasta el banner: debe decir "Sin conexión" de inmediato, sin congelar la pantalla.
4. Abre **Escanear**, lee un QR impreso (o "Escribir código del lote" → `GT-8F2A91C0D4`). Debe abrir el
   **Detalle del lote** en ≤ 2 s.
5. En el Detalle registra una **Observación** y una **Actividad** con "Guardar". Deben guardarse sin
   esperar a la red. Regresa al Detalle y comprueba que el registro aparece resaltado.

Resultado: _(anotar aquí)_

## 8. Pendientes para la fase de Supabase y seguridad

- **Sincronización (3.3.1):** sincronizar en segundo plano lotes de hasta 100 registros en menos de 5 s.
- **Cero pérdida de datos (3.3.3):** cola local persistente `pendiente_sync`.
  - Punto de conexión: `BaseLogic.conservarBorrador` (TODO) y `shared/data/sync/SyncManager`.
  - Los formularios ya guardan su borrador en `SavedStateHandle`.
- **Cifrado de la base local (3.3.2):** SQLCipher.
- **Volver a medir la consulta QR** con la base local real (SQLDelight/SQLCipher):
  - `IndiceLotesLocal` se sustituye por `SELECT … WHERE codigo_qr = ?` con índice único en `codigo_qr`.
  - Los números de este documento usan los repositorios Fake, que simulan 600–900 ms de red por llamada.
- **Sesión persistente:** con los Fakes la sesión vive en memoria. "Login con sesión guardada" sin red (RNF 3.3.4) queda para Supabase Auth con sesión en almacenamiento local.
- **Sincronizar sin red:** el botón "Sincronizar" del banner sigue simulado (sin cambios en esta tarea) y da la sincronización por completa aunque no haya red.

## 9. Otros pendientes y recomendaciones

- **Splash de 1.3 s:**
  - El splash animado de Compose espera 1.3 s fijos (diseño). Por eso el Login aparece a ~1.6 s aunque el primer frame llega a ~0.4 s.
  - Bajarlo a 0.8 s (lo que dura su animación) cumpliría la meta de 1.5 s. Es un cambio visible: decisión del equipo.
- **Pendiente – arranque en API 34:** repetir el arranque con el emulador solo (se midió con la PC saturada, n = 5–6).
- **Pendiente – gama baja:** en el emulador API 30 faltan Login → Inicio y Dashboard. Conviene repetir todo en un teléfono real de entrada.
- **Pendiente – uso prolongado:** 30 minutos navegando y escaneando con el Memory Profiler (RNF 3.3.3).
- **Pendiente – memoria del teléfono:** repetir "Inicio en reposo" (solo hubo una muestra válida).
- **Pendiente – informe de estabilidad de la línea base:** no se pudo generar el informe del compilador de Compose de "antes"; el de "después" está en la §4.
- **Pendiente – pruebas manuales:** modo avión con captura de datos (§7) y QR impresos en papel, plástico y metal (§5).
- **Advertencias de lint:** las 25 son existentes (p. ej. `InlinedApi` en `HapticaVibrador`, que ya protege por versión) y ninguna es error.
- **Revisión de pantallas:** a 200 % de fuente, en plegable y en tablet se revisaron por código (`imePadding`, `windowInsets`, `TextoAjustable`, ancho máximo). Falta un recorrido visual completo en esos perfiles.
