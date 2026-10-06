package com.greentech.app.ui.screens.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material3.Icon
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.onPreviewKeyEvent
import com.greentech.app.platform.bloqMayusActivo
import com.greentech.app.ui.components.GtBottomSheet
import com.greentech.app.ui.components.GtButtonVariant
import com.greentech.app.ui.components.MensajeError
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.greentech.app.ui.components.GtButton
import com.greentech.app.ui.components.GtTextField
import com.greentech.app.ui.components.MarcaGreenTech
import com.greentech.app.ui.strings.Textos
import com.greentech.app.ui.theme.GreenTechTheme
import com.greentech.app.ui.theme.GtShapes
import com.greentech.app.ui.theme.Motion
import com.greentech.app.ui.theme.ThemeMode
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LoginScreen(estado: LoginUiState, onEvent: (LoginEvent) -> Unit) {
    val foco = LocalFocusManager.current
    var olvide by rememberSaveable { mutableStateOf(false) }
    var bloqMayus by remember { mutableStateOf(false) }
    val enviar = {
        foco.clearFocus()
        onEvent(LoginEvent.Enviar)
    }
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        FondoOrganico(Modifier.fillMaxSize())
        // Alto mínimo = pantalla: el formulario queda centrado y el pie abajo; con letra grande se desplaza.
        BoxWithConstraints(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).imePadding()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = maxHeight)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Spacer(Modifier.height(0.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    MarcaAnimada()
                    Spacer(Modifier.height(8.dp))
                    Text(
                        Textos.Splash.LEMA,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(24.dp))
                    // Tarjeta sólida (no vidrio) para que el formulario se lea perfecto.
                    Surface(
                        modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth(),
                        shape = GtShapes.mosaico,
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 2.dp,
                    ) {
                        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                Textos.Login.TITULO,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.semantics { heading() },
                            )
                            GtTextField(
                                valor = estado.correo,
                                onValorChange = { onEvent(LoginEvent.CorreoCambio(it)) },
                                etiqueta = Textos.Login.CORREO,
                                placeholder = Textos.Login.CORREO_EJEMPLO,
                                icono = Icons.Outlined.Email,
                                error = estado.errorCorreo,
                                tipoTeclado = KeyboardType.Email,
                                accionIme = ImeAction.Next,
                            )
                            GtTextField(
                                valor = estado.password,
                                onValorChange = { onEvent(LoginEvent.PasswordCambio(it)) },
                                etiqueta = Textos.Login.PASSWORD,
                                icono = Icons.Outlined.Lock,
                                error = estado.errorPassword,
                                ayuda = if (bloqMayus) Textos.Login.BLOQ_MAYUS else null,
                                esPassword = true,
                                accionIme = ImeAction.Go,
                                alAccionIme = enviar,
                                // Solo con teclado físico: la plataforma informa si Bloq Mayús está activo.
                                modifier = Modifier.onPreviewKeyEvent { e ->
                                    e.bloqMayusActivo()?.let { bloqMayus = it }
                                    false
                                },
                            )
                            estado.errorCuenta?.let { MensajeError(it) }
                            Spacer(Modifier.height(4.dp))
                            GtButton(
                                texto = if (estado.cargando) Textos.Login.ENTRANDO else Textos.Login.ENTRAR,
                                onClick = enviar,
                                icono = Icons.AutoMirrored.Outlined.Login,
                                cargando = estado.cargando,
                                habilitado = estado.puedeEnviar,
                            )
                            GtButton(
                                texto = Textos.Login.OLVIDE,
                                onClick = { olvide = true },
                                variante = GtButtonVariant.Texto,
                            )
                        }
                    }
                }
                Text(
                    Textos.Perfil.VERSION,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 24.dp),
                )
            }
        }
    }
    if (olvide) HojaOlvidePassword(onCerrar = { olvide = false })
}

/** No hay recuperación automática: la contraseña la restablece el administrador. */
@Composable
private fun HojaOlvidePassword(onCerrar: () -> Unit) {
    GtBottomSheet(onCerrar = onCerrar) {
        Column(
            Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                Modifier.size(64.dp).clip(GtShapes.circulo).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) { Icon(Icons.Outlined.LockReset, null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(32.dp)) }
            Text(
                Textos.Login.OLVIDE,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                Textos.Login.OLVIDE_MENSAJE,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            GtButton(Textos.Comun.ENTENDIDO, onCerrar)
        }
    }
}

/** Emblema + logotipo compuesto, con escala y fade de entrada. */
@Composable
private fun MarcaAnimada() {
    val alfa = remember { Animatable(if (Motion.reducir) 1f else 0f) }
    val escala = remember { Animatable(if (Motion.reducir) 1f else 0.92f) }
    LaunchedEffect(Unit) {
        launch { alfa.animateTo(1f, Motion.lenta()) }
        escala.animateTo(1f, Motion.resorte())
    }
    MarcaGreenTech(
        vertical = true,
        tamanoEmblema = 96.dp,
        tamanoTexto = 34.sp,
        modifier = Modifier.graphicsLayer {
            alpha = alfa.value
            scaleX = escala.value
            scaleY = escala.value
        },
    )
}

/**
 * Fondo orgánico: 3 manchas grandes y difusas (brandGreen 18 %, secondary 14 %, tertiary 10 %) que
 * se mueven muy lento (ciclo de 20 s). Con "Reducir animaciones" quedan quietas.
 */
@Composable
private fun FondoOrganico(modifier: Modifier) {
    val verde = GreenTechTheme.colors.brandGreen.copy(alpha = 0.18f)
    val secundario = MaterialTheme.colorScheme.secondary.copy(alpha = 0.14f)
    val terciario = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.10f)
    val fase = if (Motion.reducir) {
        0f
    } else {
        val t by rememberInfiniteTransition(label = "fondoLogin").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(20_000, easing = LinearEasing), RepeatMode.Restart),
            label = "fase",
        )
        t
    }
    Canvas(modifier) {
        val a = fase * 2 * PI.toFloat()
        fun mancha(color: Color, cx: Float, cy: Float, radio: Float) {
            val centro = Offset(cx, cy)
            drawCircle(
                brush = Brush.radialGradient(listOf(color, color.copy(alpha = 0f)), center = centro, radius = radio),
                radius = radio,
                center = centro,
            )
        }
        val w = size.width
        val h = size.height
        mancha(verde, w * (0.15f + 0.08f * cos(a)), h * (0.18f + 0.05f * sin(a)), w * 0.85f)
        mancha(secundario, w * (0.9f + 0.06f * sin(a)), h * (0.5f + 0.06f * cos(a)), w * 0.75f)
        mancha(terciario, w * (0.3f + 0.07f * sin(a + 1f)), h * (0.92f + 0.04f * cos(a)), w * 0.8f)
    }
}

@Preview
@Composable
private fun LoginPreviewClaro() {
    GreenTechTheme {
        LoginScreen(LoginUiState(correo = "empleado@gmail.com")) {}
    }
}

@Preview
@Composable
private fun LoginPreviewOscuro() {
    GreenTechTheme(modo = ThemeMode.OSCURO) {
        LoginScreen(LoginUiState(correo = "admin@gmail.com", password = "admin123")) {}
    }
}
