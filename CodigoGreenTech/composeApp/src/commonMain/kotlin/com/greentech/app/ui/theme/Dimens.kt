package com.greentech.app.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Espaciado en múltiplos de 4 y tamaños mínimos pensados para usar con guantes.
 * Las pantallas y los componentes no escriben `N.dp` de espaciado: usan estos tokens.
 * Solo [xxs] (2 dp) queda fuera de la escala de 4, para ajustes ópticos (líneas, separación etiqueta/ícono).
 */
object Dimens {
    val none = 0.dp
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val lgx = 20.dp
    val xl = 24.dp
    val xxl = 32.dp
    val xxxl = 48.dp
    val xxxxl = 64.dp

    // Usos semánticos de la escala.
    /** Margen izquierdo/derecho de todas las pantallas. */
    val margenLateral = lgx
    /** Separación entre tarjetas de una lista y entre mosaicos bento. */
    val separacionTarjetas = md
    /** Relleno interior de tarjetas y mosaicos. */
    val rellenoTarjeta = lg
    /** Relleno interior de hojas inferiores y diálogos. */
    val rellenoHoja = xl
    /** Separación entre secciones de una pantalla o formulario. */
    val separacionSeccion = xl
    /** Separación entre un ícono y su texto. */
    val iconoTexto = sm
    /** Espacio inferior para que el FAB y la barra flotante no tapen el último elemento. */
    val espacioFab = 88.dp
    val anchoMaximo = 640.dp
    val anchoFormularioLogin = 480.dp

    val tactilMin = 56.dp
    val botonAlto = 60.dp
    val accionAlta = 112.dp
    val barraInferiorAlto = 76.dp
    val botonCentral = 60.dp

    val iconoMini = 16.dp
    val iconoChico = 20.dp
    val icono = 24.dp
    val iconoMediano = 28.dp
    val iconoGrande = 32.dp
    val iconoEstado = 44.dp
    val iconoVacio = 56.dp

    val logoBarra = 32.dp
    val logoAyuda = 56.dp
    val logoLogin = 240.dp
    val logoSplash = 160.dp

    val avatar = 48.dp
    val avatarGrande = 88.dp
    val circuloIcono = 44.dp
    val circuloVacio = 104.dp

    val borde = 1.dp
    val bordeSeleccion = 2.dp
    val elevacionTarjeta = 1.dp
    val elevacionBarra = 3.dp
    val elevacionNotificacion = 6.dp

    val barraProgreso = 8.dp
    val nodoLineaTiempo = 36.dp
    val columnaLineaTiempo = 40.dp
    val iconoLote = 48.dp
    val tooltipGrafica = 128.dp
    val anilloSalud = 96.dp
    val anilloSaludGrosor = 8.dp
    val puntoSalud = 12.dp
    val anilloFoco = 3.dp
    val puntoLeyenda = 12.dp
    val lineaTiempo = 2.dp
    val gauge = 148.dp
    /** Gauge compacto dentro de un mosaico S o M. */
    val gaugeMini = 96.dp
    /** Gauge de la pantalla Condiciones. */
    val gaugeMediano = 116.dp
    val gaugeGrosor = 14.dp
    val graficaAlto = 200.dp
    val barraHorizontalAlto = 20.dp
    val qr = 260.dp
    val visor = 280.dp
    val esquinaVisor = 36.dp
    val grosorVisor = 5.dp
    val desplazamientoEntrada = 24.dp
    val skeletonLinea = 16.dp
    val skeletonTarjeta = 112.dp
    val skeletonResumen = 80.dp
    val skeletonKpi = 160.dp
    val skeletonGrafica = 260.dp
    /** Alto mínimo de filas de lista con ícono + dos líneas (Perfil, Ayuda, menús). */
    val filaLista = 64.dp
    /** Círculo con ícono en la cabecera de hojas inferiores y diálogos. */
    val circuloHoja = 64.dp
}
