package com.salazar.cheers.data.mapper

import com.salazar.cheers.data.entities.RecentUser
import com.salazar.cheers.internal.User

fun RecentUser.toUser(): User {
    return User().copy(
        id = id,
        name = fullName,
        username = username,
        profilePictureUrl = profilePictureUrl,
        verified = verified,
    )
}