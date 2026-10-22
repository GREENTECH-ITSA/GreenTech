package com.greentech.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dangerous
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.ParColores
import com.greentech.app.ui.theme.ThemeMode
import com.greentech.shared.domain.model.EstadoSalud

/** Forma que acompaña a cada estado de salud: el estado se reconoce aunque no se distinga el color. */
enum class FormaSalud { CIRCULO, CIRCULO_BORDE, TRIANGULO, ROMBO }

/** Variantes del indicador: chip (tarjetas, filtros), punto con etiqueta (listas densas) y anillo (detalle). */
enum class VarianteSalud { CHIP, PUNTO, ANILLO }

fun EstadoSalud.forma(): FormaSalud = when (this) {
    EstadoSalud.SANA -> FormaSalud.CIRCULO
    EstadoSalud.EN_OBSERVACION -> FormaSalud.CIRCULO_BORDE
    EstadoSalud.AFECTADA -> FormaSalud.TRIANGULO
    EstadoSalud.CRITICA -> FormaSalud.ROMBO
}

/** Ícono outlined de cada estado. Cuatro siluetas distintas: hoja, ojo, triángulo y octágono. */
fun EstadoSalud.icono(): ImageVector = when (this) {
    EstadoSalud.SANA -> Icons.Outlined.Eco
    EstadoSalud.EN_OBSERVACION -> Icons.Outlined.Visibility
    EstadoSalud.AFECTADA -> Icons.Outlined.ReportProblem
    EstadoSalud.CRITICA -> Icons.Outlined.Dangerous
}

/** Tokens de salud: sana (success), en observación (info), afectada (warning) y crítica (error). */
@Composable
fun EstadoSalud.colores(): ParColores {
    val s = GreenTechTheme.colors.salud
    return when (this) {
        EstadoSalud.SANA -> s.sana
        EstadoSalud.EN_OBSERVACION -> s.observacion
        EstadoSalud.AFECTADA -> s.afectada
        EstadoSalud.CRITICA -> s.critica
    }
}

private fun EstadoSalud.significado(): String = when (this) {
    EstadoSalud.SANA -> Textos.Salud.SANA
    EstadoSalud.EN_OBSERVACION -> Textos.Salud.EN_OBSERVACION
    EstadoSalud.AFECTADA -> Textos.Salud.AFECTADA
    EstadoSalud.CRITICA -> Textos.Salud.CRITICA
}

private fun FormaSalud.nombre(): String = when (this) {
    FormaSalud.CIRCULO -> Textos.Salud.FORMA_CIRCULO
    FormaSalud.CIRCULO_BORDE -> Textos.Salud.FORMA_CIRCULO_BORDE
    FormaSalud.TRIANGULO -> Textos.Salud.FORMA_TRIANGULO
    FormaSalud.ROMBO -> Textos.Salud.FORMA_ROMBO
}

/**
 * Indicador de salud vegetal. Siempre combina color + texto + (ícono o forma), igual en tarjetas,
 * detalle, formularios y leyenda.
 * - [VarianteSalud.CHIP]: pastilla tonal con ícono y texto.
 * - [VarianteSalud.PUNTO]: forma de 12 dp con el texto al lado.
 * - [VarianteSalud.ANILLO]: anillo de 96 dp con el ícono al centro, la forma como insignia y el texto debajo.
 */
@Composable
fun PlantHealthIndicator(
    estado: EstadoSalud,
    modifier: Modifier = Modifier,
    variante: VarianteSalud = VarianteSalud.CHIP,
) {
    val colores = estado.colores()
    val descripcion = "${Textos.Disenio.SALUD}: ${estado.etiqueta}"
    when (variante) {
        VarianteSalud.CHIP -> StatusChip(
            estado.etiqueta, colores,
            modifier.clearAndSetSemantics { contentDescription = descripcion },
            estado.icono(),
        )
        VarianteSalud.PUNTO -> Row(
            modifier.clearAndSetSemantics { contentDescription = descripcion },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FormaSaludMarca(estado.forma(), colores.contenido, Dimens.puntoSalud)
            Spacer(Modifier.width(Dimens.iconoTexto))
            Text(estado.etiqueta, style = GtTexto.etiqueta, color = colores.contenido)
        }
        VarianteSalud.ANILLO -> Column(
            modifier.clearAndSetSemantics { contentDescription = descripcion },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(Modifier.size(Dimens.anilloSalud), contentAlignment = Alignment.Center) {
                Canvas(Modifier.size(Dimens.anilloSalud)) {
                    val grosor = Dimens.anilloSaludGrosor.toPx()
                    drawCircle(colores.fondo, radius = size.minDimension / 2 - grosor / 2)
                    drawCircle(colores.contenido, radius = size.minDimension / 2 - grosor / 2, style = Stroke(grosor))
                }
                Icon(estado.icono(), null, tint = colores.contenido, modifier = Modifier.size(Dimens.iconoGrande))
                // Insignia con la forma, arriba a la derecha, sobre un disco de la superficie.
                Box(
                    Modifier.align(Alignment.TopEnd).size(Dimens.icono).clip(GtShapes.circulo)
                        .background(MaterialTheme.colorScheme.surfaceContainerLow),
                    contentAlignment = Alignment.Center,
                ) { FormaSaludMarca(estado.forma(), colores.contenido, Dimens.puntoSalud) }
            }
            Spacer(Modifier.size(Dimens.sm))
            Text(estado.etiqueta, style = GtTexto.tituloTarjeta, color = colores.contenido, textAlign = TextAlign.Center)
        }
    }
}

