package com.softimpact.feature.passcode.change

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun ChangePasscodeRoute(
    navigateBack: () -> Unit,
    viewModel: ChangePasscodeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ChangePasscodeScreen(
        uiState = uiState,
        passcode = uiState.input,
        errorMessage = uiState.errorMessage,
        onSubmit = {
               viewModel.onSubmit(onComplete = navigateBack)
        },
        onAddDigit = viewModel::onAddDigit,
        onRemoveLastDigit = viewModel::onRemoveLastDigit,
        onBackPressed = navigateBack,
        onFingerprintClick = {},
    )
}
