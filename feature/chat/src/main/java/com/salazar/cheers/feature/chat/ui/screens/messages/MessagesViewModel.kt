package com.salazar.cheers.feature.chat.ui.screens.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.data.chat.repository.ChatRepository
import com.salazar.cheers.data.chat.websocket.ChatWebSocketManager
import com.salazar.cheers.data.chat.websocket.WebsocketState
import com.salazar.cheers.domain.list_chats.ListChatsUseCase
import com.salazar.cheers.domain.pin_room.PinRoomUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
        MutableStateFlow(MessagesViewModelState(isLoading = true))

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
        viewModelScope.launch {
            chatRepository.getInbox()
        }
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

    private fun updateIsRefreshing(isRefreshing: Boolean) {
        viewModelState.update {
            it.copy(isRefreshing = isRefreshing)
        }
    }

    fun onSwipeRefresh() {
        updateIsRefreshing(isRefreshing = true)
        viewModelScope.launch {
            chatRepository.getInbox()
            updateIsRefreshing(isRefreshing = false)
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