package com.salazar.cheers.internal

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*


@Entity(
    tableName = "message",
    foreignKeys = [
        ForeignKey(
            entity = ChatChannel::class,
            parentColumns = ["id"],
            childColumns = ["chatChannelId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ], indices = [
//        Index("senderId"),
        Index("chatChannelId")
    ]
)
data class TextMessage(
    @PrimaryKey
    override var id: String,
    override var chatChannelId: String,
    var text: String,
    @ServerTimestamp
    override val time: Date? = Date(),
    override val senderId: String,
    override val senderName: String,
    override val senderUsername: String,
    override val senderProfilePictureUrl: String,
    override val likedBy: List<String>,
    override val seenBy: List<String>,
    override val type: String = MessageType.TEXT
) : Message, Serializable {

    constructor() : this(
        "",
        "",
        "",
        Date(),
        "",
        "",
        "",
        "",
        listOf(),
        listOf(),
    )
}