package com.salazar.cheers.internal

import java.util.*

data class ImageMessage(
    val imagePath: String,
    override var id: String,
    override var chatChannelId: String,
    override val time: Date,
    override val senderId: String,
    override val recipientId: String,
    override val senderName: String,
    override val authorImage: String,
    override val senderUsername: String,
    override val likedBy: ArrayList<String>,
    override val seenBy: ArrayList<String>,
    override val type: String = MessageType.IMAGE
) : Message {

    constructor() : this(
        "",
        "",
        "",
        Date(0),
        "",
        "",
        "",
        "",
        "",
        arrayListOf(),
        arrayListOf()
    )
}