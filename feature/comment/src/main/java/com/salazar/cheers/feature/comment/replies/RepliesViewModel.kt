package com.salazar.cheers.feature.comment.replies

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.Comment
import com.salazar.cheers.data.account.Account
import com.salazar.cheers.domain.create_comment.CreateCommentUseCase
import com.salazar.cheers.domain.get_account.GetAccountUseCase
import com.salazar.cheers.domain.get_comment.GetCommentUseCase
import com.salazar.cheers.domain.like_comment.LikeCommentUseCase
import com.salazar.cheers.domain.list_replies.ListRepliesUseCase
import com.salazar.cheers.shared.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RepliesUiState(
    val account: Account? = null,
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
    private val createCommentUseCase: CreateCommentUseCase,
    private val getCommentUseCase: GetCommentUseCase,
    private val listRepliesUseCase: ListRepliesUseCase,
    private val likeCommentUseCase: LikeCommentUseCase,
    private val getAccountUseCase: GetAccountUseCase,
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
        stateHandle.get<String>(COMMENT_ID)?.let {
            commentId = it
        }

        viewModelScope.launch {
            val account = getAccountUseCase().first()
            viewModelState.update {
                it.copy(account = account)
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
    data object OnBackPressed : RepliesUIAction()
    data object OnSwipeRefresh : RepliesUIAction()
    data object OnFriendRequestsClick : RepliesUIAction()
    data object OnRemoveReplyComment : RepliesUIAction()
    data class OnReplyClick(val comment: Comment) : RepliesUIAction()
    data class OnCommentLongClick(val commentID: String) : RepliesUIAction()
    data class OnUserClick(val userId: String) : RepliesUIAction()
    data class OnCommentLike(val commentID: String) : RepliesUIAction()
}
