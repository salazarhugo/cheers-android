package com.salazar.cheers.shared.data.mapper

import cheers.type.UserOuterClass
import cheers.type.UserOuterClass.UserItem
import com.salazar.cheers.core.model.User


fun User.toUserItem(): com.salazar.cheers.core.model.UserItem {
    return com.salazar.cheers.core.model.UserItem(
        id = id,
        name = name,
        picture = picture,
        hasFollowed = followBack,
        username = username,
        verified = verified,
        premium = premium,
        friend = friend,
        requested = requested,
    )
}

fun UserItem.toUserItem(): com.salazar.cheers.core.model.UserItem {
    return com.salazar.cheers.core.model.UserItem(
        id = id,
        name = name,
        picture = picture,
        hasFollowed = hasFollowed,
        username = username,
        verified = verified,
        premium = premium,
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
        premium = premium,
        requested = false,
        hasFollowed = false,
        friend = true,
    )
}
