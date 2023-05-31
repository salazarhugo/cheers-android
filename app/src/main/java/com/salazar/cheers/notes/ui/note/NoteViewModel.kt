package com.salazar.cheers.notes.ui.note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.notes.data.repository.NoteRepository
import com.salazar.cheers.notes.domain.models.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NoteUiState(
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val note: Note? = null,
    val text: String = "",
)

@HiltViewModel
class NoteViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val noteRepository: NoteRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(NoteUiState(isLoading = false))
    private lateinit var userID: String

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("userID")?.let { userID ->
            this.userID = userID
            getNote()
        }
    }

    private fun getNote() {
        viewModelScope.launch {
            noteRepository.getNote(userID = userID).collect(::updateNote)
        }
    }

    private fun updateNote(note: Note) {
        viewModelState.update {
            it.copy(note = note)
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

    fun onDeleteNote(onComplete: () -> Unit) {
        viewModelScope.launch {
            noteRepository.deleteNote()
            onComplete()
        }
    }
}

sealed class NoteUIAction {
    object OnBackPressed : NoteUIAction()
    object OnSwipeRefresh : NoteUIAction()
    object OnCreateNewNoteClick : NoteUIAction()
    object OnDeleteNoteClick : NoteUIAction()
    data class OnTextChange(val text: String) : NoteUIAction()
}
