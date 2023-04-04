package com.salazar.cheers.comment.ui.replies

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.comment.data.CommentRepository
import com.salazar.cheers.comment.domain.models.Comment
import com.salazar.cheers.core.data.Resource
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.comment.domain.usecase.create_comment.CreateCommentUseCase
import com.salazar.cheers.comment.domain.usecase.get_comment.GetCommentUseCase
import com.salazar.cheers.comment.domain.usecase.like_comment.LikeCommentUseCase
import com.salazar.cheers.comment.domain.usecase.list_replies.ListRepliesUseCase
import com.salazar.cheers.core.data.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RepliesUiState(
    val user: User? = null,
    val replies: List<Comment>? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isFollowing: Boolean = false,
    val shortLink: String? = null,
    val input: String = "",
    val comment: Comment? = null,
)

@HiltViewModel
class RepliesViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
    private val createCommentUseCase: CreateCommentUseCase,
    private val getCommentUseCase: GetCommentUseCase,
    private val listRepliesUseCase: ListRepliesUseCase,
    private val likeCommentUseCase: LikeCommentUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(RepliesUiState(isLoading = true))
    private lateinit var commentId: String

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("commentId")?.let {
            commentId = it
        }

        viewModelScope.launch {
            val user = userRepository.getCurrentUser()
            viewModelState.update {
                it.copy(user = user)
            }
        }

        viewModelScope.launch {
            getCommentUseCase(commentId = commentId).onSuccess {
                updateComment(comment = it)
            }
        }

        viewModelScope.launch {
            listRepliesUseCase(commentId = commentId).collect(::updateReplies)
        }
    }

    private fun updateReplies(resource: Resource<List<Comment>>) {
        when(resource) {
            is Resource.Error ->  viewModelState.update {
                it.copy(errorMessage = resource.message, isLoading = false)
            }
            is Resource.Loading -> viewModelState.update {
                it.copy(isLoading = resource.isLoading)
            }
            is Resource.Success -> viewModelState.update {
                it.copy(replies = resource.data, isLoading = false)
            }
        }
    }

    private fun updateComment(comment: Comment) {
        viewModelState.update {
            it.copy(comment = comment)
        }
    }

    fun onSwipeRefresh() {
        viewModelScope.launch {
            listRepliesUseCase(commentId = commentId).collect(::updateReplies)
        }
    }

    fun deleteComment(commentId: String) {
    }

    fun onComment() {
        val state = uiState.value
        val text = state.input

        viewModelScope.launch {
            createCommentUseCase(
                postId = commentId,
                comment = text,
                replyToCommentId = state.comment?.id
            )
        }
        onInputChange("")
    }

    fun onInputChange(input: String) {
        viewModelState.update {
            it.copy(input = input)
        }
    }

    fun onLike(commentID: String) {
        viewModelScope.launch {
            likeCommentUseCase(commentId = commentID)
        }
    }
}

sealed class RepliesUIAction {
    object OnBackPressed : RepliesUIAction()
    object OnSwipeRefresh : RepliesUIAction()
    object OnFriendRequestsClick : RepliesUIAction()
    object OnRemoveReplyComment : RepliesUIAction()
    data class OnReplyClick(val comment: Comment) : RepliesUIAction()
    data class OnCommentLongClick(val commentID: String) : RepliesUIAction()
    data class OnUserClick(val userId: String) : RepliesUIAction()
    data class OnCommentLike(val commentID: String) : RepliesUIAction()
}
