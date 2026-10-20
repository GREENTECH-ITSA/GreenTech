package com.greentech.app.platform

import kotlinx.coroutines.flow.StateFlow

/**
 * Estado de la conexión a internet (RNF 3.3.4). Solo informa: nada en la app espera a la red para
 * arrancar, escanear o capturar datos. El banner "Sin conexión" del Inicio lo observa.
 */
expect class MonitorConexion() {
    /** true con una red que tiene internet validado. Empieza en true hasta conocer el estado real. */
    val enLinea: StateFlow<Boolean>
}
