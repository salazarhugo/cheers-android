package com.salazar.cheers.internal

import androidx.room.Entity
import androidx.room.PrimaryKey
import cheers.chat.v1.Message.*
import cheers.chat.v1.MessageType


@Entity(tableName = "message")
data class ChatMessage(
    @PrimaryKey
    val id: String,
    val chatChannelId: String,
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
    val status: Status = Status.UNRECOGNIZED,
)