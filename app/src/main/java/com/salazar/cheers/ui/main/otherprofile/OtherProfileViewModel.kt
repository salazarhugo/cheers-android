package com.salazar.cheers.ui.main.otherprofile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cheers.chat.v1.GetRoomIdReq
import com.salazar.cheers.data.repository.ChatRepository
import com.salazar.cheers.data.repository.PostRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.data.repository.friendship.FriendshipRepository
import com.salazar.cheers.domain.usecase.accept_friend_request.AcceptFriendRequestUseCase
import com.salazar.cheers.domain.usecase.cancel_friend_request.CancelFriendRequestUseCase
import com.salazar.cheers.domain.usecase.remove_friend.RemoveFriendUseCase
import com.salazar.cheers.domain.usecase.send_friend_request.SendFriendRequestUseCase
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
    private val sendFriendRequestUseCase: SendFriendRequestUseCase,
    private val cancelFriendRequestUseCase: CancelFriendRequestUseCase,
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase,
    private val removeFriendUseCase: RemoveFriendUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(OtherProfileViewModelState(isLoading = false))
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
            userRepository.fetchUser(username)
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

    fun acceptFriendRequest(userId: String) {
        viewModelScope.launch {
            acceptFriendRequestUseCase(userId = userId)
        }
    }

    fun removeFriend(userId: String) {
        viewModelScope.launch {
            removeFriendUseCase(userId = userId)
        }
    }

    fun cancelFriendRequest(userId: String) {
        viewModelScope.launch {
            cancelFriendRequestUseCase(userId = userId)
        }
    }

    fun sendFriendRequest(userId: String) {
        viewModelScope.launch {
            sendFriendRequestUseCase(userId = userId)
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
            postRepository.listPost(userIdOrUsername = username).collect {
                updatePosts(it)
            }
        }
    }
}