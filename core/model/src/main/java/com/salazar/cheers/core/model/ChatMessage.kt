package com.salazar.cheers.core.model

data class ChatMessage(
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
    val images: List<String> = emptyList(),
    val likedBy: List<String> = emptyList(),
    val seenBy: List<String> = emptyList(),
    val type: MessageType = MessageType.TEXT,
    val status: ChatMessageStatus = ChatMessageStatus.UNRECOGNIZED,
    val replyTo: ChatMessage? = null,
)

enum class ChatMessageStatus {
    EMPTY,
    SCHEDULED,
    SENT,
    DELIVERED,
    READ,
    FAILED,
    UNRECOGNIZED,
}

val EmptyChatMessage: ChatMessage = ChatMessage(
    id = "",
    roomId = "",
    text = "",
    photoUrl = "",
    createTime = 0,
    senderId = "",
    senderName = "",
    senderUsername = "",
    isSender = false,
    hasLiked = false,
    senderProfilePictureUrl = "",
    status = ChatMessageStatus.UNRECOGNIZED,
    type = MessageType.TEXT,
)
