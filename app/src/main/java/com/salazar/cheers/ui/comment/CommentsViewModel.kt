package com.salazar.cheers.ui.comment

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.MainActivity
import com.salazar.cheers.data.UserRepository
import com.salazar.cheers.internal.CommentWithAuthor
import com.salazar.cheers.internal.User
import com.salazar.cheers.util.FirestoreUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface CommentsUiState {

    val isLoading: Boolean
    val errorMessages: List<String>
    val isFollowing: Boolean
    val user: User
    val shortLink: String?

    data class NoPosts(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val isFollowing: Boolean,
        override val user: User,
        override val shortLink: String?,
    ) : CommentsUiState

    data class HasPosts(
        val comments: List<CommentWithAuthor>,
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
) {
    fun toUiState(): CommentsUiState =
        if (comments != null)
            CommentsUiState.HasPosts(
                isLoading = isLoading,
                errorMessages = errorMessages,
                isFollowing = isFollowing,
                comments = comments,
                user = user ?: User(),
                shortLink = shortLink,
            )
        else
            CommentsUiState.NoPosts(
                user = user ?: User(),
                isLoading = isLoading,
                errorMessages = errorMessages,
                isFollowing = isFollowing,
                shortLink = shortLink,
            )
}

class CommentsViewModel @AssistedInject constructor(
    private val userRepository: UserRepository,
    @Assisted private val postId: String
) : ViewModel() {

    private val viewModelState = MutableStateFlow(CommentsViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        viewModelScope.launch {
            FirestoreUtil.getComments(postId).collect { comments ->
                val commentWithAuthor = comments.map {
                    CommentWithAuthor(comment = it, author = userRepository.getUser(it.authorId))
                }
                viewModelState.update { it.copy(comments = commentWithAuthor) }
            }
        }
    }

    val user = FirestoreUtil.getCurrentUserDocumentLiveData()

    private fun toggleIsFollowed() {
        val isFollowed = viewModelState.value.user?.isFollowed ?: return
        updateIsFollowed(isFollowed = !isFollowed)
    }

    private fun updateIsFollowed(isFollowed: Boolean) {
        viewModelState.update {
            it.copy(user = it.user?.copy(isFollowed = isFollowed))
        }
    }

    fun onComment(text: String) {
        val comment = com.salazar.cheers.internal.Comment(
            postId = postId,
            authorId = FirebaseAuth.getInstance().currentUser?.uid!!,
            text = text,
        )
        FirestoreUtil.addComment(comment = comment)
    }

    private fun refreshComments(postId: String) {
    }

    @AssistedFactory
    interface CommentsViewModelFactory {
        fun create(postId: String): CommentsViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: CommentsViewModelFactory,
            postId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(postId = postId) as T
            }
        }
    }
}

@Composable
fun commentsViewModel(postId: String): CommentsViewModel {
    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        MainActivity.ViewModelFactoryProvider::class.java
    ).commentsViewModelFactory()

    return viewModel(factory = CommentsViewModel.provideFactory(factory, postId = postId))
}