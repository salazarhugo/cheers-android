package com.salazar.cheers.internal

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.protobuf.Timestamp
import com.salazar.cheers.MessageType


@Entity(tableName = "message")
data class ChatMessage(
    @PrimaryKey
    val id: String,
    val chatChannelId: String,
    val text: String,
    val photoUrl: String,
    val time: Timestamp,
    val senderId: String,
    val senderName: String,
    val senderUsername: String,
    val senderProfilePictureUrl: String,
    val likedBy: List<String> = emptyList(),
    val seenBy: List<String> = emptyList(),
    val acknowledged: Boolean = false,
    val type: MessageType = MessageType.TEXT,
)