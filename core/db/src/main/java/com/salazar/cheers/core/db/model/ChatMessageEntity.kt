package com.salazar.cheers.core.db.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.salazar.cheers.core.model.ChatMessage
import com.salazar.cheers.core.model.ChatMessageStatus
import com.salazar.cheers.core.model.MessageType


@Entity(tableName = "message")
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val roomId: String,
    val text: String,
    val photoUrl: String,
    // Unix milliseconds
    val createTime: Long,
    val senderId: String,
    val senderName: String,
    val senderUsername: String,
    val isSender: Boolean = false,
    val hasLiked: Boolean = false,
    val senderProfilePictureUrl: String,
    val images: List<String> = emptyList(),
    val likedBy: List<String> = emptyList(),
    val seenBy: List<String> = emptyList(),
    val type: MessageType = MessageType.TEXT,
    val status: ChatMessageStatus = ChatMessageStatus.UNRECOGNIZED,
    @Embedded(prefix = "replyTo_")
    val replyToEntity: ChatMessageReplyToEntity? = null,
)

fun ChatMessageEntity.asExternalModel(): ChatMessage {
    return ChatMessage(
        id = id,
        roomId = roomId,
        text = text,
        photoUrl = photoUrl,
        createTime = createTime,
        senderId = senderId,
        senderName = senderName,
        senderUsername = senderUsername,
        isSender = isSender,
        hasLiked = hasLiked,
        senderProfilePictureUrl = senderProfilePictureUrl,
        status = status,
        type = type,
        seenBy = seenBy,
        likedBy = likedBy,
        replyTo = replyToEntity.toChatMessage(),
        images = images,
    )
}

fun ChatMessage.asEntity(): ChatMessageEntity =
    ChatMessageEntity(
        id = id,
        roomId = roomId,
        text = text,
        photoUrl = photoUrl,
        createTime = createTime,
        senderId = senderId,
        senderName = senderName,
        senderUsername = senderUsername,
        isSender = isSender,
        hasLiked = hasLiked,
        senderProfilePictureUrl = senderProfilePictureUrl,
        status = status,
        type = type,
        seenBy = seenBy,
        likedBy = likedBy,
        replyToEntity = replyTo.toChatMessageEntity(),
        images = images,
    )

fun List<ChatMessageEntity>.asExternalModel() = this.map { it.asExternalModel() }

fun List<ChatMessage>.asEntity() = this.map { it.asEntity() }
