package com.salazar.cheers.ui.home

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.android.gms.ads.nativead.NativeAd
import com.salazar.cheers.data.Result
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.SuggestionUser
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.data.Neo4jRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {

    val isLoading: Boolean
    val errorMessages: List<String>
    val searchInput: String
    val suggestions: List<SuggestionUser>?
    val postSheetState: ModalBottomSheetState
    val nativeAd: NativeAd?

    data class NoPosts(
        override val suggestions: List<SuggestionUser>?,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val searchInput: String,
        override val postSheetState: ModalBottomSheetState,
        override val nativeAd: NativeAd?,
    ) : HomeUiState

    data class HasPosts(
        val postsFlow: Flow<PagingData<Post>>,
        val listState: LazyListState = LazyListState(),
        val favorites: Set<String>,
        override val postSheetState: ModalBottomSheetState,
        override val suggestions: List<SuggestionUser>?,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val searchInput: String,
        override val nativeAd: NativeAd?,
    ) : HomeUiState
}

private data class HomeViewModelState(
    val postsFlow: Flow<PagingData<Post>>? = null,
    val listState: LazyListState = LazyListState(),
    val suggestions: List<SuggestionUser>? = null,
    val favorites: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
    val sheetState: ModalBottomSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
    val nativeAd: NativeAd? = null,
) {
    fun toUiState(): HomeUiState =
        if (postsFlow == null) {
            HomeUiState.NoPosts(
                nativeAd = nativeAd,
                postSheetState = sheetState,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput,
                suggestions = suggestions,
            )
        } else {
            HomeUiState.HasPosts(
                nativeAd = nativeAd,
                postsFlow = postsFlow,
                listState = listState,
                postSheetState = sheetState,
                favorites = favorites,
                isLoading = isLoading,
                errorMessages = errorMessages,
                searchInput = searchInput,
                suggestions = suggestions,
            )
        }
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Neo4jRepository
): ViewModel() {

    private val viewModelState = MutableStateFlow(HomeViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refreshPostsFlow()
        refreshSuggestions()
    }

    fun refreshPostsFlow() {
        viewModelState.update { it.copy(isLoading = true) }
        viewModelState.update {
            it.copy(postsFlow = repository.getPosts(), isLoading = false)
        }
    }

    fun refreshSuggestions() {
        viewModelScope.launch {
            val result = Neo4jUtil.getSuggestions()
            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(suggestions = result.data)
                    is Result.Error -> it.copy(errorMessages = listOf(result.exception.toString()))
                }
            }
        }
    }

    fun toggleLike(post: Post) {
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

    fun selectPost(postId: String) {
//        viewModelState.update {
//            it.copy(selectedPostId = postId)
//        }
    }

    fun deletePost() {
//        val postId = viewModelState.value.selectedPostId ?: return
//        viewModelState.update {
//            it.copy(posts = it.posts?.filter { post -> post.id != postId })
//        }
        viewModelScope.launch {
            try {
//                Neo4jUtil.deletePost(postId = postId)
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

    fun setNativeAd(nativeAd: NativeAd) {
        viewModelState.update {
            it.copy(nativeAd = nativeAd)
        }
    }
}
