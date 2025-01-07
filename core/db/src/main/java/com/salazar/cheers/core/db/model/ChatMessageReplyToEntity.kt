package com.salazar.cheers.core.db.model

import com.salazar.cheers.core.model.ChatMessage
import com.salazar.cheers.core.model.ChatMessageStatus
import com.salazar.cheers.core.model.MessageType


data class ChatMessageReplyToEntity(
    val id: String,
    val roomId: String,
    val text: String,
    val photoUrl: String,
    // Unix milliseconds
    val createTime: Long,
    val senderId: String,
    val senderName: String,
    val senderUsername: String,
    val isSender: Boolean = false,
    val hasLiked: Boolean = false,
    val senderProfilePictureUrl: String,
    val likedBy: List<String> = emptyList(),
    val seenBy: List<String> = emptyList(),
    val type: MessageType = MessageType.TEXT,
    val status: ChatMessageStatus = ChatMessageStatus.UNRECOGNIZED,
)

fun ChatMessage?.toChatMessageEntity(): ChatMessageReplyToEntity? {
    if (this == null) return null

    return ChatMessageReplyToEntity(
        id = id,
        roomId = roomId,
        text = text,
        photoUrl = photoUrl,
        createTime = createTime,
        senderId = senderId,
        senderName = senderName,
        senderUsername = senderUsername,
        isSender = isSender,
        hasLiked = hasLiked,
        senderProfilePictureUrl = senderProfilePictureUrl,
        status = status,
        type = type,
        seenBy = seenBy,
        likedBy = likedBy,
    )
}

fun ChatMessageReplyToEntity?.toChatMessage(): ChatMessage? {
    if (this == null) return null

    return ChatMessage(
        id = id,
        roomId = roomId,
        text = text,
        photoUrl = photoUrl,
        createTime = createTime,
        senderId = senderId,
        senderName = senderName,
        senderUsername = senderUsername,
        isSender = isSender,
        hasLiked = hasLiked,
        senderProfilePictureUrl = senderProfilePictureUrl,
        status = status,
        type = type,
        seenBy = seenBy,
        likedBy = likedBy,
    )
}
