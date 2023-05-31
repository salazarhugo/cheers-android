package com.salazar.cheers.notes.ui.note

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.map.ui.dialogs.BottomSheetM3
import com.salazar.cheers.core.share.ui.CheersNavigationActions

/**
 * Stateful composable that displays the Navigation route for the Note screen.
 *
 * @param viewModel ViewModel that handles the business logic of this screen
 */
@Composable
fun NoteRoute(
    navActions: CheersNavigationActions,
    viewModel: NoteViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BottomSheetM3 {
        NoteScreen(
            uiState = uiState,
            onNoteUIAction = { action ->
                when(action) {
                    NoteUIAction.OnBackPressed -> navActions.navigateBack()
                    NoteUIAction.OnSwipeRefresh -> TODO()
                    is NoteUIAction.OnTextChange -> viewModel.onTextChange(action.text)
                    NoteUIAction.OnCreateNewNoteClick -> navActions.navigateToCreateNote()
                    NoteUIAction.OnDeleteNoteClick -> {
                        viewModel.onDeleteNote {
                            navActions.navigateBack()
                        }
                    }
                }
            }
        )
    }
}
