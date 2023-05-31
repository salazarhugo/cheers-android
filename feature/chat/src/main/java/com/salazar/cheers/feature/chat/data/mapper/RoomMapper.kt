package com.salazar.cheers.feature.chat.data.mapper

import cheers.chat.v1.Room
import com.salazar.cheers.feature.chat.domain.models.ChatChannel
import com.salazar.cheers.feature.chat.domain.models.MessageType
import com.salazar.cheers.feature.chat.domain.models.RoomStatus
import com.salazar.cheers.feature.chat.domain.models.RoomType

fun Room.toChatChannel(): ChatChannel {
    return ChatChannel(
        id = id,
        name = name,
        verified = verified,
        picture = picture,
        admins = adminsList,
        createTime = createTime,
        lastMessage = lastMessageText,
        lastMessageTime = lastMessageTime,
        lastMessageType = MessageType.TEXT,
        status = status.toRoomStatus(),
        members = membersList,
        type = type.toRoomType(),
        ownerId = owner,
    )
}

fun cheers.chat.v1.RoomType.toRoomType(): RoomType {
    return when (this) {
        cheers.chat.v1.RoomType.DIRECT -> RoomType.DIRECT
        cheers.chat.v1.RoomType.GROUP -> RoomType.GROUP
        cheers.chat.v1.RoomType.UNRECOGNIZED -> RoomType.UNRECOGNIZED
    }
}

fun cheers.chat.v1.RoomStatus.toRoomStatus(): RoomStatus {
    return when (this) {
        cheers.chat.v1.RoomStatus.EMPTY -> RoomStatus.EMPTY
        cheers.chat.v1.RoomStatus.OPENED -> RoomStatus.OPENED
        cheers.chat.v1.RoomStatus.SENT -> RoomStatus.SENT
        cheers.chat.v1.RoomStatus.RECEIVED -> RoomStatus.RECEIVED
        cheers.chat.v1.RoomStatus.NEW -> RoomStatus.NEW
        cheers.chat.v1.RoomStatus.UNRECOGNIZED -> RoomStatus.UNRECOGNIZED
    }
}