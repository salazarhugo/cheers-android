package com.salazar.cheers.feature.profile.other_profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.UserItem
import com.salazar.cheers.data.user.UserRepository
import com.salazar.cheers.domain.list_friend.ListFriendUseCase
import com.salazar.common.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
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
)


@HiltViewModel
class OtherProfileStatsViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val listFriendUseCase: ListFriendUseCase,
) : ViewModel() {

    private val viewModelState =
        MutableStateFlow(OtherProfileStatsUiState())

    private lateinit var username: String

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<Boolean>("verified")?.let { verified ->
            viewModelState.update { it.copy(verified = verified) }
        }
        stateHandle.get<String>("username")?.let { username ->
            viewModelState.update { it.copy(username = username) }
            this.username = username
        }
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
            listFriendUseCase().collect { result ->
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
        viewModelScope.launch {
            userRepository.toggleFollow(userID)
        }
    }
}