package com.salazar.cheers.data.chat.models

import java.util.Date
import java.util.UUID

val mockMessage1 = ChatMessage(
    id = UUID.randomUUID().toString(),
    senderUsername = "hugo",
    status = ChatMessageStatus.DELIVERED,
    senderProfilePictureUrl = "",
    senderName = "",
    seenBy = emptyList(),
    likedBy = emptyList(),
    photoUrl = "",
    isSender = true,
    type = MessageType.TEXT,
    hasLiked = false,
    createTime = Date().time,
    roomId = "",
    senderId = "",
    text = "Hello there!",
)
