package com.salazar.cheers.feature.settings.password

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CreatePasswordRoute(
    createPasswordViewModel: CreatePasswordViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by createPasswordViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.done) {
        if (uiState.done)
            navigateBack()
    }

    CreatePasswordScreen(
        uiState = uiState,
        onBackPressed = { navigateBack() },
        onPasswordChange = createPasswordViewModel::onPasswordChange,
        onCreatePassword = {
            createPasswordViewModel.onCreatePassword {
                navigateBack()
            }
        },
    )
}
