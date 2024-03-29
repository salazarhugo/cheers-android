package com.salazar.cheers.core.ui.components.login_message

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews

@Composable
fun LoginMessageScreen(
    onSignInClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        LoginMessage(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onSignInClick = onSignInClick,
            onRegisterClick = onRegisterClick,
        )
    }
}

@ScreenPreviews
@Composable
private fun NoAccountScreenPreview() {
    CheersPreview {
        LoginMessageScreen()
    }
}