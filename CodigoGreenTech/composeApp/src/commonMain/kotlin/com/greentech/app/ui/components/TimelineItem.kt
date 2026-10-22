package com.greentech.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.Dimens
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.GtTexto
import com.greentech.app.ui.theme.ParColores
import com.greentech.app.util.FechasUi
import com.greentech.shared.domain.model.EventoBitacora
import com.greentech.shared.domain.model.TipoEvento
import com.greentech.shared.util.Fechas

@Composable
fun TipoEvento.colores(): ParColores = when (this) {
    TipoEvento.REGISTRO -> GreenTechTheme.colors.neutro
    TipoEvento.ETAPA -> GreenTechTheme.colors.primario
    TipoEvento.OBSERVACION -> GreenTechTheme.colors.info
    TipoEvento.ACTIVIDAD -> GreenTechTheme.colors.secundario
    TipoEvento.ANOMALIA -> GreenTechTheme.colors.error
    TipoEvento.SALIDA -> GreenTechTheme.colors.advertencia
}

/**
 * Elemento de la línea de tiempo vertical: nodo con el color e ícono del tipo, línea que lo une
 * al siguiente y una tarjeta compacta con título, descripción, autor y hora.
 *
 * @param mostrarHora true en la bitácora (la fecha va en el separador); false muestra fecha y hora.
 */
@Composable
fun TimelineItem(
    evento: EventoBitacora,
    esUltimo: Boolean,
    modifier: Modifier = Modifier,
    mostrarLote: Boolean = false,
    soloHora: Boolean = false,
    esPrimero: Boolean = false,
) {
    val colores = evento.tipo.colores()
    val linea = MaterialTheme.colorScheme.outlineVariant
    Row(modifier.fillMaxWidth().height(IntrinsicSize.Min).semantics(mergeDescendants = true) {}) {
        Column(Modifier.width(Dimens.columnaLineaTiempo).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.width(Dimens.lineaTiempo).height(Dimens.md).background(if (esPrimero) androidx.compose.ui.graphics.Color.Transparent else linea))
            Box(
                Modifier.size(Dimens.nodoLineaTiempo).clip(GtShapes.circulo).background(colores.fondo),
                contentAlignment = Alignment.Center,
            ) {
                Icon(IconosDominio.evento(evento.tipo), contentDescription = evento.tipo.etiqueta, tint = colores.contenido, modifier = Modifier.size(Dimens.iconoChico))
            }
            if (!esUltimo) Box(Modifier.width(Dimens.lineaTiempo).weight(1f).background(linea))
        }
        Spacer(Modifier.width(Dimens.md))
        Surface(
            modifier = Modifier.weight(1f).padding(bottom = if (esUltimo) Dimens.none else Dimens.md),
            shape = GtShapes.campo,
            color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
            Column(Modifier.padding(horizontal = Dimens.md, vertical = Dimens.md), verticalArrangement = Arrangement.spacedBy(Dimens.xxs)) {
                Text(evento.titulo, style = GtTexto.tituloTarjeta, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (mostrarLote) {
                    Text("${evento.especie} · ${evento.loteCodigo}", style = GtTexto.ayuda, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (evento.descripcion.isNotBlank()) {
                    Text(evento.descripcion, style = GtTexto.ayuda)
                }
                Text(
                    "${Textos.Bitacora.POR} ${evento.autor} · " + if (soloHora) Fechas.hora(evento.fecha) else FechasUi.relativa(evento.fecha),
                    style = GtTexto.ayuda,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = Dimens.xxs),
                )
            }
        }
    }
}
