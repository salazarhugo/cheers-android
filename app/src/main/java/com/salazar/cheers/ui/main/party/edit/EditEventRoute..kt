package com.salazar.cheers.ui.main.party.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun EditEventRoute(
    editEventViewModel: EditEventViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val uiState by editEventViewModel.uiState.collectAsStateWithLifecycle()

    EditEventScreen(
        uiState = uiState,
        onDismiss = navigateBack,
        onSave = editEventViewModel::onSave,
    )
}
