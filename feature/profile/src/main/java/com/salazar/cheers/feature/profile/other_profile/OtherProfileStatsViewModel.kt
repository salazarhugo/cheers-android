package com.salazar.cheers.feature.profile.other_profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.domain.list_friend.ListFriendsUseCase
import com.salazar.cheers.shared.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OtherProfileStatsUiState(
    val isLoading: Boolean = true,
    val isFollowers: Boolean = true,
    val errorMessage: String? = null,
    val searchInput: String = "",
    val followers: List<UserItem>? = null,
    val friends: List<UserItem>? = null,
    val username: String = "",
    val verified: Boolean = false,
    val premium: Boolean = false,
)


@HiltViewModel
class OtherProfileStatsViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val listFriendsUseCase: ListFriendsUseCase,
) : ViewModel() {

    private val args = stateHandle.toRoute<OtherProfileStatsScreen>()

    private val viewModelState =
        MutableStateFlow(
            OtherProfileStatsUiState(
                verified = args.verified,
                username = args.username,
                premium = args.premium,
            )
        )


    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        onSwipeRefresh()
    }

    fun onSwipeRefresh() {
        refreshFriends()
    }

    private fun refreshFriends() {
        viewModelState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            listFriendsUseCase(otherUserID = args.otherUserID).collect { result ->
                when (result) {
                    is Resource.Loading -> updateIsLoading(result.isLoading)
                    is Resource.Error -> updateMessage(result.message)
                    is Resource.Success -> updateFriends(result.data)
                }
            }
        }
    }

    private fun updateMessage(errorMessage: String?) {
        viewModelState.update {
            it.copy(errorMessage = errorMessage)
        }
    }

    private fun updateIsLoading(isRefreshing: Boolean) {
        viewModelState.update {
            it.copy(isLoading = isRefreshing)
        }
    }

    private fun updateFriends(friends: List<UserItem>?) {
        viewModelState.update {
            it.copy(friends = friends)
        }
    }

    fun toggleFollow(userID: String) {
    }
}