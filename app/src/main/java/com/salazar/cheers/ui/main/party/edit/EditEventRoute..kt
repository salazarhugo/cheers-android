package com.salazar.cheers.ui.main.party.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the EditEvent screen.
 *
 * @param editEventViewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun EditEventRoute(
    editEventViewModel: EditEventViewModel = hiltViewModel(),
    navActions: CheersNavigationActions,
) {
    val uiState by editEventViewModel.uiState.collectAsStateWithLifecycle()

    EditEventScreen(
        uiState = uiState,
        onDismiss = { navActions.navigateBack() },
        onSave = editEventViewModel::onSave,
    )
}
