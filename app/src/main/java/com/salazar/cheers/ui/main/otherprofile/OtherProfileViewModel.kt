package com.salazar.cheers.ui.main.otherprofile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.salazar.cheers.GetRoomIdReq
import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.data.repository.PostRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface OtherProfileUiState {

    val isLoading: Boolean
    val errorMessages: List<String>

    data class NoUser(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : OtherProfileUiState

    data class HasUser(
        val posts: List<Post>? = null,
        val user: User,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : OtherProfileUiState
}

private data class OtherProfileViewModelState(
    val user: User? = null,
    val posts: List<Post>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val isFollowing: Boolean = false,
) {
    fun toUiState(): OtherProfileUiState =
        if (user != null) {
            OtherProfileUiState.HasUser(
                user = user,
                posts = posts,
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        } else {
            OtherProfileUiState.NoUser(
                isLoading = isLoading,
                errorMessages = errorMessages,
            )
        }
}

@HiltViewModel
class OtherProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val chatRepository: ChatRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(OtherProfileViewModelState(isLoading = true))
    lateinit var username: String

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        savedStateHandle.get<String>("username")?.let { username ->
            this.username = username
        }
        viewModelScope.launch {
            userRepository.getUserFlow(userIdOrUsername = username).collect { user ->
                updateUser(user)
            }
        }
        refreshUser()
        refreshUserPosts()
    }

    private fun refreshUser() {
        viewModelScope.launch {
            userRepository.fetchUser(userIdOrUsername = username)
        }
    }

    fun getRoomId(onSuccess: (String) -> Unit) {
        val otherUserId = viewModelState.value.user?.id ?: return

        viewModelScope.launch {
            onSuccess(
                chatRepository.getRoomId(
                    GetRoomIdReq.newBuilder().setRecipientId(otherUserId).build()
                ).roomId
            )
        }
    }

    fun onSwipeRefresh() {
        refreshUser()
        refreshUserPosts()
    }

    fun toggleFollow(user: User) {
        viewModelScope.launch {
            userRepository.toggleFollow(user = user)
        }
    }

    private fun updateUser(user: User?) {
        viewModelState.update {
            it.copy(user = user)
        }
    }

    fun toggleLike(post: Post) {
        viewModelScope.launch {
            postRepository.toggleLike(post = post)
        }
    }

    private fun updatePosts(posts: List<Post>) {
        viewModelState.update {
            it.copy(posts = posts)
        }
    }

    private fun refreshUserPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.profilePost(userIdOrUsername = username).collect {
                updatePosts(it)
            }
        }
    }
}