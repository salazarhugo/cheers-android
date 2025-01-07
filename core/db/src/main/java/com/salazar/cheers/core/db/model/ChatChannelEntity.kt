package com.salazar.cheers.core.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.core.model.ChatStatus
import com.salazar.cheers.core.model.ChatType
import com.salazar.cheers.core.model.MessageType

@Entity(tableName = "room")
data class ChatChannelEntity(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val verified: Boolean = false,
    val members: List<String> = emptyList(),
    val admins: List<String> = emptyList(),
    val admin: Boolean = false,
    val otherUserId: String = "",
    val isOtherUserTyping: Boolean = false,
    val isOtherUserPresent: Boolean = false,
    val createTime: Long = 0,
    val createTimeBy: String = "",
    val picture: String? = null,
    val lastMessage: String = "",
    val lastMessageTime: Long = 0,
    val lastMessageType: MessageType = MessageType.TEXT,
    val type: ChatType = ChatType.DIRECT,
    val status: ChatStatus = ChatStatus.UNRECOGNIZED,
    val ownerId: String = "",
    val pinned: Boolean = false,
    val unreadCount: Int = 0,
    val membersCount: Int = 0,
)

fun ChatChannelEntity.asExternalModel() = ChatChannel(
    id = id,
    createTime = createTime,
    type = type,
    status = status,
    admin = admin,
    admins = admins,
    createTimeBy = createTimeBy,
    isOtherUserPresent = isOtherUserPresent,
    isOtherUserTyping = isOtherUserTyping,
    lastMessage = lastMessage,
    lastMessageTime = lastMessageTime,
    lastMessageType = lastMessageType,
    members = members,
    name = name,
    otherUserId = otherUserId,
    ownerId = ownerId,
    picture = picture,
    pinned = pinned,
    unreadCount = unreadCount,
    verified = verified,
    membersCount = membersCount,
)

fun ChatChannel.asEntity(): ChatChannelEntity =
    ChatChannelEntity(
        id = id,
        createTime = createTime,
        type = type,
        status = status,
        admin = admin,
        admins = admins,
        createTimeBy = createTimeBy,
        isOtherUserPresent = isOtherUserPresent,
        isOtherUserTyping = isOtherUserTyping,
        lastMessage = lastMessage,
        lastMessageTime = lastMessageTime,
        lastMessageType = lastMessageType,
        members = members,
        name = name,
        otherUserId = otherUserId,
        ownerId = ownerId,
        picture = picture,
        pinned = pinned,
        unreadCount = unreadCount,
        verified = verified,
        membersCount = membersCount,
    )

fun List<ChatChannelEntity>.asExternalModel() = this.map { it.asExternalModel() }

fun List<ChatChannel>.asEntity() = this.map { it.asEntity() }
