package com.salazar.cheers.internal

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

object ChatChannelType {
    const val DIRECT = "DIRECT"
    const val GROUP = "GROUP"
}

@Entity(tableName = "channel")
data class ChatChannel(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    var members: List<String> = emptyList(),
    val otherUserId: String = "",
//    val otherUser: User = User(),
    val createdAt: Timestamp = Timestamp.now(),
    val createdBy: String = "",
    val recentMessageId: String = "",
//    val recentMessage: TextMessage,
    val recentMessageTime: Timestamp = Timestamp.now(),
    val type: String = "",
)