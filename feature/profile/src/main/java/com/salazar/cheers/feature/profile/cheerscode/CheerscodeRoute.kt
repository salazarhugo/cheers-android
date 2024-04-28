package com.salazar.cheers.feature.profile.cheerscode

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CheerscodeRoute(
    onBackPressed: () -> Unit,
    viewModel: CheerscodeViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsState().value

    CheerscodeScreen(
        link = uiState.link,
        onBackPressed = onBackPressed,
    )
}
