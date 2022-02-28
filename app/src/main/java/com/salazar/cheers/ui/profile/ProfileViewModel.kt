package com.salazar.cheers.ui.profile

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.data.PostRepository
import com.salazar.cheers.data.UserRepository
import com.salazar.cheers.data.db.PostFeed
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

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
        val posts: List<PostFeed>,
        override val sheetState: ModalBottomSheetState,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : ProfileUiState
}

private data class ProfileViewModelState @ExperimentalMaterialApi constructor(
    val user: User? = null,
    val posts: List<PostFeed> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val sheetState: ModalBottomSheetState = ModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
) {
    fun toUiState(): ProfileUiState =
        if (user != null)
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

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ProfileViewModelState(isLoading = false))

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
        val posts = viewModelState.value.posts
        val mPosts = posts.toMutableList()
//        val post = mPosts.find { it.id == post.id } ?: return
//
//        val updatedPost = if (post.liked)
//            post.copy(likes = post.likes - 1, liked = false)
//        else
//            post.copy(likes = post.likes + 1, liked = true)
//
//        mPosts[mPosts.indexOf(post)] = updatedPost
//
//        toggleLike(postId = post.id, like = updatedPost.liked)
//        updatePosts(mPosts)
    }

    private fun toggleLike(
        postId: String,
        like: Boolean
    ) {
        viewModelScope.launch {
            if (like)
                Neo4jUtil.likePost(postId = postId)
            else
                Neo4jUtil.unlikePost(postId = postId)
        }
    }

    private fun refreshUser() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val user = userRepository.getUser(FirebaseAuth.getInstance().currentUser?.uid!!)
            viewModelState.update {
                it.copy(user = user, isLoading = false)
            }
        }
    }

    private fun refreshUserPosts() {
        viewModelScope.launch {
            val posts = postRepository.getPostsWithAuthorId(FirebaseAuth.getInstance().currentUser?.uid!!)
            viewModelState.update {
                it.copy(posts = posts, isLoading = false)
            }
        }
    }

}