package com.salazar.cheers.ui.main.otherprofile

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.salazar.cheers.MainActivity
import com.salazar.cheers.backend.Neo4jUtil
import com.salazar.cheers.data.Result
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import com.salazar.cheers.util.FirestoreUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface OtherProfileUiState {

    val isLoading: Boolean
    val errorMessages: List<String>
    val isFollowing: Boolean
    val user: User
    val shortLink: String?

    data class NoPosts(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val isFollowing: Boolean,
        override val user: User,
        override val shortLink: String?,
    ) : OtherProfileUiState

    data class HasPosts(
        val posts: List<Post>,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val isFollowing: Boolean,
        override val user: User,
        override val shortLink: String?,
    ) : OtherProfileUiState
}

private data class OtherProfileViewModelState(
    val user: User? = null,
    val posts: List<Post>? = null,
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val isFollowing: Boolean = false,
    val shortLink: String? = null,
) {
    fun toUiState(): OtherProfileUiState =
        if (posts == null || posts.isEmpty()) {
            OtherProfileUiState.NoPosts(
                user = user ?: User(),
                isLoading = isLoading,
                errorMessages = errorMessages,
                isFollowing = isFollowing,
                shortLink = shortLink,
            )
        } else {
            OtherProfileUiState.HasPosts(
                isLoading = isLoading,
                errorMessages = errorMessages,
                isFollowing = isFollowing,
                posts = posts,
                user = user ?: User(),
                shortLink = shortLink,
            )
        }
}

class OtherProfileViewModel @AssistedInject constructor(
    private val userRepository: UserRepository,
    @Assisted private val username: String
) : ViewModel() {

    private val viewModelState = MutableStateFlow(OtherProfileViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refresh()
    }

    fun refresh() {
        refreshUser(username = username)
        refreshUserPosts(username = username)
    }

    val user = FirestoreUtil.getCurrentUserDocumentLiveData()

    private fun toggleIsFollowed() {
        val isFollowed = viewModelState.value.user?.isFollowed ?: return
        updateIsFollowed(isFollowed = !isFollowed)
    }

    fun followUser() {
        viewModelScope.launch {
            userRepository.followUser(username = username)
        }
        toggleIsFollowed()
    }

    fun unfollowUser() {
        viewModelScope.launch {
            userRepository.unfollowUser(username = username)
        }
        toggleIsFollowed()
    }

    fun updateShortLink(shortLink: String) {
        viewModelState.update {
            it.copy(shortLink = shortLink)
        }
    }

    private fun updateIsFollowed(isFollowed: Boolean) {
        viewModelState.update {
            it.copy(user = it.user?.copy(isFollowed = isFollowed))
        }
    }

    private fun refreshUser(username: String) {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            viewModelState.update {
                when (val result = Neo4jUtil.getUserWithUsername(username)) {
                    is Result.Success -> it.copy(user = result.data, isLoading = false)
                    is Result.Error -> it.copy(
                        isLoading = false,
                        errorMessages = listOf(result.exception.toString())
                    )
                }
            }
        }
    }

    private fun refreshUserPosts(username: String) {
        viewModelScope.launch {
            viewModelState.update {
                when (val result = Neo4jUtil.getUserPosts(username = username)) {
                    is Result.Success -> it.copy(posts = result.data, isLoading = false)
                    is Result.Error -> it.copy(
                        errorMessages = listOf(result.exception.toString()),
                        isLoading = false
                    )
                }
            }
        }
    }

    @AssistedFactory
    interface OtherProfileViewModelFactory {
        fun create(username: String): OtherProfileViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: OtherProfileViewModelFactory,
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
fun otherProfileViewModel(username: String): OtherProfileViewModel {
    val factory = EntryPointAccessors.fromActivity(
        LocalContext.current as Activity,
        MainActivity.ViewModelFactoryProvider::class.java
    ).otherProfileViewModelFactory()

    return viewModel(factory = OtherProfileViewModel.provideFactory(factory, username = username))
}