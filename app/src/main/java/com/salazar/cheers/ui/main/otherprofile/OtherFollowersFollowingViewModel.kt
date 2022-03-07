package com.salazar.cheers.ui.main.otherprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.data.Result
import com.salazar.cheers.internal.User
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface FollowersFollowingUiState {

    val isLoading: Boolean
    val isFollowers: Boolean
    val errorMessages: List<String>
    val searchInput: String

    data class NoUsers(
        override val isLoading: Boolean,
        override val isFollowers: Boolean,
        override val errorMessages: List<String>,
        override val searchInput: String
    ) : FollowersFollowingUiState

    data class HasFollowers(
        val followers: List<User>,
        val following: List<User>,
        override val isLoading: Boolean,
        override val isFollowers: Boolean,
        override val errorMessages: List<String>,
        override val searchInput: String
    ) : FollowersFollowingUiState
}

private data class FollowersFollowingViewModelState @OptIn(ExperimentalPagerApi::class) constructor(
    val followers: List<User>? = null,
    val following: List<User>? = null,
    val isLoading: Boolean = false,
    val isFollowers: Boolean = true,
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
) {
    fun toUiState(): FollowersFollowingUiState =
        if (followers != null && following != null) {
            FollowersFollowingUiState.HasFollowers(
                followers = followers,
                following = following,
                isLoading = isLoading,
                isFollowers = isFollowers,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        } else {
            FollowersFollowingUiState.NoUsers(
                isLoading = isLoading,
                errorMessages = errorMessages,
                isFollowers = isFollowers,
                searchInput = searchInput
            )
        }
}

class OtherFollowersFollowingViewModel @AssistedInject constructor(
    @Assisted private val username: String
) : ViewModel() {

    private val viewModelState =
        MutableStateFlow(FollowersFollowingViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refreshFollowers()
        refreshFollowing()
    }

    private fun refreshFollowers() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            viewModelState.update {
                when (val result = Neo4jUtil.getFollowers(username = username)) {
                    is Result.Success -> it.copy(followers = result.data, isLoading = false)
                    is Result.Error -> it.copy(
                        isLoading = false,
                        errorMessages = listOf(result.exception.toString())
                    )
                }
            }
        }
    }

    fun toggle() {
        viewModelState.update { it.copy(isFollowers = !it.isFollowers) }
    }

    private fun refreshFollowing() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            viewModelState.update {
                val result = Neo4jUtil.getFollowing(username = username)
                when (result) {
                    is Result.Success -> it.copy(following = result.data, isLoading = false)
                    is Result.Error -> it.copy(
                        isLoading = false,
                        errorMessages = listOf(result.exception.toString())
                    )
                }
            }
        }
    }

    fun unfollow(userId: String) {
        viewModelScope.launch {
            Neo4jUtil.unfollowUser(userId)
        }
    }

    @AssistedFactory
    interface OtherFollowersFollowingViewModelFactory {
        fun create(username: String): OtherFollowersFollowingViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: OtherFollowersFollowingViewModelFactory,
            username: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(username = username) as T
            }
        }
    }
}