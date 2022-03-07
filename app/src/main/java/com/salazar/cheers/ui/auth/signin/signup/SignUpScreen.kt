package com.salazar.cheers.ui.auth.signin.signup

import androidx.compose.runtime.Composable
import com.salazar.cheers.ui.auth.signin.SignInUiState

@Composable
fun SignUpScreen(
    uiState: SignInUiState,
    signInWithEmailPassword: () -> Unit,
    signInWithGoogle: () -> Unit,
    navigateToPhone: () -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
) {
}