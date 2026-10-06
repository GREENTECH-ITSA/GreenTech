package com.greentech.app

import androidx.compose.ui.window.ComposeUIViewController
import com.greentech.app.di.iniciarKoin
import platform.UIKit.UIViewController

/** Punto de entrada para iOS: iosApp (SwiftUI) solo muestra este controlador. */
@Suppress("FunctionName", "unused")
fun MainViewController(): UIViewController {
    iniciarKoin()
    return ComposeUIViewController { App() }
}
