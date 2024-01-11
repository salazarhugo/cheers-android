package com.salazar.cheers.feature.friend_request

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.friendship.FriendshipRepository
import com.salazar.cheers.domain.list_friend_request.ListFriendRequestUseCase
import com.salazar.cheers.domain.cancel_friend_request.CancelFriendRequestUseCase
import com.salazar.cheers.domain.list_suggestions.ListSuggestionsUseCase
import com.salazar.cheers.domain.send_friend_request.SendFriendRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FriendRequestsUiState(
    val isLoading: Boolean,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val friendRequests: List<com.salazar.cheers.core.model.UserItem>? = null,
    val suggestions: List<com.salazar.cheers.core.model.UserItem>? = null,
)

@HiltViewModel
class FriendRequestsViewModel @Inject constructor(
    private val friendshipRepository: FriendshipRepository,
    private val listFriendRequestUseCase: ListFriendRequestUseCase,
    private val listSuggestionsUseCase: ListSuggestionsUseCase,
    private val sendFriendRequestUseCase: SendFriendRequestUseCase,
    private val cancelFriendRequestUseCase: CancelFriendRequestUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(FriendRequestsUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        viewModelScope.launch {
            listFriendRequestUseCase().collect(::updateFriendRequests)
        }
        refreshSuggestions()
    }

    fun onSwipeToRefresh() {
        refreshSuggestions()
        viewModelScope.launch {
//            friendshipRepository.fetchFriendRequest().collect {
//                when(it) {
//                    is Resource.Error -> updateErrorMessage(it.message)
//                    is Resource.Loading -> updateIsRefreshing(it.isLoading)
//                    is Resource.Success -> updateFriendRequests(users = it.data)
//                }
//            }
        }
    }

    fun onAcceptFriendRequest(username: String) {
        viewModelScope.launch {
            friendshipRepository.acceptFriendRequest(userId = username)
        }
        viewModelState.update {
            it.copy(friendRequests = it.friendRequests?.filter { it.id != username })
        }
    }

    fun onRefuseFriendRequest(username: String) {
        viewModelScope.launch {
            friendshipRepository.cancelFriendRequest(userId = username)
        }
    }

    private fun updateErrorMessage(message: String?) {
        viewModelState.update {
            it.copy(errorMessage = message)
        }
    }

    private fun updateIsRefreshing(isRefreshing: Boolean) {
        viewModelState.update {
            it.copy(isRefreshing = isRefreshing)
        }
    }

    private fun updateIsLoading(isLoading: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isLoading)
        }
    }

    private fun updateFriendRequests(users: List<com.salazar.cheers.core.model.UserItem>?) {
        viewModelState.update {
            it.copy(friendRequests = users, isLoading = false)
        }
    }

    private fun refreshSuggestions() {
        viewModelScope.launch {
            listSuggestionsUseCase().onSuccess {
                updateSuggestions(it)
            }
        }
    }

    private fun updateSuggestions(suggestions: List<com.salazar.cheers.core.model.UserItem>) {
        viewModelState.update {
            it.copy(suggestions = suggestions)
        }
    }

    fun onRemoveSuggestion(user: com.salazar.cheers.core.model.UserItem) {
        viewModelState.update {
            val l = it.suggestions?.toMutableList()
            l?.remove(user)
            it.copy(suggestions = l)
        }
    }

    fun onCancelFriendRequestClick(userID: String) {
        viewModelScope.launch {
            cancelFriendRequestUseCase(userId = userID).onSuccess {
            }
        }
    }

    fun onAddFriendClick(userID: String) {
        viewModelScope.launch {
            sendFriendRequestUseCase(userId = userID).onSuccess {
            }
        }
    }
}

sealed class FriendRequestsUIAction {
    object OnBackPressed : FriendRequestsUIAction()
    object OnSwipeRefresh : FriendRequestsUIAction()
    data class OnAcceptFriendRequest(val userID: String) : FriendRequestsUIAction()
    data class OnRefuseFriendRequest(val userID: String) : FriendRequestsUIAction()
    data class OnUserClick(val userId: String) : FriendRequestsUIAction()
    data class OnCancelFriendRequestClick(val userID: String) : FriendRequestsUIAction()
    data class OnAddFriendClick(val userID: String) : FriendRequestsUIAction()
    data class OnRemoveSuggestion(val user: com.salazar.cheers.core.model.UserItem) :
        FriendRequestsUIAction()
}
