package com.greentech.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.tabular
import com.greentech.shared.domain.model.Lote

/** Contexto del lote para subtítulos de barra: "Agave · GT-8F2A91C0D4". */
fun Lote.contexto(): String = "${especie.nombre} · $codigo"

/** Contexto del lote mientras carga: null hasta tener datos. */
fun EstadoCarga<Lote>.contexto(): String? = datosOrNull?.contexto()

/** Tarjeta compacta que recuerda en qué lote se está registrando (primaryContainer). */
@Composable
fun LoteResumen(lote: Lote, modifier: Modifier = Modifier) {
    GtCard(modifier = modifier.semantics(mergeDescendants = true) {}, destacada = true, relleno = 12.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(48.dp).clip(GtShapes.campo).background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center,
            ) { Icon(IconosDominio.etapa(lote.etapa), null, tint = MaterialTheme.colorScheme.primary) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(lote.especie.nombre, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(
                    "${lote.codigo} · ${lote.zona.nombre}",
                    style = MaterialTheme.typography.bodySmall.tabular(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

/** Resumen del lote mientras carga (skeleton) o ya cargado. */
@Composable
fun LoteResumenEstado(estado: EstadoCarga<Lote>, modifier: Modifier = Modifier) {
    when (estado) {
        is EstadoCarga.Listo -> LoteResumen(estado.datos, modifier)
        is EstadoCarga.Error -> MensajeError(estado.mensaje, modifier)
        EstadoCarga.Cargando -> SkeletonBloque(modifier.fillMaxWidth().height(72.dp))
    }
}

/** Separación vertical uniforme entre secciones de un formulario. */
@Composable
fun EspacioSeccion() = Spacer(Modifier.height(4.dp))
