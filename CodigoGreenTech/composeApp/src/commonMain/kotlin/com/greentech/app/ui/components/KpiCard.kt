package com.greentech.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingFlat
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.components.charts.Sparkline
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.tabular
import com.greentech.app.util.formatoDecimal

/**
 * KPI en mosaico S: etiqueta, número protagonista con count-up (headlineMedium, tnum),
 * sparkline opcional y variación contra el periodo anterior con ícono de tendencia.
 *
 * @param mejorSiSube false para métricas donde subir es malo (merma): cambia el color de la variación.
 * @param info explicación de cómo se calcula: el mosaico entero se vuelve tocable (con un ícono Info
 * abajo a la derecha) y abre una hoja breve con "Ver más en Ayuda".
 */
@Composable
fun KpiTile(
    titulo: String,
    valor: Double,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    decimales: Int = 0,
    sufijo: String = "",
    tendencia: List<Double> = emptyList(),
    variacion: Double? = null,
    mejorSiSube: Boolean = true,
    colorSerie: Color = MaterialTheme.colorScheme.primary,
    onClick: (() -> Unit)? = null,
    info: InfoAyuda? = null,
    onVerAyuda: (() -> Unit)? = null,
) {
    val textoFinal = formatearNumero(valor, decimales) + sufijo
    val textoVariacion = variacion?.let { textoVariacion(it) }
    var infoAbierta by rememberSaveable(titulo) { mutableStateOf(false) }
    BentoTile(
        size = BentoSize.S,
        modifier = modifier,
        etiqueta = titulo,
        icono = icono,
        onClick = if (info != null) ({ infoAbierta = true }) else onClick,
        descripcion = listOfNotNull("$titulo: $textoFinal", textoVariacion, info?.let { Textos.Dashboard.TOCA_KPI }).joinToString(". "),
    ) {
        NumeroAnimado(valor, GtTexto.tituloPantalla, decimales = decimales, sufijo = sufijo)
        // Se reserva siempre el espacio de la tendencia y la variación para que los KPI de una fila midan lo mismo.
        Spacer(Modifier.height(Dimens.sm))
        if (tendencia.size >= 2) Sparkline(tendencia, colorSerie, alto = Dimens.xl) else Spacer(Modifier.height(Dimens.xl))
        Spacer(Modifier.height(Dimens.sm))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (variacion != null) Variacion(variacion, mejorSiSube) else Spacer(Modifier.height(Dimens.lgx))
            Spacer(Modifier.weight(1f))
            if (info != null) Icon(Icons.Outlined.Info, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(Dimens.iconoChico))
        }
    }
    if (infoAbierta && info != null) {
        HojaInfo(
            info,
            onCerrar = { infoAbierta = false },
            onVerAyuda = onVerAyuda?.let { ir ->
                {
                    infoAbierta = false
                    ir()
                }
            },
        )
    }
}

/** "+12 % vs periodo anterior" con flecha (la dirección nunca se indica solo con color). */
@Composable
fun Variacion(porcentaje: Double, mejorSiSube: Boolean = true, modifier: Modifier = Modifier) {
    val sube = porcentaje > 0.05
    val baja = porcentaje < -0.05
    val positivo = (sube && mejorSiSube) || (baja && !mejorSiSube)
    val color = when {
        !sube && !baja -> MaterialTheme.colorScheme.onSurfaceVariant
        positivo -> GreenTechTheme.colors.success
        else -> GreenTechTheme.colors.warning
    }
    val icono = when {
        sube -> Icons.AutoMirrored.Outlined.TrendingUp
        baja -> Icons.AutoMirrored.Outlined.TrendingDown
        else -> Icons.AutoMirrored.Outlined.TrendingFlat
    }
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, null, tint = color, modifier = Modifier.size(Dimens.iconoMini))
        Spacer(Modifier.size(Dimens.xs))
        Text(
            if (!sube && !baja) Textos.Disenio.SIN_CAMBIO else textoPorcentaje(porcentaje),
            style = GtTexto.etiqueta.tabular(),
            color = color,
            maxLines = 1,
        )
    }
}

private fun textoPorcentaje(p: Double): String = (if (p > 0) "+" else "") + p.formatoDecimal(1) + " %"

private fun textoVariacion(p: Double): String = "${textoPorcentaje(p)} ${Textos.Disenio.VS_ANTERIOR}"
