package com.salazar.cheers.data.mapper

import cheers.chat.v1.Room
import com.salazar.cheers.internal.ChatChannel

fun Room.toChatChannel(accountId: String): ChatChannel {
    return ChatChannel(
        id = id,
        name = name,
        verified = verified,
        picture = picture,
        createTime = createTime,
        lastMessage = lastMessageText,
        lastMessageTime = lastMessageTime,
        lastMessageType= lastMessageType,
        accountId = accountId,
        status = status,
        members = membersList,
        type = type,
        ownerId = owner,
    )
}