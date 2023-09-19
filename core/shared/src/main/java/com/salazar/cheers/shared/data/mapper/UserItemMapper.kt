package com.salazar.cheers.shared.data.mapper

import cheers.type.UserOuterClass
import cheers.type.UserOuterClass.UserItem


fun UserItem.toUserItem(): com.salazar.cheers.core.model.UserItem {
    return com.salazar.cheers.core.model.UserItem(
        id = id,
        name = name,
        picture = picture,
        has_followed = hasFollowed,
        username = username,
        verified = verified,
        story_state = com.salazar.cheers.core.model.StoryState.EMPTY,
        friend = friend,
        requested = requested,
    )
}

fun UserOuterClass.User.toUserItem(): com.salazar.cheers.core.model.UserItem {
    return com.salazar.cheers.core.model.UserItem(
        id = id,
        name = name,
        picture = picture,
        username = username,
        verified = verified,
        story_state = com.salazar.cheers.core.model.StoryState.EMPTY,
        requested = false,
        has_followed = false,
        friend = true,
    )
}
