package com.salazar.cheers.feature.chat.ui.screens.messages

import cheers.chat.v1.Room
import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.data.chat.websocket.WebsocketState

data class MessagesViewModelState(
    val channels: List<ChatChannel>? = null,
    val rooms: List<Room>? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessages: List<String> = emptyList(),
    val searchInput: String = "",
    val websocketState: WebsocketState? = null,
) {
    fun toUiState(): MessagesUiState =
        MessagesUiState(
            channels = channels,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            errorMessages = errorMessages,
            searchInput = searchInput,
            websocketState = websocketState,
        )
}