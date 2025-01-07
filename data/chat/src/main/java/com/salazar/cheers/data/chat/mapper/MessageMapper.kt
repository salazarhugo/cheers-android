package com.salazar.cheers.data.chat.mapper

import cheers.chat.v1.Message
import cheers.chat.v1.MessageItem
import com.salazar.cheers.core.model.ChatMessage
import com.salazar.cheers.core.model.ChatMessageStatus
import com.salazar.cheers.core.model.MessageType
import com.salazar.cheers.data.chat.models.ImageMessage

fun MessageItem.toTextMessage(): ChatMessage {
    return message.toTextMessage().copy(
        isSender = sender,
        hasLiked = liked,
    )
}

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
        replyTo = if (hasReplyTo()) replyTo.toTextMessage() else null,
        images = mediasList.mapNotNull { it.imageVersionsList.firstOrNull()?.url },
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
