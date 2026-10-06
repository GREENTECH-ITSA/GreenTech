package com.greentech.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes

/**
 * Diálogo de confirmación para acciones destructivas o irreversibles. Superficie sólida (texto
 * legible), scrim con desenfoque del contenido de fondo y botones grandes apilados.
 */
@Composable
fun ConfirmDialog(
    titulo: String,
    mensaje: String,
    textoConfirmar: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
    peligro: Boolean = true,
    icono: ImageVector = Icons.Outlined.WarningAmber,
    cargando: Boolean = false,
) {
    RegistrarModal()
    BasicAlertDialog(onDismissRequest = { if (!cargando) onCancelar() }) {
        Surface(shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.surfaceContainerLow) {
            ContenidoConfirmacion(titulo, mensaje, textoConfirmar, onConfirmar, onCancelar, peligro, icono, cargando)
        }
    }
}

/**
 * Hoja inferior de confirmación (mismo contenido que [ConfirmDialog]). Entra con resorte y
 * desenfoca el fondo. Se usa en las acciones destructivas o irreversibles (salidas, bajas,
 * cerrar sesión, descartar cambios): el [mensaje] resume lo que va a pasar y [detalle] puede
 * mostrar un resumen con [ResumenFila].
 */
@Composable
fun ConfirmSheet(
    titulo: String,
    mensaje: String,
    textoConfirmar: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
    peligro: Boolean = false,
    icono: ImageVector = Icons.Outlined.WarningAmber,
    cargando: Boolean = false,
    textoCancelar: String = Textos.Comun.CANCELAR,
    detalle: @Composable ColumnScope.() -> Unit = {},
) {
    GtBottomSheet(onCerrar = { if (!cargando) onCancelar() }) {
        ContenidoConfirmacion(titulo, mensaje, textoConfirmar, onConfirmar, onCancelar, peligro, icono, cargando, detalle, textoCancelar)
    }
}

/**
 * Fila "etiqueta · valor" para el resumen de una confirmación. Con valores largos (correos)
 * la etiqueta va arriba y el valor abajo, para que no se parta en pedazos.
 */
@Composable
fun ResumenFila(etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    val base = modifier.fillMaxWidth().padding(vertical = 4.dp).semantics(mergeDescendants = true) {}
    if (valor.length > LARGO_MAXIMO_EN_LINEA) {
        Column(base) {
            Text(etiqueta, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valor, style = MaterialTheme.typography.titleSmall)
        }
        return
    }
    Row(base, verticalAlignment = Alignment.Top) {
        Text(
            etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            valor,
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1.4f),
        )
    }
}

private const val LARGO_MAXIMO_EN_LINEA = 22

/** Tarjeta con las filas del resumen, dentro de una hoja de confirmación. */
@Composable
fun ResumenConfirmacion(contenido: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = GtShapes.campo,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), content = contenido)
    }
}

@Composable
private fun ContenidoConfirmacion(
    titulo: String,
    mensaje: String,
    textoConfirmar: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
    peligro: Boolean,
    icono: ImageVector,
    cargando: Boolean,
    detalle: @Composable ColumnScope.() -> Unit = {},
    textoCancelar: String = Textos.Comun.CANCELAR,
) {
    Column(
        Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val colores = if (peligro) GreenTechTheme.colors.error else GreenTechTheme.colors.primario
        Box(
            Modifier.size(64.dp).clip(GtShapes.circulo).background(colores.fondo),
            contentAlignment = Alignment.Center,
        ) { Icon(icono, null, tint = colores.contenido, modifier = Modifier.size(32.dp)) }
        Text(
            titulo,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            mensaje,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        detalle()
        Spacer(Modifier.height(4.dp))
        GtButton(
            texto = textoConfirmar,
            onClick = onConfirmar,
            variante = if (peligro) GtButtonVariant.Peligro else GtButtonVariant.Primario,
            cargando = cargando,
        )
        GtButton(
            texto = textoCancelar,
            onClick = onCancelar,
            variante = GtButtonVariant.Secundario,
            habilitado = !cargando,
        )
    }
}

/** Hoja inferior de GreenTech UI: 32 dp de radio superior, superficie sólida y fondo desenfocado. */
@Composable
fun GtBottomSheet(onCerrar: () -> Unit, contenido: @Composable ColumnScope.() -> Unit) {
    RegistrarModal()
    ModalBottomSheet(
        onDismissRequest = onCerrar,
        sheetState = rememberBottomSheetState(SheetValue.Hidden, enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)),
        shape = GtShapes.hoja,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        scrimColor = GreenTechTheme.colors.velo.copy(alpha = 0.32f),
    ) {
        Column(Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.navigationBars), content = contenido)
    }
}
