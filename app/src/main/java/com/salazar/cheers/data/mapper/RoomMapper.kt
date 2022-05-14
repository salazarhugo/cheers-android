package com.salazar.cheers.data.mapper

import com.google.firebase.auth.FirebaseAuth
import com.salazar.cheers.Room
import com.salazar.cheers.internal.ChatChannel

fun Room.toChatChannel(): ChatChannel {
    return ChatChannel(
        id = id,
        name = name,
        verified = verified,
        avatarUrl = profilePictureUrl,
        createdAt = created,
        recentMessage = lastMessageText,
        recentMessageTime = lastMessageTime,
        recentMessageType = lastMessageType,
        accountId = FirebaseAuth.getInstance().currentUser?.uid!!,
        status = status,
        members = membersList,
        type = type,
        ownerId = owner,
    )
}