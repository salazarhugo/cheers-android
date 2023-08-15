package com.salazar.cheers.notes.ui.note

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.salazar.cheers.map.ui.dialogs.BottomSheetM3

@Composable
fun NoteRoute(
    navigateBack: () -> Unit,
    navigateToCreateNote: () -> Unit,
    viewModel: NoteViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BottomSheetM3 {
        NoteScreen(
            uiState = uiState,
            onNoteUIAction = { action ->
                when(action) {
                    NoteUIAction.OnBackPressed -> navigateBack()
                    NoteUIAction.OnSwipeRefresh -> TODO()
                    is NoteUIAction.OnTextChange -> viewModel.onTextChange(action.text)
                    NoteUIAction.OnCreateNewNoteClick -> navigateToCreateNote()
                    NoteUIAction.OnDeleteNoteClick -> {
                        viewModel.onDeleteNote {
                            navigateBack()
                        }
                    }
                }
            }
        )
    }
}
