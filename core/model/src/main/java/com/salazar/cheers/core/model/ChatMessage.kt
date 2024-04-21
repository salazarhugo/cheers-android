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
    val likedBy: List<String> = emptyList(),
    val seenBy: List<String> = emptyList(),
    val type: MessageType = MessageType.TEXT,
    val status: ChatMessageStatus = ChatMessageStatus.UNRECOGNIZED,
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