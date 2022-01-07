package com.salazar.cheers.internal

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*


data class TextMessage(
    override var id: String,
    override var chatChannelId: String,
    var text: String,
    @ServerTimestamp
    override val time: Date? = null,
    override val senderId: String,
    override val senderName: String,
    override val senderUsername: String,
    override val senderProfilePictureUrl: String,
    override val likedBy: ArrayList<String>,
    override val seenBy: List<String>,
    override val type: String = MessageType.TEXT
) : Message, Serializable {

    constructor() : this(
        "",
        "",
        "",
        null,
        "",
        "",
        "",
        "",
        arrayListOf(),
        listOf(),
    )
}