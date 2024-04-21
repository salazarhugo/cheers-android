package com.salazar.cheers.feature.chat.ui.screens.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.data.chat.repository.ChatRepository
import com.salazar.cheers.domain.pin_room.PinRoomUseCase
import com.salazar.cheers.data.chat.websocket.ChatWebSocketManager
import com.salazar.cheers.data.chat.websocket.WebsocketState
import com.salazar.cheers.domain.list_chats.ListChatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val webSocketManager: ChatWebSocketManager,
    private val chatRepository: ChatRepository,
    private val pinRoomUseCase: PinRoomUseCase,
    private val listChatsUseCase: ListChatsUseCase,
) : ViewModel() {

    private val viewModelState =
        MutableStateFlow(MessagesViewModelState(isLoading = false))

    val uiState = viewModelState
        .map(MessagesViewModelState::toUiState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        viewModelScope.launch {
            listChatsUseCase().collect(::updateRooms)
        }
        onSwipeRefresh()
        listenWebsocket()
    }

    private fun listenWebsocket() {
        viewModelScope.launch {
            webSocketManager.websocketState.collect(::updateWebsocketState)
        }
    }

    private fun updateWebsocketState(state: WebsocketState) {
        viewModelState.update {
            it.copy(websocketState = state)
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

    fun onRoomPin(roomId: String) {
        viewModelScope.launch {
            pinRoomUseCase(roomId = roomId)
        }
    }
}