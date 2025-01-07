package com.softimpact.feature.passcode.passcode

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.modifier.gradientBackground
import com.salazar.cheers.shared.util.LocalActivity


@Composable
fun PasscodeRoute(
    banner: Int,
    navigateToHome: () -> Unit,
    pinLockViewModel: PinLockViewModel = hiltViewModel(),
) {
    val uiState by pinLockViewModel.uiState.collectAsStateWithLifecycle()
    val biometricEnabled = uiState.biometricEnabled
    val activity = LocalActivity.current
//    val promptManager by lazy {
//        BiometricPromptManager(activity as AppCompatActivity)
//    }
//    val biometricResult by promptManager.promptResults.collectAsState(
//        initial = null
//    )
//    LaunchedEffect(biometricResult) {
//        if (biometricResult is BiometricPromptManager.BiometricResult.AuthenticationSuccess) {
//            navigateToHome()
//        }
//    }
    fun promptBiometric() {
//        promptManager.showBiometricPrompt(
//            title = "Sample prompt",
//            description = "Sample prompt description"
//        )
    }

    if (biometricEnabled) {
        LaunchedEffect(Unit) {
            promptBiometric()
        }
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

    PasscodeScreen(
        modifier = Modifier.gradientBackground(angle, colors),
        inputPin = uiState.inputPin,
        errorMessage = uiState.errorMessage,
        biometricEnabled = biometricEnabled,
        onSubmit = {
            pinLockViewModel.onSubmit(
                onSuccess = navigateToHome,
            )
        },
        onAddDigit = pinLockViewModel::onAddDigit,
        onRemoveLastDigit = pinLockViewModel::onRemoveLastDigit,
        onBackPressed = {
//            navigateToSignIn(fromPinLock = true)
        },
        onFingerprintClick = { promptBiometric() },
        banner = banner,
    )
}
