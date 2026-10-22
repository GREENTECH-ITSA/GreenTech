package com.greentech.app.platform

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.HandlerThread
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.mp.KoinPlatform

/**
 * ConnectivityManager.NetworkCallback en su propio HandlerThread: los avisos de red nunca ocupan el hilo
 * principal. Vive lo que vive la app (single de Koin), así que el callback no se desregistra.
 */
actual class MonitorConexion actual constructor() {
    private val _enLinea = MutableStateFlow(true)
    actual val enLinea: StateFlow<Boolean> = _enLinea.asStateFlow()

    init {
        val contexto = KoinPlatform.getKoin().get<Context>()
        val cm = contexto.getSystemService(ConnectivityManager::class.java)
        val hilo = HandlerThread("gt-red").apply { start() }
        val manejador = Handler(hilo.looper)
        manejador.post {
            _enLinea.value = cm.getNetworkCapabilities(cm.activeNetwork)?.tieneInternet() == true
        }
        cm.registerDefaultNetworkCallback(
            object : ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(network: Network, capacidades: NetworkCapabilities) {
                    _enLinea.value = capacidades.tieneInternet()
                }

                override fun onLost(network: Network) {
                    _enLinea.value = false
                }
            },
            manejador,
        )
    }

    private fun NetworkCapabilities.tieneInternet() =
        hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
