package com.salazar.cheers.ui.comment

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.salazar.cheers.MainActivity
import com.salazar.cheers.data.Result
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import com.salazar.cheers.util.FirestoreUtil
import com.salazar.cheers.backend.Neo4jUtil
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
        val posts: List<Post>,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val isFollowing: Boolean,
        override val user: User,
        override val shortLink: String?,
    ) : CommentsUiState
}

private data class CommentsViewModelState(
    val user: User? = null,
    val posts: List<Post>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val isFollowing: Boolean = false,
    val shortLink: String? = null,
) {
    fun toUiState(): CommentsUiState =
        if (posts == null || posts.isEmpty()) {
            CommentsUiState.NoPosts(
                user = user ?: User(),
                isLoading = isLoading,
                errorMessages = errorMessages,
                isFollowing = isFollowing,
                shortLink = shortLink,
            )
        } else {
            CommentsUiState.HasPosts(
                isLoading = isLoading,
                errorMessages = errorMessages,
                isFollowing = isFollowing,
                posts = posts,
                user = user ?: User(),
                shortLink = shortLink,
            )
        }
}

class CommentsViewModel @AssistedInject constructor(
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
        refresh()
    }

    fun refresh() {
        refreshUser(postId = postId)
        refreshUserPosts(postId = postId)
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

    private fun refreshUser(postId: String) {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            viewModelState.update {
                when (val result = Neo4jUtil.getUserWithUsername(postId)) {
                    is Result.Success -> it.copy(user = result.data, isLoading = false)
                    is Result.Error -> it.copy(
                        isLoading = false,
                        errorMessages = listOf(result.exception.toString())
                    )
                }
            }
        }
    }

    private fun refreshUserPosts(postId: String) {
        viewModelScope.launch {
//            viewModelState.update {
//                when (val result = Neo4jUtil.getUserPosts(postId = postId)) {
//                    is Result.Success -> it.copy(posts = result.data, isLoading = false)
//                    is Result.Error -> it.copy(
//                        errorMessages = listOf(result.exception.toString()),
//                        isLoading = false
//                    )
//                }
//            }
        }
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