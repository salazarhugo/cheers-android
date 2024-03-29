package com.salazar.cheers.core.ui.components.login_message

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.R
import com.salazar.cheers.core.ui.annotations.ComponentPreviews
import com.salazar.cheers.core.ui.components.message.MessageComponent

@Composable
fun LoginMessage(
    modifier: Modifier = Modifier,
    onSignInClick: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    MessageComponent(
        modifier = modifier,
        image = R.drawable.ic_moncompte_teasing,
        title = "Have we met?",
        subtitle = "Log in or create an account to get custom recommendations and buy tickets for the best parties.",
        primaryButtonText = "Sign in",
        secondaryButtonText = "Register",
        onPrimaryButtonClick = onSignInClick,
        onSecondaryButtonClick = onRegisterClick,
    )
}

@ComponentPreviews
@Composable
private fun LoginMessagePreview() {
    CheersPreview {
        LoginMessage(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onSignInClick = {},
            onRegisterClick = {},
        )
    }
}