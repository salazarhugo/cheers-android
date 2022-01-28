package com.salazar.cheers.ui.signin.signup

import androidx.compose.runtime.Composable
import com.salazar.cheers.ui.signin.SignInUiState

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