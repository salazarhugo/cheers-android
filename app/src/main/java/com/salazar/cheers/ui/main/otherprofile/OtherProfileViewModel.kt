package com.salazar.cheers.ui.main.otherprofile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cheers.chat.v1.GetRoomIdReq
import com.salazar.cheers.chat.data.repository.ChatRepository
import com.salazar.cheers.post.data.repository.PostRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.friendship.domain.usecase.accept_friend_request.AcceptFriendRequestUseCase
import com.salazar.cheers.friendship.domain.usecase.cancel_friend_request.CancelFriendRequestUseCase
import com.salazar.cheers.friendship.domain.usecase.send_friend_request.SendFriendRequestUseCase
import com.salazar.cheers.core.data.internal.Party
import com.salazar.cheers.core.data.internal.Post
import com.salazar.cheers.core.data.internal.User
import com.salazar.cheers.parties.data.repository.PartyRepository
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
        val parties: List<Party>? = null,
        val user: User,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
    ) : OtherProfileUiState
}

private data class OtherProfileViewModelState(
    val user: User? = null,
    val posts: List<Post>? = null,
    val parties: List<Party>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val isFollowing: Boolean = false,
) {
    fun toUiState(): OtherProfileUiState =
        if (user != null) {
            OtherProfileUiState.HasUser(
                user = user,
                posts = posts,
                parties = parties,
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
    private val partyRepository: PartyRepository,
    private val sendFriendRequestUseCase: SendFriendRequestUseCase,
    private val cancelFriendRequestUseCase: CancelFriendRequestUseCase,
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase,
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
                refreshUserParties()
            }
        }
        onSwipeRefresh()
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
        refreshUserParties()
    }

    fun acceptFriendRequest(userId: String) {
        viewModelScope.launch {
            acceptFriendRequestUseCase(userId = userId)
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

    private fun updateParties(parties: List<Party>) {
        viewModelState.update {
            it.copy(parties = parties)
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

    private fun refreshUserParties() {
        val otherUserId = viewModelState.value.user?.id ?: return
        viewModelScope.launch(Dispatchers.IO) {
            partyRepository.listParty(userId = otherUserId)
                .collect(::updateParties)
        }
    }
}