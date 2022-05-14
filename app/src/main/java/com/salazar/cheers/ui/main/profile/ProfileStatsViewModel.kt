package com.salazar.cheers.ui.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfileStatsUiState {

    val isLoading: Boolean
    val isFollowers: Boolean
    val errorMessages: List<String>
    val searchInput: String

    data class NoUsers(
        override val isLoading: Boolean,
        override val isFollowers: Boolean,
        override val errorMessages: List<String>,
        override val searchInput: String
    ) : ProfileStatsUiState

    data class HasFollowers(
        val followers: List<User>,
        val following: List<User>,
        override val isLoading: Boolean,
        override val isFollowers: Boolean,
        override val errorMessages: List<String>,
        override val searchInput: String
    ) : ProfileStatsUiState
}

private data class ProfileStatsViewModelState @OptIn(ExperimentalPagerApi::class) constructor(
    val followers: List<User>? = null,
    val following: List<User>? = null,
    val isLoading: Boolean = false,
    val isFollowers: Boolean = true,
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
) {
    fun toUiState(): ProfileStatsUiState =
        if (followers != null && following != null) {
            ProfileStatsUiState.HasFollowers(
                followers = followers,
                following = following,
                isLoading = isLoading,
                isFollowers = isFollowers,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        } else {
            ProfileStatsUiState.NoUsers(
                isLoading = isLoading,
                errorMessages = errorMessages,
                isFollowers = isFollowers,
                searchInput = searchInput
            )
        }
}

@HiltViewModel
class ProfileStatsViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val viewModelState =
        MutableStateFlow(ProfileStatsViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refreshFollowersFollowing()
    }

    fun onSwipeRefresh() {
        refreshFollowersFollowing()
    }

    private fun refreshFollowersFollowing() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            viewModelState.update {
                val result =
                    userRepository.getFollowersFollowing(FirebaseAuth.getInstance().currentUser?.uid!!)
                it.copy(followers = result.first, following = result.second, isLoading = false)
            }
        }
    }

    fun toggleFollow(user: User) {
        viewModelScope.launch {
            userRepository.toggleFollow(user = user)
        }
    }
}