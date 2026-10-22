package com.greentech.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme

/**
 * Anillo de foco visible (teclado, D-pad o lector de pantalla con teclado): borde de 3 dp en el color
 * `foco` (azul agua, ≥ 3:1 sobre todas las superficies) con la misma forma del componente.
 * Al tocar no aparece: el foco táctil no se marca.
 */
@Composable
fun Modifier.anilloFoco(interaccion: InteractionSource, forma: Shape): Modifier {
    val enfocado by interaccion.collectIsFocusedAsState()
    return if (enfocado) border(Dimens.anilloFoco, GreenTechTheme.colors.foco, forma) else this
}
