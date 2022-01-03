package com.salazar.cheers.ui.home

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.Result
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import com.salazar.cheers.util.Neo4jUtil
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface HomeUiState {

    val isLoading: Boolean
    val errorMessages: List<String>
    val searchInput: String
    val suggestions: List<User>?
    val postSheetState: ModalBottomSheetState

    data class NoPosts(
        override val suggestions: List<User>?,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val searchInput: String,
        override val postSheetState: ModalBottomSheetState,
    ) : HomeUiState

    data class HasPosts(
        val posts: List<Post>,
        val listState: LazyListState = LazyListState(),
        val selectedPost: Post,
        val favorites: Set<String>,
        override val postSheetState: ModalBottomSheetState,
        override val suggestions: List<User>?,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val searchInput: String,
    ) : HomeUiState
}

private data class HomeViewModelState(
    val posts: List<Post>? = null,
    val listState: LazyListState = LazyListState(),
    val suggestions: List<User>? = null,
    val selectedPostId: String? = null,
    val favorites: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
    val sheetState: ModalBottomSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
) {
    fun toUiState(): HomeUiState =
        if (posts == null || posts.isEmpty()) {
            HomeUiState.NoPosts(
                postSheetState = sheetState,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput,
                suggestions = suggestions,
            )
        } else {
            HomeUiState.HasPosts(
                listState = listState,
                postSheetState = sheetState,
                posts = posts,
                selectedPost = posts.find {
                    it.id == selectedPostId
                } ?: posts.last(),
                favorites = favorites,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput,
                suggestions = suggestions,
            )
        }
}

class HomeViewModel : ViewModel() {

    private val viewModelState = MutableStateFlow(HomeViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refreshPosts()
        refreshSuggestions()
    }

    private fun refreshSuggestions() {
        viewModelScope.launch {
            val result = Neo4jUtil.getUserRecommendations()
            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(suggestions = result.data)
                    is Result.Error -> it.copy(errorMessages = listOf(result.exception.toString()))
                }
            }
        }
    }

    fun refreshPosts() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            viewModelState.update {
                val result = Neo4jUtil.posts()
                when (result) {
                    is Result.Success -> it.copy(posts = result.data, isLoading = false)
                    is Result.Error -> it.copy(
                        isLoading = false,
                        errorMessages = listOf(result.exception.toString())
                    )
                }
            }
        }
    }

    fun toggleLike(postId: String, liked: Boolean) {
        viewModelState.value.posts?.find { it.id == postId }?.liked = true
        viewModelState.value.posts?.find { it.id == postId }?.likes = 99
//        viewModelState.update {
////            val post = post.copy(liked = !post.liked, likes = post.likes+1)
//            it.copy(posts)
//        }
        if (liked)
            likePost(postId)
        else
            unlikePost(postId)
    }

    private fun unlikePost(postId: String) {
        viewModelScope.launch {
            try {
                Neo4jUtil.unlikePost(postId = postId)
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

    fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                Neo4jUtil.deletePost(postId = postId)
            } catch (e: Exception) {
                Log.e("HomeViewModel", e.toString())
            }
        }
    }

    fun deleteErrorMessage() {
        viewModelState.update {
            it.copy(errorMessages = emptyList())
        }
    }
}
