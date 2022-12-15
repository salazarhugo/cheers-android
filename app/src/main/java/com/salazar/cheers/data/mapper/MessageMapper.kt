package com.salazar.cheers.data.mapper

import cheers.chat.v1.Message
import com.salazar.cheers.internal.ChatMessage
import com.salazar.cheers.internal.ChatMessageStatus
import com.salazar.cheers.internal.ImageMessage
import com.salazar.cheers.internal.MessageType

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
        status = ChatMessageStatus.READ,
    )
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
