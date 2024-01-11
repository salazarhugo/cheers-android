package com.salazar.cheers.feature.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews
import com.salazar.cheers.core.ui.components.message.MessageComponent
import com.salazar.cheers.core.ui.theme.CheersTheme

@Composable
fun NoAccountScreen(
    onSignInClick: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        MessageComponent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            image = R.drawable.ic_moncompte_teasing,
            title = "Have we met?",
            subtitle = "Log in or create an account to get custom recommendations and buy tickets for the best parties.",
            primaryButtonText = "Sign in",
            secondaryButtonText = "Register",
            onPrimaryButtonClick = onSignInClick,
            onSecondaryButtonClick = onRegisterClick,
        )
    }
}

@ScreenPreviews
@Composable
fun NoAccountScreenPreview() {
    CheersPreview {
        NoAccountScreen(
            onSignInClick = {},
            onRegisterClick = {},
        )
    }
}