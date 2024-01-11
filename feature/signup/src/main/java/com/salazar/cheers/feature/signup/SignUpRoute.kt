package com.salazar.cheers.feature.signup

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.common.util.LocalActivity

@Composable
fun SignUpRoute(
    viewModel: SignUpViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToSignIn: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current

    LaunchedEffect(uiState.isSignedIn) {
        if (uiState.isSignedIn)
            navigateToSignIn()
    }

    if (uiState.withGoogle) {
        Text(
            text = "Signing up with Google",
        )
    }

    ChooseUsernameScreen(
        username = uiState.username,
        errorMessage = uiState.errorMessage,
        isLoading = uiState.isLoading,
        isUsernameAvailable = true,
        onClearUsername = {},
        onUsernameChanged = viewModel::onUsernameChange,
        onNextClicked = {
            viewModel.onNextClick(activity)
        },
        onBackPressed = {},
    )
}