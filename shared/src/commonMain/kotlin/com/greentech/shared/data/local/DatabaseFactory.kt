package com.greentech.shared.data.local

/**
 * TODO(Offline): base de datos local con SQLDelight para trabajar sin internet.
 *
 * Pasos:
 * 1. Agregar el plugin app.cash.sqldelight y los drivers (android-driver, native-driver).
 * 2. Crear los archivos .sq en shared/src/commonMain/sqldelight/com/greentech/shared/db/.
 * 3. Implementar el actual de esta clase en androidMain (AndroidSqliteDriver con Context)
 *    y en iosMain (NativeSqliteDriver).
 */
expect class DatabaseFactory {
    /** TODO: devolverá un SqlDriver cuando se agregue SQLDelight. */
    fun nombreBaseDeDatos(): String
}
