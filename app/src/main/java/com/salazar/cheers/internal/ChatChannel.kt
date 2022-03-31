package com.salazar.cheers.internal

import com.google.firebase.Timestamp
import java.util.*

object ChatChannelType {
    const val DIRECT = "DIRECT"
    const val GROUP = "GROUP"
}

data class ChatChannel(
    val id: String = "",
    val name: String = "",
    val members: List<User> = emptyList(),
    val otherUserId: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val createdBy: String = "",
    val recentMessage: TextMessage? = null,
    val recentMessageTime: Date = Date(),
    val type: String = "",
)

data class ChatChannelResponse(
    val id: String = "",
    val name: String = "",
    var members: List<String> = emptyList(),
    val otherUserId: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val createdBy: String = "",
    val recentMessage: TextMessage? = null,
    val recentMessageTime: Date = Date(),
    val type: String = "",
)