import UIKit
import SwiftUI
import ComposeApp

/// Solo muestra la UI compartida de Compose Multiplatform (MainViewController de composeApp/iosMain).
struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all)
            .preferredColorScheme(.light) // Solo tema claro: se lee mejor bajo el sol.
    }
}
