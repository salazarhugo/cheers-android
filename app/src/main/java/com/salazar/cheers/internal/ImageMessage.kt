package com.salazar.cheers.internal

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class ImageMessage(
    val imagesPath: List<String>,
    override var id: String,
    override var chatChannelId: String,
    @ServerTimestamp
    override val time: Date? = null,
    override val senderId: String,
    override val recipientId: String,
    override val senderName: String,
    override val senderProfilePicturePath: String,
    override val senderUsername: String,
    override val likedBy: ArrayList<String>,
    override val seenBy: ArrayList<String>,
    override val type: String = MessageType.IMAGE
) : Message {

    constructor() : this(
        listOf(),
        "",
        "",
        null,
        "",
        "",
        "",
        "",
        "",
        arrayListOf(),
        arrayListOf()
    )
}