package com.salazar.cheers.feature.chat.ui.screens.chat

import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.core.model.ChatMessage

data class ChatUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val channel: ChatChannel? = null,
    val messages: List<ChatMessage> = emptyList(),
    val textState: TextFieldValue = TextFieldValue(),
    val replyMessage: ChatMessage? = null,
    val images: List<Uri> = emptyList(),
    val isRecording: Boolean = false,
    val isOtherUserPresent: Boolean = false,
    val isLoadingMore: Boolean = false,
)