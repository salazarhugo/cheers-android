package com.salazar.cheers.feature.premium.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.animations.Presets
import com.salazar.cheers.core.ui.components.message.MessageScreenComponent
import com.salazar.cheers.core.ui.ui.LoadingScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.compose.KonfettiView

@Composable
fun SuccessPurchaseLoadingScreen(
    navigateToProfile: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var success by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            delay(2000L)
            success = true
        }
    }

    if (success) {
        MessageScreenComponent(
            image = com.salazar.cheers.core.ui.R.drawable.icons8_success,
            modifier = Modifier.padding(16.dp),
            title = "Welcome to Cheers Premium",
            subtitle = "Your purchase was successful",
            primaryButtonText = "Go to profile",
            onPrimaryButtonClick = navigateToProfile,
        )
        KonfettiView(
            modifier = Modifier,
            parties = Presets.explode(),
        )
    } else {
        LoadingScreen(
            text = "",
            modifier = Modifier,
        )
    }
}

@Preview
@Composable
private fun SuccessPurchaseLoadingScreenPreview() {
    CheersPreview {
        SuccessPurchaseLoadingScreen(
            navigateToProfile = {}
        )
    }
}
