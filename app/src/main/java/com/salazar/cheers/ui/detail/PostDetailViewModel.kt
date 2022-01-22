package com.salazar.cheers.ui.detail

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.salazar.cheers.MainActivity
import com.salazar.cheers.data.Result
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.SuggestionUser
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.data.Neo4jRepository
import com.salazar.cheers.ui.otherprofile.OtherProfileViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PostDetailUiState {

    val isLoading: Boolean
    val errorMessages: List<String>

    data class NoPosts(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : PostDetailUiState

    data class HasPost(
        val post: Post,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : PostDetailUiState
}

private data class PostDetailViewModelState(
    val post: Post? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
) {
    fun toUiState(): PostDetailUiState =
        if (post == null) {
            PostDetailUiState.NoPosts(
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        } else {
            PostDetailUiState.HasPost(
                post = post,
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        }
}

class PostDetailViewModel @AssistedInject constructor(
//    private val repository: Neo4jRepository,
    @Assisted private val postId: String
): ViewModel() {

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

    fun refreshPost() {
        viewModelScope.launch {
            val result = Neo4jUtil.getPost(postId = postId)
            viewModelState.update {
                when (result) {
                    is Result.Success -> it.copy(post = result.data)
                    is Result.Error -> it.copy(errorMessages = listOf(result.exception.toString()))
                }
            }
        }
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

    private fun likePost(postId: String) {
        viewModelScope.launch {
            try {
                Neo4jUtil.likePost(postId = postId)
            } catch (e: Exception) {
                Log.e("PostDetailViewModel", e.toString())
            }
        }
    }

    fun selectPost(postId: String) {
//        viewModelState.update {
//            it.copy(selectedPostId = postId)
//        }
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

    fun deleteErrorMessage() {
        viewModelState.update {
            it.copy(errorMessages = emptyList())
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
