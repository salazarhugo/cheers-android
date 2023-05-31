package com.salazar.cheers.feature.chat.data.mapper

import cheers.chat.v1.Message
import com.salazar.cheers.feature.chat.domain.models.ChatMessage
import com.salazar.cheers.feature.chat.domain.models.ChatMessageStatus
import com.salazar.cheers.feature.chat.domain.models.ImageMessage
import com.salazar.cheers.feature.chat.domain.models.MessageType

fun Message.toTextMessage(): ChatMessage {
    return ChatMessage(
        id = id,
        text = text,
        senderName = senderName,
        senderId = senderId,
        senderUsername = senderUsername,
        senderProfilePictureUrl = senderPicture,
        createTime = createTime,
        roomId = roomId,
        photoUrl = picture,
        type = MessageType.TEXT,
        status = status.toChatMessageStatus(),
    )
}

fun Message.Status.toChatMessageStatus(): ChatMessageStatus {
    return when(this) {
        Message.Status.DELIVERED -> ChatMessageStatus.DELIVERED
        Message.Status.EMPTY -> ChatMessageStatus.EMPTY
        Message.Status.SENT -> ChatMessageStatus.SENT
        Message.Status.READ -> ChatMessageStatus.READ
        Message.Status.FAILED -> ChatMessageStatus.FAILED
        Message.Status.UNRECOGNIZED -> ChatMessageStatus.UNRECOGNIZED
    }
}

fun Message.toImageMessage(): ImageMessage {
    return ImageMessage().copy(
        id = id,
        senderName = senderName,
        senderId = senderId,
        senderUsername = senderUsername,
        senderProfilePictureUrl = senderPicture,
        time = 0,
        chatChannelId = roomId,
        imagesDownloadUrl = listOf(picture)
    )
}
