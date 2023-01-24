package com.salazar.cheers.auth.ui.register

import androidx.compose.runtime.Composable
import com.salazar.cheers.auth.ui.signin.CreateAccountScreen

@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    onRegisterClick: () -> Unit,
    onAcceptTermsChange: (Boolean) -> Unit,
) {
    CreateAccountScreen(
        errorMessage = uiState.errorMessage,
        username = uiState.username,
        isLoading = uiState.isLoading,
        acceptTerms = uiState.termsAccepted,
        onSignUp = onRegisterClick,
        onAcceptTermsChange = onAcceptTermsChange,
    )
}
