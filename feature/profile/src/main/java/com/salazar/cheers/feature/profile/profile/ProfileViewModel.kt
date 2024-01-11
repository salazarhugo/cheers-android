package com.salazar.cheers.feature.profile.profile

import androidx.compose.material3.SheetState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.party.Party
import com.salazar.cheers.data.post.repository.PostRepository
import com.salazar.cheers.data.user.User
import com.salazar.cheers.data.user.UserRepository
import com.salazar.cheers.domain.list_post.ListPostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProfileUiState {

    val isLoading: Boolean
    val isRefreshing: Boolean
    val errorMessages: String
    val sheetState: SheetState

    data class NoAccount(
        override val isLoading: Boolean,
        override val errorMessages: String,
        override val sheetState: SheetState,
        override val isRefreshing: Boolean,
    ) : ProfileUiState

    data class HasUser(
        val user: User,
        val posts: List<com.salazar.cheers.data.post.repository.Post>?,
        val parties: List<Party>?,
        override val sheetState: SheetState,
        override val isLoading: Boolean,
        override val errorMessages: String,
        override val isRefreshing: Boolean,
    ) : ProfileUiState
}

private data class ProfileViewModelState(
    val user: User? = null,
    val posts: List<com.salazar.cheers.data.post.repository.Post>? = null,
    val parties: List<Party>? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessages: String = "",
    val sheetState: SheetState = SheetState(true),
) {
    fun toUiState(): ProfileUiState =
        if (user != null)
            ProfileUiState.HasUser(
                posts = posts,
                user = user,
                parties = parties,
                isLoading = isLoading,
                errorMessages = errorMessages,
                sheetState = sheetState,
                isRefreshing = isRefreshing,
            )
        else
            ProfileUiState.NoAccount(
                isLoading = isLoading,
                errorMessages = errorMessages,
                sheetState = sheetState,
                isRefreshing = isRefreshing,
            )
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val listPostUseCase: ListPostUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ProfileViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        viewModelScope.launch {
            userRepository.getCurrentUserFlow().collect { user ->
                updateUser(user)
            }
        }
        refreshUser()
        refreshUserPosts()
    }

    fun onSwipeRefresh() {
        viewModelScope.launch {
            updateIsRefreshing(true)
            refreshUser()
            refreshUserPosts()
            updateIsRefreshing(false)
        }
    }

    private fun updateIsRefreshing(isRefreshing: Boolean) {
        viewModelState.update {
            it.copy(isRefreshing = isRefreshing)
        }
    }

    private fun updateUser(user: User) {
        viewModelState.update {
            it.copy(user = user, isLoading = false)
        }
    }

    fun toggleLike(
        post: com.salazar.cheers.data.post.repository.Post,
    ) {
        viewModelScope.launch {
            postRepository.toggleLike(post = post)
        }
    }

    private fun refreshUser() {
        viewModelState.update { it.copy(isLoading = true) }

        viewModelScope.launch(Dispatchers.IO) {
            val result = userRepository.fetchCurrentUser()
            viewModelState.update { it.copy(isLoading = false) }
        }
    }

    private fun updatePosts(posts: List<com.salazar.cheers.data.post.repository.Post>) {
        viewModelState.update {
            it.copy(posts = posts)
        }
    }

    private fun refreshUserPosts() {
        viewModelScope.launch {
            listPostUseCase().collect {
                updatePosts(it)
            }
        }
    }

    private fun updateError(message: String?) {
        if (message == null)
            return
        viewModelState.update {
            it.copy(errorMessages = message)
        }
    }
}