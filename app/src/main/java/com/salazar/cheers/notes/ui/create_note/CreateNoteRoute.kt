package com.salazar.cheers.notes.ui.create_note

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.share.ui.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Create Note screen.
 *
 * @param viewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun CreateNoteRoute(
    navActions: CheersNavigationActions,
    viewModel: CreateNoteViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateNoteScreen(
        uiState = uiState,
        onCreateNoteUIAction = { action ->
            when(action) {
                CreateNoteUIAction.OnBackPressed -> navActions.navigateBack()
                CreateNoteUIAction.OnSwipeRefresh -> TODO()
                CreateNoteUIAction.OnCreateNote -> {
                    viewModel.createNote {
                        navActions.navigateBack()
                    }
                }
                is CreateNoteUIAction.OnTextChange -> viewModel.onTextChange(action.text)
            }
        }
    )
}
