package com.salazar.cheers.ui.profile

import androidx.compose.animation.core.updateTransition
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.Result
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.util.addOrRemove
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterialApi::class)
sealed interface ProfileUiState {

    val isLoading: Boolean
    val errorMessages: List<String>
    val sheetState: ModalBottomSheetState

    data class Loading(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val sheetState: ModalBottomSheetState,
    ) : ProfileUiState

    data class HasUser(
        val user: User,
        val posts: List<Post>,
        override val sheetState: ModalBottomSheetState,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : ProfileUiState
}

private data class ProfileViewModelState @ExperimentalMaterialApi constructor(
    val user: User? = null,
    val posts: List<Post>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val sheetState: ModalBottomSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
) {
    fun toUiState(): ProfileUiState =
        if (user != null && posts != null)
            ProfileUiState.HasUser(
                posts = posts,
                user = user,
                isLoading = isLoading,
                errorMessages = errorMessages,
                sheetState = sheetState,
            )
        else
            ProfileUiState.Loading(
                isLoading = isLoading,
                errorMessages = errorMessages,
                sheetState = sheetState,
            )
}

@ExperimentalMaterialApi
@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val viewModelState = MutableStateFlow(ProfileViewModelState(isLoading = true))

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
        refreshUser()
        refreshUserPosts()
    }
    /**
     * Toggle like of a post
     */
    fun toggleLike(post: Post) {
        val posts = viewModelState.value.posts ?: return
        val mPosts = posts.toMutableList()
        val post = mPosts.find { it.id == post.id } ?: return

        val updatedPost = if (post.liked)
            post.copy(likes = post.likes - 1, liked = false)
        else
            post.copy(likes = post.likes + 1, liked = true)

        mPosts[mPosts.indexOf(post)] = updatedPost

        toggleLike(postId = post.id, like = updatedPost.liked)
        updatePosts(mPosts)
    }

    private fun toggleLike(postId: String, like: Boolean) {
        viewModelScope.launch {
            if (like)
                Neo4jUtil.likePost(postId = postId)
            else
                Neo4jUtil.unlikePost(postId = postId)
        }
    }

    private fun updatePosts(posts: List<Post>) {
        viewModelState.update {
            it.copy(posts = posts)
        }
    }

    private fun refreshUser() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            viewModelState.update {
                when (val result = Neo4jUtil.getCurrentUser()) {
                    is Result.Success -> it.copy(user = result.data, isLoading = false)
                    is Result.Error -> it.copy(
                        errorMessages = listOf(result.exception.toString()),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun refreshUserPosts() {
        viewModelScope.launch {
            viewModelState.update {
                when (val result = Neo4jUtil.getCurrentUserPosts()) {
                    is Result.Success ->  it.copy(posts = result.data)
                    is Result.Error -> it.copy(
                        errorMessages = listOf(result.exception.toString()),
                        isLoading = false
                    )
                }
            }
        }
    }

}