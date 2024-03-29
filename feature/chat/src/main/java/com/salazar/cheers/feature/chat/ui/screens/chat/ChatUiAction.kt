package com.salazar.cheers.feature.chat.ui.screens.chat

import androidx.compose.ui.text.input.TextFieldValue
import com.salazar.cheers.data.chat.models.ChatMessage

sealed class ChatUIAction {
    data object OnBackPressed : ChatUIAction()
    data object OnSwipeRefresh : ChatUIAction()
    data object OnImageSelectorClick : ChatUIAction()
    data class OnTextInputChange(val text: TextFieldValue) : ChatUIAction()
    data class OnCopyText(val text: String) : ChatUIAction()
    data class OnRoomInfoClick(val roomId: String) : ChatUIAction()
    data class OnUserClick(val userId: String) : ChatUIAction()
    data class OnLikeClick(val messageId: String) : ChatUIAction()
    data class OnUnLikeClick(val messageId: String) : ChatUIAction()
    data class OnReplyMessage(val message: ChatMessage?) : ChatUIAction()
    data class OnUnSendMessage(val messageId: String) : ChatUIAction()
    data class OnSendTextMessage(val text: String) : ChatUIAction()
}
