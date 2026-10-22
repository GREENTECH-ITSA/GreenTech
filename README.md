# GreenTech
Repo para los avances del proyecto

## Código fuente

El código de la app Android está en la carpeta [`CodigoGreenTech/`](CodigoGreenTech/).

Para abrirlo en Android Studio: **File > Open** y selecciona la carpeta `CodigoGreenTech`
(no la raíz del repositorio). Para compilar desde la terminal:

```bash
cd CodigoGreenTech
./gradlew :composeApp:assembleDebug
```

`local.properties` (ruta del SDK) y las llaves de firma (`keystore.properties`, `*.jks`) no se
suben al repositorio; Android Studio genera `local.properties` al abrir el proyecto.
