package com.salazar.cheers.ui.main.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.data.repository.PostRepository
import com.salazar.cheers.internal.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PostDetailUiState {

    val isLoading: Boolean
    val errorMessages: List<String>

    data class NoPost(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : PostDetailUiState

    data class HasPost(
        val postFeed: Post,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : PostDetailUiState
}

private data class PostDetailViewModelState(
    val postFeed: Post? = null,
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

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val service: Neo4jService,
    stateHandle: SavedStateHandle,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(PostDetailViewModelState(isLoading = true))
    lateinit var postId: String

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )


    init {
        stateHandle.get<String>("postId")?.let { postId ->
            this@PostDetailViewModel.postId = postId
            refreshPost()
        }
    }

    private fun refreshPost() {
        viewModelScope.launch {
            val post = postRepository.getPost(postId = postId)
            viewModelState.update {
                it.copy(postFeed = post)
            }
        }
    }

    fun toggleLike(post: Post) {
        viewModelScope.launch {
            postRepository.toggleLike(post = post)
        }
    }

    fun leavePost() {
        viewModelScope.launch {
            try {
//                Neo4jUtil.leavePost(postId = postId)
            } catch (e: Exception) {
                Log.e("HomeViewModel", e.toString())
            }
        }
    }

    fun deletePost() {
        viewModelScope.launch {
            try {
                postRepository.deletePost(postId = postId)
            } catch (e: Exception) {
                Log.e("PostDetailViewModel", e.toString())
            }
        }
    }
}