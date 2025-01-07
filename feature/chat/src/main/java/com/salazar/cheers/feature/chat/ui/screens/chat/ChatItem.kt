package com.salazar.cheers.feature.chat.ui.screens.chat

import OnMessageLongClickDialog
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.model.ChatMessage


@Composable
internal fun ChatItem(
    seen: Boolean,
    isFirstMessageByAuthor: Boolean,
    isLastMessageByAuthor: Boolean,
    index: Int,
    isGroup: Boolean,
    chatMessage: ChatMessage,
    onChatUIAction: (ChatUIAction) -> Unit,
    navigateToProfile: (String) -> Unit,
    onDoubleTapMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val offset = Offset.Zero

    Box(
        modifier = modifier,
    ) {
        ChatMessageComponent(
            modifier = Modifier,
            chatMessage = chatMessage,
            isGroup = isGroup,
            seen = index == 0 && seen,
            onAuthorClick = { name -> navigateToProfile(name) },
            onLongClickMessage = {
                expanded = true
            },
            onDoubleTapMessage = onDoubleTapMessage,
            isFirstMessageByAuthor = isFirstMessageByAuthor,
            isLastMessageByAuthor = isLastMessageByAuthor,
            onReply = {
                onChatUIAction(ChatUIAction.OnReplyMessage(chatMessage))
            },
        )

        OnMessageLongClickDialog(
            expanded = expanded,
            offset = DpOffset(offset.x.dp, offset.y.dp),
            msg = chatMessage,
            onDismiss = {
                expanded = false
            },
            onUnsendMessage = {
                onChatUIAction(ChatUIAction.OnUnSendMessage(it))
            },
            onCopyText = {
                onChatUIAction(ChatUIAction.OnCopyText(it))
            },
            onLike = {
                onChatUIAction(ChatUIAction.OnLikeClick(it))
            },
            onUnlike = {
                onChatUIAction(ChatUIAction.OnUnLikeClick(it))
            },
        )
    }
}