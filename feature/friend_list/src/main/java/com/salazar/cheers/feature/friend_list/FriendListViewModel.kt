package com.salazar.cheers.feature.friend_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.domain.list_friend.ListMyFriendsUseCase
import com.salazar.cheers.domain.remove_friend.RemoveFriendUseCase
import com.salazar.cheers.shared.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FriendListUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val friends: List<UserItem>? = null,
)

@HiltViewModel
class FriendListViewModel @Inject constructor(
    private val listFriendUseCase: ListMyFriendsUseCase,
    private val removeFriendUseCase: RemoveFriendUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(FriendListUiState(isLoading = true))

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        onSwipeRefresh()
    }

    private fun getFriendList() {
        viewModelScope.launch {
            listFriendUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> updateIsLoading(result.isLoading)
                    is Resource.Error -> updateMessage(result.message)
                    is Resource.Success -> updateFriendList(result.data)
                }
            }
        }
    }


    private fun updateFriendList(friends: List<UserItem>?) {
        viewModelState.update {
            it.copy(friends = friends)
        }
    }

    private fun updateMessage(errorMessage: String?) {
        viewModelState.update {
            it.copy(errorMessage = errorMessage)
        }
    }

    private fun updateIsLoading(isRefreshing: Boolean) {
        viewModelState.update {
            it.copy(isRefreshing = isRefreshing)
        }
    }

    fun onSwipeRefresh() {
        getFriendList()
    }

    fun onRemoveFriend(userID: String) {
        viewModelScope.launch {
            removeFriendUseCase(userID)
                .onSuccess {
                    onSwipeRefresh()
                }
                .onFailure {
                    updateMessage("Failed to remove friend")
                }
        }
    }
}

sealed class FriendListUIAction {
    object OnBackPressed : FriendListUIAction()
    object OnSwipeRefresh : FriendListUIAction()
    data class OnUserClick(val userId: String) : FriendListUIAction()
    data class OnRemoveFriendClick(val userId: String) : FriendListUIAction()
}
