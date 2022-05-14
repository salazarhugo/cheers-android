package com.salazar.cheers.internal

import com.google.protobuf.Timestamp


interface Message {
    var id: String
    var chatChannelId: String
    val time: Timestamp
    val senderId: String
    val acknowledged: Boolean
    val senderName: String
    val senderProfilePictureUrl: String
    val senderUsername: String
    val type: String
    val likedBy: List<String>
    val seenBy: List<String>
}