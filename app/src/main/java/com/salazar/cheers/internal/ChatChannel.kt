package com.salazar.cheers.internal

import androidx.room.Entity
import androidx.room.PrimaryKey
import cheers.chat.v1.MessageType
import cheers.chat.v1.RoomStatus
import cheers.chat.v1.RoomType

@Entity(tableName = "room")
data class ChatChannel(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val verified: Boolean = false,
    val members: List<String> = emptyList(),
    val otherUserId: String = "",
    val createTime: Long = 0,
    val createTimeBy: String = "",
    val picture: String? = null,
    val lastMessage: String = "",
    val lastMessageTime: Long = 0,
    val lastMessageType: MessageType = MessageType.TEXT,
    val type: RoomType = RoomType.DIRECT,
    val status: RoomStatus = RoomStatus.UNRECOGNIZED,
    val accountId: String = "",
    val ownerId: String = "",
)