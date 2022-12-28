package com.salazar.cheers.ui.main.comment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.repository.PostRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.data.repository.comment.CommentRepository
import com.salazar.cheers.domain.usecase.create_comment.CreateCommentUseCase
import com.salazar.cheers.internal.Comment
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import com.salazar.cheers.util.FirestoreUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommentsUiState(
    val user: User? = null,
    val comments: List<Comment>? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isFollowing: Boolean = false,
    val shortLink: String? = null,
    val input: String = "",
    val post: Post? = null,
)

@HiltViewModel
class CommentsViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val createCommentUseCase: CreateCommentUseCase,
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

    fun onSwipeRefresh() {
        viewModelScope.launch {
            commentRepository.listComment(postId = postId).collect(::updateComments)
        }
    }

    private fun updateComments(resource: Resource<List<Comment>>) {
        when(resource) {
            is Resource.Error ->  viewModelState.update {
                it.copy(errorMessage = resource.message)
            }
            is Resource.Loading -> viewModelState.update {
                it.copy(isLoading = resource.isLoading)
            }
            is Resource.Success -> viewModelState.update {
                it.copy(comments = resource.data)
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
        val text = viewModelState.value.input

        val user = viewModelState.value.user ?: return

        val comment = Comment(
            username = user.username,
            verified = user.verified,
            avatar = user.picture,
            postId = postId,
            authorId = FirebaseAuth.getInstance().currentUser?.uid!!,
            text = text,
        )
        viewModelScope.launch {
            createCommentUseCase(postId = postId, comment = comment.text)
        }
        onInputChange("")
    }

    fun onInputChange(input: String) {
        viewModelState.update {
            it.copy(input = input)
        }
    }
}