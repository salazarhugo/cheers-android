package com.salazar.cheers.ui.auth.register

import androidx.compose.runtime.Composable
import com.salazar.cheers.ui.auth.signin.CreateAccountScreen

@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    onRegisterClick: () -> Unit,
    onAcceptTermsChange: (Boolean) -> Unit,
) {
    CreateAccountScreen(
        username = uiState.username,
        isLoading = uiState.isLoading,
        acceptTerms = uiState.termsAccepted,
        onSignUp = onRegisterClick,
        onAcceptTermsChange = onAcceptTermsChange,
    )
}
