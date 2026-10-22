package com.greentech.app.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.greentech.app.ui.core.EstadoCarga
import com.greentech.app.ui.theme.Dimens
import com.greentech.shared.domain.model.Lote

/** Contexto del lote para subtítulos de barra: "Agave · GT-8F2A91C0D4". */
fun Lote.contexto(): String = "${especie.nombre} · $codigo"

/** Contexto del lote mientras carga: null hasta tener datos. */
fun EstadoCarga<Lote>.contexto(): String? = datosOrNull?.contexto()

/** Tarjeta que recuerda en qué lote se está registrando: [LoteCard] en su variante destacada. */
@Composable
fun LoteResumen(lote: Lote, modifier: Modifier = Modifier) {
    LoteCard(lote, onClick = null, modifier = modifier, variante = VarianteLoteCard.DESTACADA)
}

/** Resumen del lote mientras carga (skeleton) o ya cargado. */
@Composable
fun LoteResumenEstado(estado: EstadoCarga<Lote>, modifier: Modifier = Modifier) {
    when (estado) {
        is EstadoCarga.Listo -> LoteResumen(estado.datos, modifier)
        is EstadoCarga.Error -> MensajeError(estado.mensaje, modifier)
        EstadoCarga.Cargando -> SkeletonBloque(modifier.fillMaxWidth().height(Dimens.skeletonResumen))
    }
}

/** Separación vertical uniforme entre secciones de un formulario. */
@Composable
fun EspacioSeccion() = Spacer(Modifier.height(Dimens.xs))
