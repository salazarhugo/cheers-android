package com.salazar.cheers.feature.home.note

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.model.UserID

@Composable
fun NoteRoute(
    userID: UserID,
    navigateBack: () -> Unit,
    navigateToCreateNote: () -> Unit,
    viewModel: NoteViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.setUserId(userID)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NoteScreen(
        uiState = uiState,
        onNoteUIAction = { action ->
            when (action) {
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
