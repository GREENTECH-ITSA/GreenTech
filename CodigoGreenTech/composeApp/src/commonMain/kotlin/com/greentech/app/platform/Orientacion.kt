package com.greentech.app.platform

import androidx.compose.runtime.Composable

/**
 * Mientras esté en pantalla, fija la orientación vertical en teléfonos (en tablets y plegables abiertos
 * no hace nada). Lo usa Escanear: el visor y la lectura del QR funcionan mejor en vertical.
 */
@Composable
expect fun FijarVerticalEnTelefono()
