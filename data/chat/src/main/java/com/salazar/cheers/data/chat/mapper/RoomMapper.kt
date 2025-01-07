package com.salazar.cheers.data.chat.mapper

import cheers.chat.v1.Room
import com.salazar.cheers.core.model.ChatChannel
import com.salazar.cheers.core.model.ChatStatus
import com.salazar.cheers.core.model.ChatType
import com.salazar.cheers.core.model.MessageType

fun Room.toChatChannel(): ChatChannel {
    return ChatChannel(
        id = id,
        name = name,
        verified = verified,
        picture = picture,
        admins = adminsList,
        admin = admin,
        createTime = createTime,
        lastMessage = lastMessageText,
        lastMessageTime = lastMessageTime,
        lastMessageType = MessageType.TEXT,
        status = status.toRoomStatus(),
        members = members10List,
        type = type.toRoomType(),
        ownerId = owner,
        unreadCount = unreadMessageCount.toInt(),
        membersCount = membersCount20.toInt(),
        otherUserId = otherUserId,
    )
}

fun cheers.chat.v1.RoomType.toRoomType(): ChatType {
    return when (this) {
        cheers.chat.v1.RoomType.DIRECT -> ChatType.DIRECT
        cheers.chat.v1.RoomType.GROUP -> ChatType.GROUP
        cheers.chat.v1.RoomType.UNRECOGNIZED -> ChatType.UNRECOGNIZED
    }
}

fun cheers.chat.v1.RoomStatus.toRoomStatus(): ChatStatus {
    return when (this) {
        cheers.chat.v1.RoomStatus.EMPTY -> ChatStatus.EMPTY
        cheers.chat.v1.RoomStatus.OPENED -> ChatStatus.OPENED
        cheers.chat.v1.RoomStatus.SENT -> ChatStatus.SENT
        cheers.chat.v1.RoomStatus.RECEIVED -> ChatStatus.RECEIVED
        cheers.chat.v1.RoomStatus.NEW -> ChatStatus.NEW
        cheers.chat.v1.RoomStatus.UNRECOGNIZED -> ChatStatus.UNRECOGNIZED
    }
}