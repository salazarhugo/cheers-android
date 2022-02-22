package com.salazar.cheers.internal

import com.google.firebase.Timestamp

object ChatChannelType {
    const val DIRECT = "DIRECT"
    const val GROUP = "GROUP"
}

data class ChatChannel(
    val id: String,
    val name: String,
    var members: List<String>,
    val otherUser: User,
    val createdAt: Timestamp,
    val createdBy: String,
    val recentMessage: TextMessage,
    val recentMessageTime: Timestamp,
    val type: String,
) {
    constructor() : this(
        "",
        "",
        listOf(),
        User(),
        Timestamp.now(),
        "",
        TextMessage(),
        Timestamp.now(),
        ChatChannelType.DIRECT
    )
}