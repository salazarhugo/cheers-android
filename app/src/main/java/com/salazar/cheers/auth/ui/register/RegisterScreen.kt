package com.salazar.cheers.auth.ui.register

import androidx.compose.runtime.Composable
import com.salazar.cheers.auth.ui.signin.CreateAccountScreen
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.core.ui.annotations.ScreenPreviews

@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    onRegisterClick: () -> Unit,
    onAcceptTermsChange: (Boolean) -> Unit,
    onBackPressed: () -> Unit,
) {
    CreateAccountScreen(
        errorMessage = uiState.errorMessage,
        username = uiState.username,
        isLoading = uiState.isLoading,
        acceptTerms = uiState.termsAccepted,
        onSignUp = onRegisterClick,
        onAcceptTermsChange = onAcceptTermsChange,
        onBackPressed = onBackPressed,
    )
}

@ScreenPreviews
@Composable
fun RegisterScreenPreview() {
    CheersPreview {
        RegisterScreen(
            uiState = RegisterUiState(),
            onRegisterClick = {},
            onAcceptTermsChange = {},
            onBackPressed = {},
        )
    }
}