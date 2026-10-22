# Reglas de R8 para el build de release de GreenTech.
# La mayoría de las bibliotecas traen sus propias reglas (consumer rules); aquí solo va lo que
# la app necesita además de eso.

# Trazas legibles en los reportes de errores.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- kotlinx.serialization (rutas type-safe de Navigation y DTOs) ---
-keepattributes *Annotation*, InnerClasses, Signature, Exceptions
-dontnote kotlinx.serialization.**
-keepclassmembers @kotlinx.serialization.Serializable class com.greentech.** {
    *** Companion;
    static ** INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclasseswithmembers class com.greentech.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.greentech.**$$serializer { *; }
-keepclassmembers class com.greentech.**$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}

# --- Koin (la DI se resuelve por KClass; se conservan los nombres de los Logic por claridad en errores) ---
-keepnames class * extends androidx.lifecycle.ViewModel
-dontwarn org.koin.**

# --- ML Kit Barcode Scanning (modelo empaquetado, usa JNI) ---
# Sus componentes se registran por reflexión desde MlKitInitProvider (meta-data del manifest):
# si R8 renombra cualquiera de ellos, la app se cierra al arrancar ("Unsatisfied dependency").
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_** { *; }
-dontwarn com.google.mlkit.**
-dontwarn com.google.android.gms.**

# --- CameraX (el proveedor de Camera2 se carga por reflexión) ---
-keep class androidx.camera.camera2.Camera2Config$DefaultProvider { *; }
-dontwarn androidx.camera.**
