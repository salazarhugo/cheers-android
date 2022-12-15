package com.salazar.cheers.data.mapper

import cheers.type.UserOuterClass.UserItem
import com.salazar.cheers.data.enums.StoryState


fun UserItem.toUserItem(): com.salazar.cheers.data.db.entities.UserItem {
    return com.salazar.cheers.data.db.entities.UserItem(
        id = id,
        name = name,
        picture = picture,
        has_followed = hasFollowed,
        username = username,
        verified = verified,
        story_state = StoryState.EMPTY,
    )
}