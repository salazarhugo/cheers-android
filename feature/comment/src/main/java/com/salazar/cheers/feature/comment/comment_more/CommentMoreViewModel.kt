package com.salazar.cheers.feature.comment.comment_more

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cheers.type.UserOuterClass
import com.salazar.cheers.core.model.Comment
import com.salazar.cheers.domain.get_comment.GetCommentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommentMoreUiState(
    val user: UserOuterClass.User? = null,
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