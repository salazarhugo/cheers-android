package com.softimpact.feature.passcode.passcode

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.common.util.getActivity
import com.softimpact.feature.passcode.BiometricPromptUtils
import com.softimpact.feature.passcode.PinLockScreen
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


@Composable
fun PinRoute(
    banner: Int,
    navigateToHome: () -> Unit,
    pinLockViewModel: PinLockViewModel = hiltViewModel(),
) {
    val uiState by pinLockViewModel.uiState.collectAsStateWithLifecycle()
    val biometricEnabled = uiState.biometricEnabled
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    fun promptBiometric() {
        val activity = context.getActivity() ?: return
        val biometricPrompt = BiometricPromptUtils.createBiometricPrompt(activity) {
            pinLockViewModel.updateSuccess(true)
        }
        val promptInfo = BiometricPromptUtils.createPromptInfo(activity)
        biometricPrompt.authenticate(promptInfo)
    }

    LaunchedEffect(Unit) {
        if (biometricEnabled) promptBiometric()
    }

    val angle by animateFloatAsState(
        targetValue = (uiState.inputPin.length - 1) * 30f,
        animationSpec = tween(
            durationMillis = 300,
        ),
        label = "",
    )

    val colors = listOf(
        Color(0xFF53385B),
        Color(0xFF003d66),
    )

    PinLockScreen(
        modifier = Modifier.gradientBackground(colors, angle),
        inputPin = uiState.inputPin,
        errorMessage = uiState.errorMessage,
        showSuccess = uiState.success,
        biometricEnabled = biometricEnabled,
        onSubmit = pinLockViewModel::onSubmit,
        onAddDigit = pinLockViewModel::onAddDigit,
        onRemoveLastDigit = pinLockViewModel::onRemoveLastDigit,
        onAnimationComplete = {
            navigateToHome()
        },
        onBackPressed = {
//            navigateToSignIn(fromPinLock = true)
        },
        onFingerprintClick = { promptBiometric() },
        banner = banner,
    )
}

fun Modifier.gradientBackground(colors: List<Color>, angle: Float) = this.then(Modifier.drawBehind {
    val angleRad = angle / 180f * PI
    val x = cos(angleRad).toFloat()
    val y = sin(angleRad).toFloat()

    val radius = sqrt(size.width.pow(2) + size.height.pow(2)) / 2f
    val offset = center + Offset(x * radius, y * radius)

    val exactOffset = Offset(
        x = min(offset.x.coerceAtLeast(0f), size.width),
        y = size.height - min(offset.y.coerceAtLeast(0f), size.height)
    )

    drawRect(
        brush = Brush.linearGradient(
            colors = colors,
            start = Offset(size.width, size.height) - exactOffset,
            end = exactOffset
        ), size = size
    )
    blur(
        radius = 30.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded
    )
})
