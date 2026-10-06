package com.greentech.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.greentech.app.ui.theme.Dimens

/** Título de sección con acción de texto opcional a la derecha. */
@Composable
fun SectionHeader(
    titulo: String,
    modifier: Modifier = Modifier,
    accion: String? = null,
    onAccion: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth().heightIn(min = Dimens.tactilMin).padding(top = Dimens.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            titulo,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f).semantics { heading() },
        )
        if (accion != null && onAccion != null) {
            TextButton(onClick = onAccion, modifier = Modifier.heightIn(min = Dimens.tactilMin)) {
                Text(accion, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
