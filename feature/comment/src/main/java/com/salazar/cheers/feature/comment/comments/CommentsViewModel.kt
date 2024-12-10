package com.salazar.cheers.feature.comment.comments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.Post
import com.salazar.cheers.core.model.Comment
import com.salazar.cheers.domain.create_comment.CreateCommentUseCase
import com.salazar.cheers.domain.get_account.GetAccountUseCase
import com.salazar.cheers.domain.like_comment.LikeCommentUseCase
import com.salazar.cheers.domain.list_post_comments.ListPostCommentsUseCase
import com.salazar.cheers.shared.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommentsUiState(
    val avatar: String? = null,
    val comments: List<Comment>? = null,
    val isLoading: Boolean = false,
    val showBanner: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val isFollowing: Boolean = false,
    val shortLink: String? = null,
    val input: String = "",
    val post: Post? = null,
    val replyComment: Comment? = null,
)

@HiltViewModel
class CommentsViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val createCommentUseCase: CreateCommentUseCase,
    private val likeCommentUseCase: LikeCommentUseCase,
    private val getAccountUseCase: GetAccountUseCase,
    private val listPostCommentsUseCase: ListPostCommentsUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(CommentsUiState(isLoading = true))
    private lateinit var postId: String

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>(POST_ID)?.let {
            postId = it
        }

        viewModelScope.launch {
            val account = getAccountUseCase().first()
            viewModelState.update {
                it.copy(avatar = account?.picture)
            }
        }

        viewModelScope.launch {
//            val post = postRepository.getPost(postId)
//            viewModelState.update {
//                it.copy(post = post)
//            }
        }
        viewModelScope.launch {
            listPostCommentsUseCase(postID = postId).collect(::updateComments)
        }
    }

    fun onRemoveReplyComment() {
        viewModelState.update {
            it.copy(replyComment = null)
        }
    }

    fun onReplyCommentClick(comment: Comment) {
        viewModelState.update {
            it.copy(replyComment = comment)
        }
    }

    fun onSwipeRefresh() {
        viewModelState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
//            commentRepository.listComment(postId = postId).collect(::updateComments)
        }
    }

    private fun updateComments(resource: Resource<List<Comment>>) {
        when(resource) {
            is Resource.Error ->  viewModelState.update {
                it.copy(errorMessage = resource.message, isLoading = false, isRefreshing = false)
            }
            is Resource.Loading -> viewModelState.update {
                it.copy(isLoading = resource.isLoading)
            }
            is Resource.Success -> viewModelState.update {
                it.copy(comments = resource.data, isLoading = false, isRefreshing = false)
            }
        }
    }

    private fun toggleIsFollowed() {
//        val isFollowed = viewModelState.value.user?.followBack ?: return
//        updateIsFollowed(isFollowed = !isFollowed)
    }

    private fun updateIsFollowed(isFollowed: Boolean) {
//        viewModelState.update {
//            it.copy(user = it.user?.copy(followBack = isFollowed))
//        }
    }

    fun deleteComment(commentId: String) {
    }

    fun onComment() {
        val state = uiState.value
        val text = state.input

        val comment = Comment(
            username = "cheers",
            verified = true,
            avatar = state.avatar,
            postId = postId,
//            authorId = FirebaseAuth.getInstance().currentUser?.uid!!,
            text = text,
        )
        viewModelScope.launch {
            createCommentUseCase(
                postId = postId,
                comment = comment.text,
                replyToCommentId = state.replyComment?.id
            )
        }
        onInputChange("")
        onRemoveReplyComment()
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

    fun onCloseBannerClick() {
        viewModelState.update {
            it.copy(showBanner = false)
        }
    }
}

sealed class CommentsUIAction {
    data object OnBackPressed : CommentsUIAction()
    data object OnSwipeRefresh : CommentsUIAction()
    data object OnFriendRequestsClick : CommentsUIAction()
    data object OnRemoveReplyComment : CommentsUIAction()
    data object OnCloseBannerClick : CommentsUIAction()
    data class OnReplyClick(val comment: Comment) : CommentsUIAction()
    data class OnUserClick(val userId: String) : CommentsUIAction()
    data class OnCommentLongClick(val commentID: String) : CommentsUIAction()
    data class OnCommentLike(val commentID: String) : CommentsUIAction()
}
