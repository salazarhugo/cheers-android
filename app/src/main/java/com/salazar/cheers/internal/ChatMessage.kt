package com.salazar.cheers.internal

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "message")
data class ChatMessage(
    @PrimaryKey
    val id: String,
    val roomId: String,
    val text: String,
    val photoUrl: String,
    val createTime: Long,
    val senderId: String,
    val senderName: String,
    val senderUsername: String,
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