package com.salazar.cheers.data.mapper

import cheers.chat.v1.Message
import com.salazar.cheers.internal.ChatMessage
import com.salazar.cheers.internal.ImageMessage

fun Message.toTextMessage(): ChatMessage {
    return ChatMessage(
        id = id,
        text = message,
        senderName = senderName,
        senderId = sender,
        senderUsername = senderUsername,
        senderProfilePictureUrl = senderpicture,
        time = created,
        chatChannelId = room.id,
        photoUrl = photoUrl,
        type = type,
        status = status,
    )
}

fun Message.toImageMessage(): ImageMessage {
    return ImageMessage().copy(
        id = id,
        senderName = senderName,
        senderId = sender,
        senderUsername = senderUsername,
        senderProfilePictureUrl = senderpicture,
        time = created,
        chatChannelId = room.id,
        imagesDownloadUrl = listOf(photoUrl)
    )
}
