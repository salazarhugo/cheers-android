package com.salazar.cheers.feature.chat.ui.screens.mediapreview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MediaPreviewRoute(
    viewModel: MediaPreviewViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MediaPreviewScreen(
        uiState = uiState,
        onBackPressed = navigateBack,
    )
}
