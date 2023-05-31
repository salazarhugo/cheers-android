package com.salazar.cheers.feature.chat.domain.models

interface Message {
    var id: String
    var chatChannelId: String
    val time: Int
    val senderId: String
    val acknowledged: Boolean
    val senderName: String
    val senderProfilePictureUrl: String
    val senderUsername: String
    val type: String
    val likedBy: List<String>
    val seenBy: List<String>
}
