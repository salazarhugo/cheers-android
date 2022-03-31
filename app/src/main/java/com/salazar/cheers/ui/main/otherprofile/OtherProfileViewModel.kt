package com.salazar.cheers.ui.main.otherprofile

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import com.salazar.cheers.MainActivity
import com.salazar.cheers.data.db.PostFeed
import com.salazar.cheers.data.repository.PostRepository
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
    val shortLink: String?

    data class NoUser(
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val isFollowing: Boolean,
        override val shortLink: String?,
    ) : OtherProfileUiState

    data class HasUser(
        val postFlow: Flow<PagingData<PostFeed>> = emptyFlow(),
        val user: User,
        override val isLoading: Boolean,
        override val errorMessages: List<String>,
        override val isFollowing: Boolean,
        override val shortLink: String?,
    ) : OtherProfileUiState
}

private data class OtherProfileViewModelState(
    val user: User? = null,
    val postFlow: Flow<PagingData<PostFeed>> = emptyFlow(),
    val isLoading: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val isFollowing: Boolean = false,
    val shortLink: String? = null,
) {
    fun toUiState(): OtherProfileUiState =
        if (user != null) {
            OtherProfileUiState.HasUser(
                user = user,
                postFlow = postFlow,
                isLoading = isLoading,
                errorMessages = errorMessages,
                isFollowing = isFollowing,
                shortLink = shortLink,
            )
        } else {
            OtherProfileUiState.NoUser(
                isLoading = isLoading,
                errorMessages = errorMessages,
                isFollowing = isFollowing,
                shortLink = shortLink,
            )
        }
}

class OtherProfileViewModel @AssistedInject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
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
        getUser(username = username)
        refreshUserPosts(username = username)
    }

    val user = FirestoreUtil.getCurrentUserDocumentLiveData()

    private fun toggleIsFollowed() {
        val isFollowed = viewModelState.value.user?.isFollowed ?: return
        updateIsFollowed(isFollowed = !isFollowed)
    }

    fun toggleLike(post: Post) {
        viewModelScope.launch {
            postRepository.toggleLike(post = post)
        }
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

    private fun getUser(username: String) {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val user = userRepository.getUser(userIdOrUsername = username)
            viewModelState.update {
                it.copy(user = user, isLoading = false)
            }
        }
    }

    private fun refreshUserPosts(username: String) {
        viewModelScope.launch {
            val posts = postRepository.profilePostFeed(userIdOrUsername = username)
            viewModelState.update {
                it.copy(postFlow = posts, isLoading = false)
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