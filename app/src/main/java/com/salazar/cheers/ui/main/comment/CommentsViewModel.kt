package com.salazar.cheers.ui.main.comment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.Comment
import com.salazar.cheers.internal.CommentWithAuthor
import com.salazar.cheers.internal.User
import com.salazar.cheers.util.FirestoreUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

sealed interface CommentsUiState {

    val isLoading: Boolean
    val errorMessages: List<String>
    val isFollowing: Boolean
    val user: User
    val input: String
    val shortLink: String?

    data class NoPosts(
        override val input: String,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val isFollowing: Boolean,
        override val user: User,
        override val shortLink: String?,
    ) : CommentsUiState

    data class HasPosts(
        val comments: List<CommentWithAuthor>,
        override val input: String,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val isFollowing: Boolean,
        override val user: User,
        override val shortLink: String?,
    ) : CommentsUiState
}

private data class CommentsViewModelState(
    val user: User? = null,
    val comments: List<CommentWithAuthor>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val isFollowing: Boolean = false,
    val shortLink: String? = null,
    val input: String = "",
) {
    fun toUiState(): CommentsUiState =
        if (comments != null)
            CommentsUiState.HasPosts(
                input = input,
                isLoading = isLoading,
                errorMessages = errorMessages,
                isFollowing = isFollowing,
                comments = comments,
                user = user ?: User(),
                shortLink = shortLink,
            )
        else
            CommentsUiState.NoPosts(
                input = input,
                user = user ?: User(),
                isLoading = isLoading,
                errorMessages = errorMessages,
                isFollowing = isFollowing,
                shortLink = shortLink,
            )
}

@HiltViewModel
class CommentsViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(CommentsViewModelState(isLoading = true))
    private lateinit var postId: String

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        stateHandle.get<String>("postId")?.let {
            postId = it
        }
    }

    val user = FirestoreUtil.getCurrentUserDocumentLiveData()

    private fun toggleIsFollowed() {
        val isFollowed = viewModelState.value.user?.followBack ?: return
        updateIsFollowed(isFollowed = !isFollowed)
    }

    private fun updateIsFollowed(isFollowed: Boolean) {
        viewModelState.update {
            it.copy(user = it.user?.copy(followBack = isFollowed))
        }
    }

    fun onComment() {
        val text = viewModelState.value.input

        val comment = Comment(
            postId = postId,
            authorId = FirebaseAuth.getInstance().currentUser?.uid!!,
            text = text,
        )
        FirestoreUtil.addComment(comment = comment)
        onInputChange("")
    }

    fun onInputChange(input: String) {
        viewModelState.update {
            it.copy(input = input)
        }
    }
}