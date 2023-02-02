package com.salazar.cheers.ui.main.otherprofile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.db.entities.UserItem
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.domain.usecase.list_friend.ListFriendUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OtherProfileStatsUiState(
    val isLoading: Boolean = true,
    val isFollowers: Boolean = true,
    val errorMessages: List<String> = emptyList(),
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
            val userId = userRepository.getUserFlow(username).first().id
            listFriendUseCase(userId).collect(::updateFriends)
        }
    }

    private fun updateFriends(friends: List<UserItem>) {
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