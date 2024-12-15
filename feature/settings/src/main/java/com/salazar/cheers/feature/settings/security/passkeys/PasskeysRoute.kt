package com.salazar.cheers.feature.settings.security.passkeys

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PasskeysRoute(
    viewModel: PasskeysViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PasskeysScreen(
        uiState = uiState,
        onBackPressed = navigateBack,
    )
}
