package com.salazar.cheers.ui.main.friendrequests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.Resource
import com.salazar.cheers.data.db.entities.UserItem
import com.salazar.cheers.data.repository.friendship.FriendshipRepository
import com.salazar.cheers.internal.Activity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class FriendRequestsUiState(
    val isLoading: Boolean,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val friendRequests: List<UserItem>? = null,
)

@HiltViewModel
class FriendRequestsViewModel @Inject constructor(
    private val friendshipRepository: FriendshipRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(FriendRequestsUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        onSwipeToRefresh()
    }

    fun onSwipeToRefresh() {
        viewModelScope.launch {
            friendshipRepository.listFriendRequest().collect {
                when(it) {
                    is Resource.Error -> updateErrorMessage(it.message)
                    is Resource.Loading -> updateIsRefreshing(it.isLoading)
                    is Resource.Success -> updateFriendRequests(users = it.data)
                }
            }
        }
    }

    fun onAcceptFriendRequest(userId: String) {
        viewModelScope.launch {
            friendshipRepository.acceptFriendRequest(userId = userId)
        }
        viewModelState.update {
            it.copy(friendRequests = it.friendRequests?.filter { it.id != userId })
        }
    }

    fun onRefuseFriendRequest(userId: String) {
        viewModelScope.launch {
//            friendshipRepository.cancelFriendRequest()
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

    private fun updateFriendRequests(users: List<UserItem>?) {
        viewModelState.update {
            it.copy(friendRequests = users, isLoading = false)
        }
    }
}

sealed class FriendRequestsUIAction {
    object OnBackPressed : FriendRequestsUIAction()
    object OnSwipeRefresh : FriendRequestsUIAction()
    data class OnAcceptFriendRequest(val userId: String) : FriendRequestsUIAction()
}
