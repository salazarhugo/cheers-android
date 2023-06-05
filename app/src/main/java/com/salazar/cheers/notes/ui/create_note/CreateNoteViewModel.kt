package com.salazar.cheers.notes.ui.create_note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.note.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateNoteUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val text: String = "",
)

@HiltViewModel
class CreateNoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(CreateNoteUiState(isLoading = false))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
    }

    fun createNote(onComplete: () -> Unit) {
        val state = uiState.value
        val text = state.text

        if (text.isBlank())
            return

        if (text.length > 60)
            return

        viewModelScope.launch {
            updateIsLoading(true)
            noteRepository.createNote(text = text).onSuccess {
                onComplete()
            }
            updateIsLoading(false)
        }
    }

    fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

    fun onTextChange(text: String) {
        viewModelState.update {
            it.copy(text = text)
        }
    }
}

sealed class CreateNoteUIAction {
    object OnBackPressed : CreateNoteUIAction()
    object OnSwipeRefresh : CreateNoteUIAction()
    object OnCreateNote : CreateNoteUIAction()
    data class OnTextChange(val text: String) : CreateNoteUIAction()
}
