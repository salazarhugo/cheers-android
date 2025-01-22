package com.salazar.cheers.feature.profile.other_profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.salazar.cheers.core.Post
import com.salazar.cheers.core.model.Party
import com.salazar.cheers.core.model.User
import com.salazar.cheers.data.party.data.repository.PartyRepository
import com.salazar.cheers.data.post.repository.PostRepository
import com.salazar.cheers.data.user.UserRepositoryImpl
import com.salazar.cheers.domain.accept_friend_request.AcceptFriendRequestUseCase
import com.salazar.cheers.domain.cancel_friend_request.CancelFriendRequestUseCase
import com.salazar.cheers.domain.list_post.ListPostFlowUseCase
import com.salazar.cheers.domain.list_post.ListPostUseCase
import com.salazar.cheers.domain.send_friend_request.SendFriendRequestUseCase
import com.salazar.cheers.feature.profile.navigation.OtherProfileScreen
import com.salazar.cheers.shared.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private data class OtherProfileViewModelState(
    val username: String = String(),
    val user: User? = null,
    val posts: List<Post>? = null,
    val parties: List<Party>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val isFollowing: Boolean = false,
    val isRefreshing: Boolean = false,
) {
    fun toUiState(): OtherProfileUiState =
        if (user != null) {
            OtherProfileUiState.HasUser(
                user = user,
                posts = posts,
                parties = parties,
                isLoading = isLoading,
                errorMessages = errorMessages,
                isRefreshing = isRefreshing,
                username = user.username,
            )
        } else if (isLoading) {
            OtherProfileUiState.Loading(
                username = username,
                isRefreshing = isRefreshing,
            )
        } else {
            OtherProfileUiState.NotFound(
                username = username,
                isRefreshing = isRefreshing,
            )
        }
}

@HiltViewModel
class OtherProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepositoryImpl: UserRepositoryImpl,
    private val postRepository: PostRepository,
    private val partyRepository: PartyRepository,
    private val sendFriendRequestUseCase: SendFriendRequestUseCase,
    private val cancelFriendRequestUseCase: CancelFriendRequestUseCase,
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase,
    private val listPostUseCase: ListPostUseCase,
    private val listPostFlowUseCase: ListPostFlowUseCase,
) : ViewModel() {
    val args = savedStateHandle.toRoute<OtherProfileScreen>()
    val username = args.username

    private val viewModelState = MutableStateFlow(
        OtherProfileViewModelState(
            isLoading = true,
            username = username,
        )
    )

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        viewModelScope.launch {
            userRepositoryImpl.getUserFlow(userIdOrUsername = username).collect { user ->
                updateUser(user)
                refreshUserPosts()
                refreshUserParties()
            }
        }
        onSwipeRefresh()
        viewModelScope.launch {
            listPostFlowUseCase(username)
                .collect(::updatePosts)
        }
    }

    private fun refreshUser() {
        viewModelScope.launch {
            updateIsRefreshing(true)
            val result = userRepositoryImpl.fetchUser(username)
            when (result) {
                is Resource.Error -> {}
                is Resource.Loading -> updateIsRefreshing(result.isLoading)
                is Resource.Success -> {}
            }
            updateIsRefreshing(false)
        }
    }

    fun onSwipeRefresh() {
        refreshUser()
    }

    private fun updateUsername(username: String) {
        viewModelState.update {
            it.copy(username = username)
        }
    }

    private fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

    private fun updateIsRefreshing(isRefreshing: Boolean) {
        viewModelState.update {
            it.copy(isRefreshing = isRefreshing)
        }
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

    fun sendFriendRequest(userID: String) {
        viewModelScope.launch {
            sendFriendRequestUseCase(userId = userID)
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

    fun onLoadMorePost(lastLoadedIndex: Int) {
        val nextItemIndex = lastLoadedIndex + 1
        val nextPage = nextItemIndex / 10 + 1

        refreshUserPosts(page = nextPage)
    }

    private fun refreshUserPosts(page: Int = 1) {
        val otherUserId = viewModelState.value.user?.id ?: return
        viewModelScope.launch {
            listPostUseCase(
                page = page,
                userId = otherUserId,
            )
        }
    }

    private fun refreshUserParties() {
        val otherUserId = viewModelState.value.user?.id ?: return
        viewModelScope.launch() {
            partyRepository.listPartyFlow(
                filter = "other",
                userId = otherUserId,
            ).collect(::updateParties)
        }
    }

    fun onLikeClick(post: Post) {
        viewModelScope.launch {
            postRepository.toggleLike(post = post)
        }
    }
}