package com.salazar.cheers.feature.chat.ui.screens.chat

import androidx.compose.ui.text.input.TextFieldValue
import com.salazar.cheers.data.chat.models.ChatChannel
import com.salazar.cheers.data.chat.models.ChatMessage

data class ChatUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val channel: ChatChannel? = null,
    val messages: List<ChatMessage> = emptyList(),
    val textState: TextFieldValue = TextFieldValue(),
    val replyMessage: ChatMessage? = null,
    val isRecording: Boolean = false,
    val isOtherUserPresent: Boolean = false,
)