package com.salazar.cheers.ui.main.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cheers.chat.v1.Room
import com.salazar.cheers.chat.data.repository.ChatRepository
import com.salazar.cheers.data.repository.UserRepository
import com.salazar.cheers.chat.domain.models.ChatChannel
import com.salazar.cheers.chat.domain.usecase.pin_room.PinRoomUseCase
import com.salazar.cheers.core.data.internal.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MessagesUiState(
    val isLoading: Boolean,
    val isRefreshing: Boolean,
    val errorMessages: List<String>,
    val searchInput: String,
    val channels: List<ChatChannel>?,
    val suggestions: List<User>?,
)

private data class MessagesViewModelState(
    val channels: List<ChatChannel>? = null,
    val rooms: List<Room>? = null,
    val suggestions: List<User>? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
) {
    fun toUiState(): MessagesUiState =
        MessagesUiState(
            channels = channels,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            errorMessages = errorMessages,
            searchInput = searchInput,
            suggestions = suggestions,
        )
}

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val pinRoomUseCase: PinRoomUseCase,
) : ViewModel() {

    private val viewModelState =
        MutableStateFlow(MessagesViewModelState(isLoading = false))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        viewModelScope.launch {
            chatRepository.getChannels().collect(::updateRooms)
        }
    }

    private fun updateRooms(rooms: List<ChatChannel>) {
        viewModelState.update {
            it.copy(channels = rooms, isLoading = false)
        }
    }

    fun onSwipeRefresh() {
        viewModelScope.launch {
            chatRepository.getInbox()
        }
    }

    fun onSearchInputChange(searchInput: String) {
        viewModelState.update {
            it.copy(searchInput = searchInput)
        }
    }

    fun onFollowToggle(user: User) {
        viewModelScope.launch {
            userRepository.toggleFollow(user.id)
        }
    }

    fun onRoomPin(roomId: String) {
        viewModelScope.launch {
            pinRoomUseCase(roomId = roomId)
        }
    }
}

sealed class RoomsUIAction {
    object OnBackPressed : RoomsUIAction()
    object OnSwipeRefresh : RoomsUIAction()
    data class OnRoomClick(val roomId: String) : RoomsUIAction()
    data class OnCameraClick(val id: String) : RoomsUIAction()
    data class OnPinRoom(val roomId: String) : RoomsUIAction()
    data class OnRoomLongPress(val roomId: String) : RoomsUIAction()
    data class OnUserClick(val userId: String) : RoomsUIAction()
    data class OnSearchInputChange(val query: String) : RoomsUIAction()
    data class OnFollowToggle(val user: User) : RoomsUIAction()
}
