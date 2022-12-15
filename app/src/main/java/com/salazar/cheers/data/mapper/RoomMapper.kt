package com.salazar.cheers.data.mapper

import cheers.chat.v1.Room
import com.salazar.cheers.internal.ChatChannel
import com.salazar.cheers.internal.MessageType
import com.salazar.cheers.internal.RoomStatus
import com.salazar.cheers.internal.RoomType

fun Room.toChatChannel(accountId: String): ChatChannel {
    return ChatChannel(
        id = id,
        name = name,
        verified = verified,
        picture = picture,
        admins = adminsList,
        createTime = createTime,
        lastMessage = lastMessageText,
        lastMessageTime = lastMessageTime,
        lastMessageType= MessageType.TEXT,
        accountId = accountId,
        status = RoomStatus.SENT,
        members = membersList,
        type = RoomType.DIRECT,
        ownerId = owner,
    )
}