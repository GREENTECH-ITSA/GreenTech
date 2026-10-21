import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.androidx.baselineprofile)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            // El nombre por defecto ("GreenTech:composeApp") no es un nombre de archivo válido en Windows y
            // rompe los informes del compilador de Compose.
            if (project.hasProperty("informesCompose")) moduleName.set("composeApp")
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        optIn.addAll(
            "kotlin.time.ExperimentalTime",
            "androidx.compose.material3.ExperimentalMaterial3Api",
            "androidx.compose.material3.ExperimentalMaterial3ExpressiveApi",
            "androidx.compose.foundation.layout.ExperimentalLayoutApi",
        )
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared)

            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.ui.tooling.preview)

            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.navigation.compose)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.collections.immutable)

            implementation(libs.qrose)
            implementation(libs.haze)
            implementation(libs.haze.blur)
            implementation(libs.multiplatform.settings.no.arg)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        // Pruebas instrumentadas (lectura del QR exportado con ML Kit en condiciones difíciles).
        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.test.ext.junit)
            implementation(libs.androidx.test.runner)
            implementation(libs.androidx.uiautomator)
            implementation(libs.kotlin.test)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.camera.core)
            implementation(libs.androidx.camera.camera2)
            implementation(libs.androidx.camera.lifecycle)
            implementation(libs.androidx.camera.view)
            implementation(libs.mlkit.barcode.scanning)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.koin.android)
            // Rendimiento: instala el Baseline/Startup Profile, splash del sistema, niveles de dispositivo,
            // trazas de la consulta QR y JankStats (este último solo se activa en debug).
            implementation(libs.androidx.profileinstaller)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.androidx.core.performance)
            implementation(libs.androidx.tracing)
            implementation(libs.androidx.metrics.performance)
        }
    }
}

// Firma de release: keystore.properties en la raíz (no se versiona). Sin él, debug compila igual
// y assembleRelease genera una APK sin firmar.
val propiedadesFirma = Properties().apply {
    val archivo = rootProject.file("keystore.properties")
    if (archivo.exists()) archivo.inputStream().use { load(it) }
}

android {
    namespace = "com.greentech.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.greentech.app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true
    }
    signingConfigs {
        // Si el keystore indicado no existe (p. ej. una ruta temporal), se compila sin firmar en vez de fallar.
        val keystoreExiste = propiedadesFirma.getProperty("storeFile")?.let { rootProject.file(it).exists() } == true
        if (keystoreExiste) {
            create("release") {
                storeFile = rootProject.file(propiedadesFirma.getProperty("storeFile"))
                storePassword = propiedadesFirma.getProperty("storePassword")
                keyAlias = propiedadesFirma.getProperty("keyAlias")
                keyPassword = propiedadesFirma.getProperty("keyPassword")
            }
        }
    }
    // La app solo está en español: se descartan las traducciones de las bibliotecas.
    androidResources {
        localeFilters += listOf("es")
    }
    // APK por ABI para instalación directa (las librerías nativas de ML Kit pesan ~5 MB por ABI) además de
    // la universal. En Google Play se publica el App Bundle (bundleRelease), que ya reparte por ABI/densidad.
    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a", "armeabi-v7a", "x86_64", "x86")
            isUniversalApk = true
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.findByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "com.greentech.app.resources"
    generateResClass = always
}

// Firma de debug solo para las variantes que crea el plugin de Baseline Profile (benchmarkRelease y
// nonMinifiedRelease): así se pueden instalar para medir sin tocar la firma del release real.
androidComponents {
    onVariants(selector().all()) { variante ->
        if (variante.buildType == "benchmarkRelease" || variante.buildType == "nonMinifiedRelease") {
            variante.signingConfig.setConfig(android.signingConfigs.getByName("debug"))
        }
    }
}

baselineProfile {
    // El perfil se genera a mano (./gradlew :composeApp:generateBaselineProfile) y se versiona en
    // src/androidMain/generated/baselineProfiles; el build normal no lo regenera.
    automaticGenerationDuringBuild = false
    saveInSrc = true
    // Además del Baseline Profile, genera el Startup Profile (dex layout optimizado para el arranque).
    dexLayoutOptimization = true
}

// Informes del compilador de Compose (clases estables/inestables, funciones skippable):
//   ./gradlew :composeApp:assembleRelease -PinformesCompose
composeCompiler {
    stabilityConfigurationFiles.add(layout.projectDirectory.file("compose-stability.conf"))
    if (project.hasProperty("informesCompose")) {
        reportsDestination = layout.buildDirectory.dir("compose_reports")
        metricsDestination = layout.buildDirectory.dir("compose_metrics")
    }
}

dependencies {
    debugImplementation(libs.compose.ui.tooling)
    // Detección de fugas de memoria: solo en debug (se instala sola con su ContentProvider).
    debugImplementation(libs.leakcanary.android)
    baselineProfile(projects.baselineprofile)
}
