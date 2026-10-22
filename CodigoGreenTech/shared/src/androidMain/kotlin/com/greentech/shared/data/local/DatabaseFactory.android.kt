package com.greentech.shared.data.local

/**
 * TODO(Offline): recibir el Context de Android y crear un AndroidSqliteDriver:
 * `AndroidSqliteDriver(GreenTechDatabase.Schema, context, "greentech.db")`
 */
actual class DatabaseFactory {
    actual fun nombreBaseDeDatos(): String = "greentech.db"
}
