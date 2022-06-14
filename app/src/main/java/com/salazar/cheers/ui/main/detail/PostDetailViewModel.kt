package com.salazar.cheers.ui.main.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.backend.Neo4jService
import com.salazar.cheers.data.repository.PostRepository
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
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
        val members: List<User>?,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : PostDetailUiState
}

private data class PostDetailViewModelState(
    val postFeed: Post? = null,
    val members: List<User>? = null,
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
                members = members,
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
        viewModelScope.launch {
            val members = postRepository.getPostMembers(postId = postId)
            updateMembers(members)
        }
        viewModelScope.launch {
            postRepository.postFlow(postId = postId).collect { post ->
                updatePost(post)
            }
        }
    }

    private fun refreshPost() {
        viewModelScope.launch {
            val post = postRepository.getPost(postId = postId)
            updatePost(post)
        }
    }

    private fun updateMembers(members: List<User>?) {
        viewModelState.update {
            it.copy(members = members)
        }
    }

    private fun updatePost(post: Post) {
        viewModelState.update {
            it.copy(postFeed = post)
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