/** Dibuja la forma del estado: círculo lleno, círculo con borde, triángulo o rombo. */
@Composable
fun FormaSaludMarca(forma: FormaSalud, color: Color, tamano: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(tamano)) { dibujarForma(forma, color) }
}

private fun DrawScope.dibujarForma(forma: FormaSalud, color: Color) {
    val w = size.width
    val h = size.height
    when (forma) {
        FormaSalud.CIRCULO -> drawCircle(color)
        FormaSalud.CIRCULO_BORDE -> {
            val grosor = w * 0.22f
            drawCircle(color, radius = w / 2 - grosor / 2, style = Stroke(grosor))
        }
        FormaSalud.TRIANGULO -> drawPath(
            Path().apply {
                moveTo(w / 2, 0f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            },
            color,
        )
        FormaSalud.ROMBO -> drawPath(
            Path().apply {
                moveTo(w / 2, 0f)
                lineTo(w, h / 2)
                lineTo(w / 2, h)
                lineTo(0f, h / 2)
                close()
            },
            color,
        )
    }
}

/**
 * Ícono Info (56 dp) que abre la leyenda de salud vegetal. Va junto a cualquier lugar donde aparezca
 * el indicador por primera vez en una pantalla.
 */
@Composable
fun BotonLeyendaSalud(modifier: Modifier = Modifier) {
    var abierta by rememberSaveable { mutableStateOf(false) }
    IconButton(onClick = { abierta = true }, modifier = modifier.size(Dimens.tactilMin)) {
        Icon(Icons.Outlined.Info, contentDescription = Textos.Salud.VER_LEYENDA, tint = MaterialTheme.colorScheme.primary)
    }
    if (abierta) HojaLeyendaSalud(onCerrar = { abierta = false })
}

/** Hoja inferior con los cuatro estados: forma, ícono, nombre y qué significa. */
@Composable
fun HojaLeyendaSalud(onCerrar: () -> Unit) {
    GtBottomSheet(onCerrar = onCerrar) {
        Column(
            Modifier.padding(start = Dimens.rellenoHoja, end = Dimens.rellenoHoja, bottom = Dimens.rellenoHoja),
            verticalArrangement = Arrangement.spacedBy(Dimens.lg),
        ) {
            Text(Textos.Salud.TITULO, style = GtTexto.tituloTarjeta, modifier = Modifier.semantics { heading() })
            Text(Textos.Salud.LEYENDA_INTRO, style = GtTexto.ayuda, color = MaterialTheme.colorScheme.onSurfaceVariant)
            EstadoSalud.entries.forEach { FilaLeyendaSalud(it) }
            GtButton(Textos.Comun.ENTENDIDO, onCerrar, variante = GtButtonVariant.Tonal)
        }
    }
}

@Composable
private fun FilaLeyendaSalud(estado: EstadoSalud) {
    val colores = estado.colores()
    val forma = estado.forma()
    Row(
        Modifier.semantics(mergeDescendants = true) {
            contentDescription = "${estado.etiqueta}. ${forma.nombre()}. ${estado.significado()}"
        },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(Dimens.circuloIcono).clip(GtShapes.circulo).background(colores.fondo),
            contentAlignment = Alignment.Center,
        ) { Icon(estado.icono(), null, tint = colores.contenido) }
        Spacer(Modifier.width(Dimens.md))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FormaSaludMarca(forma, colores.contenido, Dimens.puntoSalud)
                Spacer(Modifier.width(Dimens.iconoTexto))
                Text(estado.etiqueta, style = GtTexto.etiqueta.copy(color = colores.contenido))
            }
            Text(estado.significado(), style = GtTexto.ayuda)
        }
    }
}

@Composable
private fun MuestraSalud() {
    Column(
        Modifier.background(MaterialTheme.colorScheme.background).padding(Dimens.margenLateral),
        verticalArrangement = Arrangement.spacedBy(Dimens.lg),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.sm)) {
            EstadoSalud.entries.take(2).forEach { PlantHealthIndicator(it) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.sm)) {
            EstadoSalud.entries.drop(2).forEach { PlantHealthIndicator(it) }
        }
        EstadoSalud.entries.forEach { PlantHealthIndicator(it, variante = VarianteSalud.PUNTO) }
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.lg)) {
            EstadoSalud.entries.forEach { PlantHealthIndicator(it, variante = VarianteSalud.ANILLO) }
        }
    }
}

@Preview
@Composable
private fun PlantHealthIndicatorPreviewClaro() {
    GreenTechTheme { MuestraSalud() }
}

@Preview
@Composable
private fun PlantHealthIndicatorPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) { MuestraSalud() }
}
