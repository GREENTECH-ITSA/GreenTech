package com.greentech.shared.data.local

/**
 * TODO(Offline): crear un NativeSqliteDriver:
 * `NativeSqliteDriver(GreenTechDatabase.Schema, "greentech.db")`
 */
actual class DatabaseFactory {
    actual fun nombreBaseDeDatos(): String = "greentech.db"
}
