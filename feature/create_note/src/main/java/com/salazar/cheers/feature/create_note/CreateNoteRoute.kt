package com.salazar.cheers.feature.create_note

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.salazar.cheers.core.ui.ui.CheersNavigationActions

@Composable
fun CreateNoteRoute(
    navigateBack: () -> Unit,
    viewModel: CreateNoteViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateNoteScreen(
        uiState = uiState,
        onCreateNoteUIAction = { action ->
            when(action) {
                CreateNoteUIAction.OnBackPressed -> navigateBack()
                CreateNoteUIAction.OnSwipeRefresh -> TODO()
                CreateNoteUIAction.OnCreateNote -> {
                    viewModel.createNote {
                        navigateBack()
                    }
                }
                is CreateNoteUIAction.OnTextChange -> viewModel.onTextChange(action.text)
            }
        }
    )
}
