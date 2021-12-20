package com.salazar.cheers.internal

import java.util.*


object MessageType {
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
}

interface Message {
    var id: String
    var chatChannelId: String
    val time: Date?
    val senderId: String
    val recipientId: String
    val senderName: String
    val senderProfilePicturePath: String
    val senderUsername: String
    val type: String
    val likedBy: ArrayList<String>
    val seenBy: ArrayList<String>
}