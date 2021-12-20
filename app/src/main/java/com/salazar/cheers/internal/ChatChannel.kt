package com.salazar.cheers.internal

import com.google.firebase.Timestamp

object ChatChannelType {
    const val DIRECT = "DIRECT"
    const val GROUP = "GROUP"
}

sealed interface ChatChannel2 {
    val id: String
    val name: String
    var members: MutableList<String>
    val createdAt: Timestamp
    val createdBy: String
    val recentMessage: TextMessage
    val type: String
}

data class ChatChannel(
    val id: String,
    val name: String,
    var members: List<String>,
    val otherUser: User,
    val createdAt: Timestamp,
    val createdBy: String,
    val recentMessage: TextMessage,
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
        ChatChannelType.DIRECT
    )
}