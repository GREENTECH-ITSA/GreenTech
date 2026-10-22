// Módulo de rendimiento: genera el Baseline Profile / Startup Profile de :composeApp y contiene los
// Macrobenchmarks (arranque, scroll, navegación, consulta por QR). Se corre contra el build "benchmark"
// (release con R8, firmado con la llave de debug para poder instalarlo).
//
//   Generar perfiles:   ./gradlew :composeApp:generateBaselineProfile
//   Medir:              ./gradlew :baselineprofile:connectedBenchmarkReleaseAndroidTest
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.androidx.baselineprofile)
}

android {
    namespace = "com.greentech.baselineprofile"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        // Macrobenchmark necesita API 28+ para las métricas de frames; el generador de perfiles, API 28+.
        minSdk = 28
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // En emuladores se permite medir (los números de emulador se reportan aparte en RENDIMIENTO.md).
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR,LOW-BATTERY,DEBUGGABLE"
    }

    targetProjectPath = ":composeApp"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
}

baselineProfile {
    // Se usan los dispositivos conectados (teléfono físico o emulador encendido).
    useConnectedDevices = true
}

dependencies {
    implementation(libs.androidx.test.ext.junit)
    implementation(libs.androidx.test.runner)
    implementation(libs.androidx.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
}
