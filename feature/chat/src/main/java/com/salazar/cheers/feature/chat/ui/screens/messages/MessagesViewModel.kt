package com.salazar.cheers.feature.chat.ui.screens.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cheers.chat.v1.Room
import com.salazar.cheers.feature.chat.domain.usecase.pin_room.PinRoomUseCase
import com.salazar.cheers.feature.chat.data.repository.ChatRepository
import com.salazar.cheers.feature.chat.domain.models.ChatChannel
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
)

private data class MessagesViewModelState(
    val channels: List<ChatChannel>? = null,
    val rooms: List<Room>? = null,
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
        )
}

@HiltViewModel
class MessagesViewModel @Inject constructor(
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
        onSwipeRefresh()
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
}
