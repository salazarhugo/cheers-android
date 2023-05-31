package com.salazar.cheers.friendship.ui.manage_friendship

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.data.internal.Post
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.friendship.domain.usecase.remove_friend.RemoveFriendUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManageFriendshipUiState(
    val userId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val post: Post? = null,
)

@HiltViewModel
class ManageFriendshipViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val removeFriendUseCase: RemoveFriendUseCase,
    private val blockFriendUseCase: RemoveFriendUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ManageFriendshipUiState(isLoading = true))
    private lateinit var friendId: String

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        stateHandle.get<String>("friendId")?.let { friendId ->
            this.friendId = friendId
            viewModelState.update { it.copy(userId = friendId) }
        }
        viewModelScope.launch {
//            userRepository.getU
        }
    }

    fun reportFriend() {
        viewModelScope.launch {
        }
    }

    fun blockFriend() {
        viewModelScope.launch {
        }
    }

    fun removeFriend(onComplete: () -> Unit) {
        viewModelScope.launch {
            removeFriendUseCase(userId = friendId)
            onComplete()
        }
    }
}