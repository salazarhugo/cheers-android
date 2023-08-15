package com.salazar.cheers.feature.profile

import androidx.compose.material3.SheetState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cheers.party.v1.PartyServiceGrpcKt
import com.salazar.cheers.data.party.Party
import com.salazar.cheers.data.party.data.repository.PartyRepository
import com.salazar.cheers.data.user.User
import com.salazar.cheers.data.user.UserRepository
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
    val errorMessages: String
    val sheetState: SheetState

    data class Loading(
        override val isLoading: Boolean,
        override val errorMessages: String,
        override val sheetState: SheetState,
    ) : ProfileUiState

    data class HasUser(
        val user: User,
        val posts: List<com.salazar.cheers.data.post.repository.Post>?,
        val parties: List<Party>?,
        override val sheetState: SheetState,
        override val isLoading: Boolean,
        override val errorMessages: String,
    ) : ProfileUiState
}

private data class ProfileViewModelState(
    val user: User? = null,
    val posts: List<com.salazar.cheers.data.post.repository.Post>? = null,
    val parties: List<Party>? = null,
    val isLoading: Boolean = false,
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
            )
        else
            ProfileUiState.Loading(
                isLoading = isLoading,
                errorMessages = errorMessages,
                sheetState = sheetState,
            )
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: com.salazar.cheers.data.post.repository.PostRepository,
    private val partyRepository: PartyRepository,
    private val partyStub: PartyServiceGrpcKt.PartyServiceCoroutineStub,
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
        refreshUser()
        refreshUserPosts()
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

        viewModelScope.launch {
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
        viewModelScope.launch(Dispatchers.IO) {
            postRepository.listPost("").collect {
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