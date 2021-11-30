package com.salazar.cheers.internal

import java.util.*


data class TextMessage(
    override var id: String,
    override var chatChannelId: String,
    var text: String,
    override val time: Date,
    override var senderId: String,
    override val recipientId: String,
    override val senderName: String,
    override val senderUsername: String,
    override val authorImage: String,
    override val likedBy: ArrayList<String>,
    override val seenBy: ArrayList<String>,
    override val type: String = MessageType.TEXT
) : Message {

    constructor(text: String = "", senderId: String = "") : this(
        "",
        "",
        "",
        Date(0), "",
        "",
        "",
        "",
        "",
        arrayListOf(),
        arrayListOf()
    )
    {
        this.text = text
        this.chatChannelId = text
        this.senderId = senderId
    }
}