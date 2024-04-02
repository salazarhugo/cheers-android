package com.salazar.cheers.feature.chat.ui.components.chat_item

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.salazar.cheers.core.ui.CheersPreview
import com.salazar.cheers.data.chat.models.ChatStatus
import com.salazar.cheers.data.chat.models.MessageType
import com.salazar.cheers.feature.chat.ui.components.ChatTypingComponent
import com.salazar.cheers.feature.chat.ui.components.DeliveredChat
import com.salazar.cheers.feature.chat.ui.components.EmptyChat
import com.salazar.cheers.feature.chat.ui.components.NewChat
import com.salazar.cheers.feature.chat.ui.components.OpenedChat
import com.salazar.cheers.feature.chat.ui.components.ReceivedChat

@Composable
fun ChatStatus(
    status: ChatStatus,
    isOtherUserTyping: Boolean = false,
    messageType: MessageType,
) {
    ChatTypingComponent(
        isTyping = isOtherUserTyping
    )
    if (isOtherUserTyping.not()) {
        when (status) {
            ChatStatus.NEW -> NewChat(messageType)
            ChatStatus.EMPTY -> EmptyChat()
            ChatStatus.OPENED -> OpenedChat()
            ChatStatus.SENT -> DeliveredChat(messageType)
            ChatStatus.RECEIVED -> ReceivedChat(messageType)
//                            RoomStatus.SENDING -> SendingChat()
            ChatStatus.UNRECOGNIZED -> {}
        }
    }
}

@Preview
@Composable
private fun ChatStatusPreview() {
    val statuses = ChatStatus.entries
    CheersPreview(
        modifier = Modifier.padding(16.dp),
        maxItemsInEachRow = 1,
    ) {
        statuses.forEach {
            ChatStatus(
                status = it,
                messageType = MessageType.TEXT,
            )
        }
    }
}