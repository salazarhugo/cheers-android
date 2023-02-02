package com.salazar.cheers.ui.sheets.manage_friendship

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.domain.usecase.remove_friend.RemoveFriendUseCase
import com.salazar.cheers.internal.Post
import com.salazar.cheers.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManageFriendshipUiState(
    val user: User? = null,
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
        stateHandle.get<String>("friendId")?.let {
            friendId = it
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

    fun removeFriend() {
        viewModelScope.launch {
            val result = removeFriendUseCase(userId = friendId)
        }
    }
}