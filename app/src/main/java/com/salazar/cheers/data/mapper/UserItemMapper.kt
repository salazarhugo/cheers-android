package com.salazar.cheers.data.mapper

import cheers.type.UserOuterClass.UserItem
import com.salazar.cheers.core.data.enums.StoryState


fun UserItem.toUserItem(): com.salazar.cheers.data.db.entities.UserItem {
    return com.salazar.cheers.data.db.entities.UserItem(
        id = id,
        name = name,
        picture = picture,
        has_followed = hasFollowed,
        username = username,
        verified = verified,
        story_state = StoryState.EMPTY,
        friend = friend,
        requested = requested,
    )
}

fun cheers.type.UserOuterClass.User.toUserItem(): com.salazar.cheers.data.db.entities.UserItem {
    return com.salazar.cheers.data.db.entities.UserItem(
        id = id,
        name = name,
        picture = picture,
        username = username,
        verified = verified,
        story_state = StoryState.EMPTY,
        requested = false,
        has_followed = false,
        friend = true,
    )
}
