package com.salazar.cheers.internal

import com.google.firebase.Timestamp

object ChatChannelType {
    const val DIRECT = "DIRECT"
    const val GROUP = "GROUP"
}

data class ChatChannel(
    val id: String,
    val name: String,
    var members: MutableList<String>,
    val createdAt: Timestamp,
    val createdBy: String,
    val recentMessage: TextMessage,
    val type: String,
){
    constructor() : this("", "", mutableListOf(), Timestamp.now(), "", TextMessage(), ChatChannelType.DIRECT)
}