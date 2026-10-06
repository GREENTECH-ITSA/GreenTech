package com.greentech.app.di

import com.greentech.shared.di.sharedModules
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

private var iniciado = false

/** Arranca Koin una sola vez con los módulos de shared (datos y casos de uso) y de la UI. */
fun iniciarKoin(configuracion: KoinAppDeclaration = {}) {
    if (iniciado) return
    iniciado = true
    startKoin {
        configuracion()
        modules(sharedModules + uiModule)
    }
}
