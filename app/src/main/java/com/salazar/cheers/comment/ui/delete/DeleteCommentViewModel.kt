package com.salazar.cheers.comment.ui.delete

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.comment.domain.usecase.delete_comment.DeleteCommentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
)

@HiltViewModel
class DeleteCommentViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val deleteCommentUseCase: DeleteCommentUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(HomeUiState())
    lateinit var commentID: String

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("commentID")?.let {
            commentID = it
        }
    }

    fun deleteComment(onCompleted: () -> Unit) {
        if (!::commentID.isInitialized)
            return

        viewModelScope.launch {
            deleteCommentUseCase(commentID = commentID)
            onCompleted()
        }
    }
}