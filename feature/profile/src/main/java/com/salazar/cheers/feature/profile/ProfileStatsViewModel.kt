package com.salazar.cheers.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.user.UserRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileStatsUiState(
    val isLoadingFollowers: Boolean = true,
    val isLoadingFollowing: Boolean = true,
    val isFollowers: Boolean = true,
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
    val followers: List<com.salazar.cheers.core.model.UserItem>? = null,
    val following: List<com.salazar.cheers.core.model.UserItem>? = null,
)

@HiltViewModel
class ProfileStatsViewModel @Inject constructor(
    private val userRepositoryImpl: UserRepositoryImpl,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ProfileStatsUiState())

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        refreshFollowers()
        refreshFollowing()
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
            val following =
                userRepositoryImpl.getFollowing("")
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
            val followers =
                userRepositoryImpl.getFollowers("")
            viewModelState.update {
                it.copy(followers = followers, isLoadingFollowers = false)
            }
        }
    }

    fun toggleFollow(userID: String) {
    }
}