package com.softimpact.feature.passcode.settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun PasscodeSettingRoute(
    navigateBack: () -> Unit,
    navigateToChangePasscode: () -> Unit,
    viewModel: PasscodeSettingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var openDialog by remember { mutableStateOf(false) }

    PasscodeSettingScreen(
        uiState = uiState,
        onPasscodeLockSettingUIAction = { action ->
            when (action) {
                PasscodeSettingUIAction.OnBackPressed -> navigateBack()
                PasscodeSettingUIAction.OnChangePasscodeClick -> navigateToChangePasscode()
                PasscodeSettingUIAction.OnTurnOffPasscodeClick -> {
                    openDialog = true
                }
                PasscodeSettingUIAction.OnBiometricToggle -> viewModel.toggleBiometric()
                PasscodeSettingUIAction.OnHideContentToggle -> viewModel.toggleHideContent()
            }
        }
    )

    TurnOffPasscodeDialog(
        openDialog = openDialog,
        onDismissClick = { openDialog = false },
        onConfirmClick = {
            viewModel.onTurnOffPasscode {
                openDialog = false
                navigateBack()
            }
        }
    )
}

@Composable
fun TurnOffPasscodeDialog(
    openDialog: Boolean,
    onDismissClick: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    if (openDialog) {
        AlertDialog(
            onDismissRequest = onDismissClick,
            title = {
                Text(
                    text = "Turn Passcode Off",
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to disable your passcode?",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmClick,
                ) {
                    Text("Turn Off")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissClick,
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}