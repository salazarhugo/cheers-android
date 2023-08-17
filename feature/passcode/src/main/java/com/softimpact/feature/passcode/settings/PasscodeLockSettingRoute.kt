package com.softimpact.feature.passcode.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun PasscodeLockSettingRoute(
    navigateBack: () -> Unit,
    navigateToSetPasscode: () -> Unit,
    viewModel: PasscodeLockSettingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PasscodeLockSettingScreen(
        uiState = uiState,
        onPasscodeLockSettingUIAction = { action ->
            when (action) {
                PasscodeLockSettingUIAction.OnBackPressed -> navigateBack()
                PasscodeLockSettingUIAction.OnChangePasscodeClick -> navigateToSetPasscode()
                PasscodeLockSettingUIAction.OnTurnOffPasscodeClick -> {
                    viewModel.onDeletePin {
                        navigateBack()
                    }
                }

                PasscodeLockSettingUIAction.OnBiometricToggle -> viewModel.toggleBiometric()
            }
        }
    )
}