package com.salazar.cheers.comment.ui.comment_more

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.comment.data.CommentRepository
import com.salazar.cheers.comment.domain.models.Comment
import com.salazar.cheers.comment.domain.usecase.get_comment.GetCommentUseCase
import com.salazar.cheers.core.data.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommentMoreUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val comment: Comment? = null,
)

@HiltViewModel
class CommentMoreViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val getCommentUseCase: GetCommentUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(CommentMoreUiState(isLoading = true))
    private lateinit var commentID: String

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
        viewModelScope.launch {
            getCommentUseCase(commentId = commentID).onSuccess {
                updateComment(comment = it)
            }
        }
    }

    private fun updateComment(comment: Comment) {
        viewModelState.update {
            it.copy(comment = comment)
        }
    }
}