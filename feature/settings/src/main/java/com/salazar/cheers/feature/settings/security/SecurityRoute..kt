package com.salazar.cheers.feature.settings.security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.shared.util.LocalActivity

@Composable
fun SecurityRoute(
    viewModel: SecurityViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
    navigateToPasscodeSettings: () -> Unit,
    navigateToCreatePasscode: () -> Unit,
    navigateToPasskeys: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current

    SecurityScreen(
        uiState = uiState,
        onBackPressed = navigateBack,
        onPasskeysClick = navigateToPasskeys,
        onPasscodeClick = {
            if (uiState.passcodeEnabled)
                navigateToPasscodeSettings()
            else
                navigateToCreatePasscode()
        },
        onCreatePasskeyClick = {
            viewModel.onCreatePasskeyClick(activity = activity)
        },
    )
}
