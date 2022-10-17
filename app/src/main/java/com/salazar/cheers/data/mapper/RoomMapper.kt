package com.salazar.cheers.data.mapper

import cheers.chat.v1.Room
import com.salazar.cheers.internal.ChatChannel

fun Room.toChatChannel(accountId: String): ChatChannel {
    return ChatChannel(
        id = id,
        name = name,
        verified = verified,
        picture = profilePictureUrl,
        createdAt = created,
        recentMessage = lastMessageText,
        recentMessageTime = lastMessageTime,
        recentMessageType = lastMessageType,
        accountId = accountId,
        status = status,
        members = membersList,
        type = type,
        ownerId = owner,
    )
}