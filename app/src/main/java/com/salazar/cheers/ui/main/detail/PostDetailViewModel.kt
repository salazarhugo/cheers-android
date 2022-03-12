package com.salazar.cheers.ui.main.detail

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.salazar.cheers.MainActivity
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.data.repository.PostRepository
import com.salazar.cheers.data.db.PostFeed
import com.salazar.cheers.internal.Post
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface PostDetailUiState {

    val isLoading: Boolean
    val errorMessages: List<String>

    data class NoPost(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : PostDetailUiState

    data class HasPost(
        val postFeed: PostFeed,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : PostDetailUiState
}

private data class PostDetailViewModelState(
    val postFeed: PostFeed? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
) {
    fun toUiState(): PostDetailUiState =
        if (postFeed == null) {
            PostDetailUiState.NoPost(
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        } else {
            PostDetailUiState.HasPost(
                postFeed = postFeed,
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        }
}

class PostDetailViewModel @AssistedInject constructor(
    private val repository: PostRepository,
    @Assisted private val postId: String
) : ViewModel() {

    private val viewModelState = MutableStateFlow(PostDetailViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refreshPost()
    }

    private fun refreshPost() {
        viewModelScope.launch {
            val post = repository.getPost(postId = postId)
            viewModelState.update {
                it.copy(postFeed = post)
            }
        }
    }

    fun toggleLike(post: Post) {
        val likes = if (post.liked) post.likes - 1 else post.likes + 1
        viewModelScope.launch {
            repository.postDao.update(post.copy(liked = !post.liked, likes = likes))
        }
        if (post.liked)
            unlikePost(post.id)
        else
            likePost(post.id)
    }

    private fun unlikePost(postId: String) {
        viewModelScope.launch {
            try {
                Neo4jUtil.unlikePost(postId = postId)
            } catch (e: Exception) {
                Log.e("PostDetailViewModel", e.toString())
            }
        }
    }

    fun leavePost() {
        viewModelScope.launch {
            try {
                Neo4jUtil.leavePost(postId = postId)
            } catch (e: Exception) {
                Log.e("HomeViewModel", e.toString())
            }
        }
    }

    private fun likePost(postId: String) {
        viewModelScope.launch {
            try {
                Neo4jUtil.likePost(postId = postId)
            } catch (e: Exception) {
                Log.e("HomeViewModel", e.toString())
            }
        }
    }

    fun deletePost() {
        viewModelScope.launch {
            try {
                Neo4jUtil.deletePost(postId = postId)
            } catch (e: Exception) {
                Log.e("PostDetailViewModel", e.toString())
            }
        }
    }

    @AssistedFactory
    interface PostDetailViewModelFactory {
        fun create(postId: String): PostDetailViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: PostDetailViewModelFactory,
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
fun postDetailViewModel(postId: String): PostDetailViewModel {
    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        MainActivity.ViewModelFactoryProvider::class.java
    ).postDetailViewModelFactory()

    return viewModel(factory = PostDetailViewModel.provideFactory(factory, postId = postId))
}
