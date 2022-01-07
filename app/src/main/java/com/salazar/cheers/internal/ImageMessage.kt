package com.salazar.cheers.internal

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class ImageMessage(
    val imagesDownloadUrl: List<String>,
    override var id: String,
    override var chatChannelId: String,
    @ServerTimestamp
    override val time: Date? = null,
    override val senderId: String,
    override val senderName: String,
    override val senderProfilePictureUrl: String,
    override val senderUsername: String,
    override val likedBy: ArrayList<String>,
    override val seenBy: List<String>,
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
        arrayListOf(),
        listOf(),
    )
}