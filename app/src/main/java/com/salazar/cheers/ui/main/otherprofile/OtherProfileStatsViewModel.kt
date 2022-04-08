package com.salazar.cheers.ui.main.otherprofile

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.salazar.cheers.MainActivity
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.User
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface OtherProfileStatsUiState {

    val isLoading: Boolean
    val isFollowers: Boolean
    val errorMessages: List<String>
    val searchInput: String

    data class NoUsers(
        override val isLoading: Boolean,
        override val isFollowers: Boolean,
        override val errorMessages: List<String>,
        override val searchInput: String
    ) : OtherProfileStatsUiState

    data class HasFollowers(
        val followers: List<User>,
        val following: List<User>,
        override val isLoading: Boolean,
        override val isFollowers: Boolean,
        override val errorMessages: List<String>,
        override val searchInput: String
    ) : OtherProfileStatsUiState
}

private data class OtherProfileStatsViewModelState constructor(
    val followers: List<User>? = null,
    val following: List<User>? = null,
    val isLoading: Boolean = false,
    val isFollowers: Boolean = true,
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
) {
    fun toUiState(): OtherProfileStatsUiState =
        if (followers != null && following != null) {
            OtherProfileStatsUiState.HasFollowers(
                followers = followers,
                following = following,
                isLoading = isLoading,
                isFollowers = isFollowers,
                errorMessages = errorMessages,
                searchInput = searchInput
            )
        } else {
            OtherProfileStatsUiState.NoUsers(
                isLoading = isLoading,
                errorMessages = errorMessages,
                isFollowers = isFollowers,
                searchInput = searchInput
            )
        }
}

class OtherProfileStatsViewModel @AssistedInject constructor(
    @Assisted private val username: String,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val viewModelState =
        MutableStateFlow(OtherProfileStatsViewModelState(isLoading = true))

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
                val result = userRepository.getFollowersFollowing(userIdOrUsername = username)
                it.copy(followers = result.first, following = result.second, isLoading = false)
            }
        }
    }

    fun toggleFollow(user: User) {
        viewModelScope.launch {
            userRepository.toggleFollow(user = user)
        }
    }

    @AssistedFactory
    interface OtherProfileStatsViewModelFactory {
        fun create(username: String): OtherProfileStatsViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: OtherProfileStatsViewModelFactory,
            username: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(username = username) as T
            }
        }
    }
}

@Composable
fun otherProfileStatsViewModel(username: String): OtherProfileStatsViewModel {
    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        MainActivity.ViewModelFactoryProvider::class.java
    ).otherProfileStatsViewModelFactory()

    return viewModel(
        factory = OtherProfileStatsViewModel.provideFactory(
            factory,
            username = username
        )
    )
}
