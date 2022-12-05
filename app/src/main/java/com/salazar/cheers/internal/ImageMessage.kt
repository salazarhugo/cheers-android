package com.salazar.cheers.internal

import com.google.protobuf.Timestamp

data class ImageMessage(
    val imagesDownloadUrl: List<String>,
    override var id: String,
    override var chatChannelId: String,
    override val time: Int,
    override val senderId: String,
    override val senderName: String,
    override val senderProfilePictureUrl: String,
    override val senderUsername: String,
    override val likedBy: List<String>,
    override val seenBy: List<String>,
    override val acknowledged: Boolean,
    override val type: String = "IMAGE",
) : Message {

    constructor() : this(
        listOf(),
        "",
        "",
        0,
        "",
        "",
        "",
        "",
        arrayListOf(),
        listOf(),
        false,
    )
}