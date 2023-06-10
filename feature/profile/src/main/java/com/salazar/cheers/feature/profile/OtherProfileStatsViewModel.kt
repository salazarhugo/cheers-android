package com.salazar.cheers.feature.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.user.UserRepository
import com.salazar.cheers.domain.list_friend.ListFriendUseCase
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
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
    val followers: List<com.salazar.cheers.core.model.UserItem>? = null,
    val friends: List<com.salazar.cheers.core.model.UserItem>? = null,
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
            val userId = userRepository.getUserFlow(username).first().id
            listFriendUseCase().collect(::updateFriends)
        }
    }

    private fun updateFriends(friends: List<com.salazar.cheers.core.model.UserItem>) {
        viewModelState.update {
            it.copy(friends = friends, isLoading = false)
        }
    }

    fun toggleFollow(userID: String) {
        viewModelScope.launch {
            userRepository.toggleFollow(userID)
        }
    }
}