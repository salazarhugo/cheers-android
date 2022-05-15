package com.salazar.cheers.internal

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.protobuf.Timestamp
import com.salazar.cheers.MessageType
import com.salazar.cheers.RoomStatus
import com.salazar.cheers.RoomType

@Entity(tableName = "room")
data class ChatChannel(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val username: String = "",
    val verified: Boolean = false,
    val members: List<String> = emptyList(),
    val otherUserId: String = "",
    val createdAt: Timestamp = Timestamp.getDefaultInstance(),
    val createdBy: String = "",
    val avatarUrl: String = "",
    val recentMessage: String = "",
    val status: RoomStatus = RoomStatus.UNRECOGNIZED,
    val recentMessageTime: Timestamp = Timestamp.getDefaultInstance(),
    val recentMessageType: MessageType = MessageType.TEXT,
    val type: RoomType = RoomType.DIRECT,
    val accountId: String = "",
    val ownerId: String = "",
)