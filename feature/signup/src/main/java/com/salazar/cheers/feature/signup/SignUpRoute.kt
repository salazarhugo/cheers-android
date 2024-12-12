package com.salazar.cheers.feature.signup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.shared.util.LocalActivity

@Composable
fun SignUpRoute(
    viewModel: SignUpViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current

    LaunchedEffect(uiState.isSignedIn) {
        if (uiState.isSignedIn)
            navigateToHome()
    }

    ChooseUsernameScreen(
        username = uiState.username,
        errorMessage = uiState.errorMessage,
        isLoading = uiState.isLoading,
        usernameAvailabilityState = uiState.usernameAvailabilityState,
        onClearUsername = {},
        onUsernameChanged = viewModel::onUsernameChange,
        onNextClicked = {
            viewModel.onNextClick(activity)
        },
        onBackPressed = {},
    )
}