package com.salazar.cheers.ui.main.otherprofile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.db.entities.UserItem
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OtherProfileStatsUiState(
    val isLoadingFollowers: Boolean = true,
    val isLoadingFollowing: Boolean = true,
    val isFollowers: Boolean = true,
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
    val followers: List<UserItem>? = null,
    val following: List<UserItem>? = null,
    val username: String = "",
    val verified: Boolean = false,
)


@HiltViewModel
class OtherProfileStatsViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
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
        refreshFollowers()
        refreshFollowing()
    }

    private fun refreshFollowing() {
        viewModelState.update {
            it.copy(isLoadingFollowing = true)
        }

        viewModelScope.launch {
            val following = userRepository.getFollowing(username)
            viewModelState.update {
                it.copy(following = following, isLoadingFollowing = false)
            }
        }
    }

    private fun refreshFollowers() {
        viewModelState.update {
            it.copy(isLoadingFollowers = true)
        }

        viewModelScope.launch {
            val followers = userRepository.getFollowers(username)
            viewModelState.update {
                it.copy(followers = followers, isLoadingFollowers = false)
            }
        }
    }

    fun toggleFollow(userID: String) {
        viewModelScope.launch {
            userRepository.toggleFollow(userID)
        }
    }
}