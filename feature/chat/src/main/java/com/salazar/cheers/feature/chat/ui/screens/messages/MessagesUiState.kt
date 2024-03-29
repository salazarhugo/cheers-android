package com.salazar.cheers.feature.chat.ui.screens.messages

import com.salazar.cheers.data.chat.models.ChatChannel
import com.salazar.cheers.data.chat.websocket.WebsocketState

data class MessagesUiState(
    val isLoading: Boolean,
    val isRefreshing: Boolean,
    val errorMessages: List<String>,
    val searchInput: String,
    val channels: List<ChatChannel>?,
    val websocketState: WebsocketState?,
)
