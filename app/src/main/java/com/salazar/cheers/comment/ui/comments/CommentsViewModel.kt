package com.salazar.cheers.comment.ui.comments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.comment.data.CommentRepository
import com.salazar.cheers.comment.domain.models.Comment
import com.salazar.cheers.core.data.Resource
import com.salazar.cheers.post.data.repository.PostRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.comment.domain.usecase.create_comment.CreateCommentUseCase
import com.salazar.cheers.comment.domain.usecase.like_comment.LikeCommentUseCase
import com.salazar.cheers.core.data.internal.Post
import com.salazar.cheers.core.data.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommentsUiState(
    val user: User? = null,
    val comments: List<Comment>? = null,
    val isLoading: Boolean = false,
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
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val createCommentUseCase: CreateCommentUseCase,
    private val likeCommentUseCase: LikeCommentUseCase,
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
        stateHandle.get<String>("postId")?.let {
            postId = it
        }
        viewModelScope.launch {
            val user = userRepository.getCurrentUser()
            viewModelState.update {
                it.copy(user = user)
            }
        }
        viewModelScope.launch {
            val post = postRepository.getPost(postId)
            viewModelState.update {
                it.copy(post = post)
            }
        }
        viewModelScope.launch {
            commentRepository.listComment(postId = postId).collect(::updateComments)
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
            commentRepository.listComment(postId = postId).collect(::updateComments)
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
        val isFollowed = viewModelState.value.user?.followBack ?: return
        updateIsFollowed(isFollowed = !isFollowed)
    }

    private fun updateIsFollowed(isFollowed: Boolean) {
        viewModelState.update {
            it.copy(user = it.user?.copy(followBack = isFollowed))
        }
    }

    fun deleteComment(commentId: String) {
    }

    fun onComment() {
        val state = uiState.value
        val text = state.input
        val user = state.user ?: return

        val comment = Comment(
            username = user.username,
            verified = user.verified,
            avatar = user.picture,
            postId = postId,
            authorId = FirebaseAuth.getInstance().currentUser?.uid!!,
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
}

sealed class CommentsUIAction {
    object OnBackPressed : CommentsUIAction()
    object OnSwipeRefresh : CommentsUIAction()
    object OnFriendRequestsClick : CommentsUIAction()
    object OnRemoveReplyComment : CommentsUIAction()
    data class OnReplyClick(val comment: Comment) : CommentsUIAction()
    data class OnUserClick(val userId: String) : CommentsUIAction()
    data class OnCommentLongClick(val commentID: String) : CommentsUIAction()
    data class OnCommentLike(val commentID: String) : CommentsUIAction()
}
